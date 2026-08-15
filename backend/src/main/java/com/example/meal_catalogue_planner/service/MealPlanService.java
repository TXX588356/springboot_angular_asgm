package com.example.meal_catalogue_planner.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.meal_catalogue_planner.dto.MealPlanItemRequest;
import com.example.meal_catalogue_planner.dto.MealPlanItemResponse;
import com.example.meal_catalogue_planner.dto.MealPlanRequest;
import com.example.meal_catalogue_planner.dto.MealPlanResponse;
import com.example.meal_catalogue_planner.entity.FoodItem;
import com.example.meal_catalogue_planner.entity.MealPlan;
import com.example.meal_catalogue_planner.entity.MealPlanItem;
import com.example.meal_catalogue_planner.entity.MealType;
import com.example.meal_catalogue_planner.entity.UserAccount;
import com.example.meal_catalogue_planner.repository.FoodItemRepository;
import com.example.meal_catalogue_planner.repository.MealPlanRepository;
import com.example.meal_catalogue_planner.repository.UserAccountRepository;

@Service
public class MealPlanService {
    private final MealPlanRepository mealPlanRepository;
    private final FoodItemRepository foodItemRepository;
    private final UserAccountRepository userAccountRepository;

    public MealPlanService(
        MealPlanRepository mealPlanRepository,
        FoodItemRepository foodItemRepository,
        UserAccountRepository userAccountRepository
    ) {
        this.mealPlanRepository = mealPlanRepository;
        this.foodItemRepository = foodItemRepository;
        this.userAccountRepository = userAccountRepository;
    }

    public List<MealPlanResponse> getMealPlans(String userEmail, LocalDate mealDate, MealType mealType) {
        List<MealPlan> mealPlans;

        // Always include userEmail so one user cannot read another user's meal plans.
        if (mealDate != null && mealType != null) {
            mealPlans = mealPlanRepository.findByUserAccountEmailIgnoreCaseAndMealDateAndMealType(userEmail, mealDate, mealType);
        } else if (mealDate != null) {
            mealPlans = mealPlanRepository.findByUserAccountEmailIgnoreCaseAndMealDate(userEmail, mealDate);
        } else if (mealType != null) {
            mealPlans = mealPlanRepository.findByUserAccountEmailIgnoreCaseAndMealType(userEmail, mealType);
        } else {
            mealPlans = mealPlanRepository.findByUserAccountEmailIgnoreCase(userEmail);
        }

        return mealPlans.stream()
            .map(this::toResponse)
            .toList();
    }

    public MealPlanResponse getMealPlanById(String userEmail, Long id) {
        MealPlan mealPlan = findMealPlanEntity(userEmail, id);
        return toResponse(mealPlan);
    }

    public MealPlanResponse createMealPlan(String userEmail, MealPlanRequest request) {
        MealPlan mealPlan = new MealPlan();
        mealPlan.setUserAccount(findUserAccount(userEmail));

        mealPlan.setName(request.name());
        mealPlan.setMealDate(request.mealDate());
        mealPlan.setMealType(request.mealType());
        mealPlan.setNotes(request.notes());

        // Build child rows from food IDs instead of accepting nested JPA entities from the client.
        replaceItems(mealPlan, request.items());

        MealPlan savedMealPlan = mealPlanRepository.save(mealPlan);
        return toResponse(savedMealPlan);
    }

    public MealPlanResponse updateMealPlan(String userEmail, Long id, MealPlanRequest request) {
        MealPlan mealPlan = findMealPlanEntity(userEmail, id);

        mealPlan.setName(request.name());
        mealPlan.setMealDate(request.mealDate());
        mealPlan.setMealType(request.mealType());
        mealPlan.setNotes(request.notes());

        // Clearing the collection works with orphanRemoval so old item rows are deleted on save.
        mealPlan.getItems().clear();
        replaceItems(mealPlan, request.items());

        MealPlan savedMealPlan = mealPlanRepository.save(mealPlan);
        return toResponse(savedMealPlan);
    }

    public void deleteMealPlan(String userEmail, Long id) {
        MealPlan mealPlan = findMealPlanEntity(userEmail, id);
        mealPlanRepository.delete(mealPlan);
    }

    private MealPlan findMealPlanEntity(String userEmail, Long id) {
        return mealPlanRepository.findByIdAndUserAccountEmailIgnoreCase(id, userEmail)
        .orElseThrow(() -> new ResponseStatusException(
            HttpStatus.NOT_FOUND,
            "Meal plan not found with id: " + id
        ));
    }

    private UserAccount findUserAccount(String userEmail) {
        return userAccountRepository.findByEmailIgnoreCase(userEmail)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.UNAUTHORIZED,
                "Authenticated user was not found"
            ));
    }

    private FoodItem findFoodEntity(Long id) {
        return foodItemRepository.findById(id)
        .orElseThrow(() -> new ResponseStatusException(
            HttpStatus.NOT_FOUND,
            "Food item not found with id: " + id
        ));
    }

    private void replaceItems(
        MealPlan mealPlan,
        List<MealPlanItemRequest> itemRequests
    ) {
        for (MealPlanItemRequest itemRequest : itemRequests) {
            // Validate each referenced food ID and attach the managed FoodItem entity.
            FoodItem foodItem = findFoodEntity(itemRequest.foodItemId());

            MealPlanItem item = new MealPlanItem();
            item.setMealPlan(mealPlan);
            item.setFoodItem(foodItem);
            item.setQuantity(itemRequest.quantity());

            mealPlan.getItems().add(item);
        }
    }

    private MealPlanResponse toResponse(MealPlan mealPlan) {
        // Response DTOs avoid circular JSON from MealPlan -> items -> mealPlan.
        List<MealPlanItemResponse> itemResponses = mealPlan.getItems()
            .stream()
            .map(this::toItemResponse)
            .toList();

        // Total calories is calculated from line subtotals instead of stored separately.
        BigDecimal totalCalories = itemResponses.stream()
            .map(MealPlanItemResponse::lineSubtotalCalories)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new MealPlanResponse(
            mealPlan.getId(),
            mealPlan.getName(),
            mealPlan.getMealDate(),
            mealPlan.getMealType(),
            mealPlan.getNotes(),
            itemResponses,
            totalCalories
        );        
    }

    private MealPlanItemResponse toItemResponse(MealPlanItem item) {
        // Quantity can be decimal, so use BigDecimal for the subtotal calculation.
        BigDecimal lineSubtotalCalories = BigDecimal.valueOf(item.getFoodItem().getCalories())
            .multiply(item.getQuantity());

        return new MealPlanItemResponse(
            item.getId(),
            item.getFoodItem().getId(),
            item.getFoodItem().getName(),
            item.getFoodItem().getCalories(),
            item.getQuantity(),
            lineSubtotalCalories
        );
    }
}
