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

    // Injects repositories used to manage meal plans, food references, and user ownership.
    public MealPlanService(
        MealPlanRepository mealPlanRepository,
        FoodItemRepository foodItemRepository,
        UserAccountRepository userAccountRepository
    ) {
        this.mealPlanRepository = mealPlanRepository;
        this.foodItemRepository = foodItemRepository;
        this.userAccountRepository = userAccountRepository;
    }

    // Gets meal plans for a user, optionally filtered by date and meal type.
    public List<MealPlanResponse> getMealPlans(String userEmail, LocalDate mealDate, MealType mealType) {
        List<MealPlan> mealPlans;

        // Always include userEmail so one user cannot read another user's meal plans.
        // Repository methods stay explicit here because each optional filter maps to a different query.
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

    // Gets one meal plan by ID for the authenticated user.
    public MealPlanResponse getMealPlanById(String userEmail, Long id) {
        MealPlan mealPlan = findMealPlanEntity(userEmail, id);
        return toResponse(mealPlan);
    }

    // Creates a meal plan and its item rows for the authenticated user.
    public MealPlanResponse createMealPlan(String userEmail, MealPlanRequest request) {
        MealPlan mealPlan = new MealPlan();
        // Bind the plan to the authenticated account instead of trusting a user ID from the request body.
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

    // Updates an existing meal plan and replaces its item rows.
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

    // Deletes a meal plan owned by the authenticated user.
    public void deleteMealPlan(String userEmail, Long id) {
        MealPlan mealPlan = findMealPlanEntity(userEmail, id);
        mealPlanRepository.delete(mealPlan);
    }

    // Finds a meal plan by ID while enforcing user ownership.
    private MealPlan findMealPlanEntity(String userEmail, Long id) {
        // Look up by plan ID and owner in one query so authorization failures do not expose other users' data.
        return mealPlanRepository.findByIdAndUserAccountEmailIgnoreCase(id, userEmail)
        .orElseThrow(() -> new ResponseStatusException(
            HttpStatus.NOT_FOUND,
            "Meal plan not found with id: " + id
        ));
    }

    // Finds the user account associated with the authenticated email.
    private UserAccount findUserAccount(String userEmail) {
        return userAccountRepository.findByEmailIgnoreCase(userEmail)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.UNAUTHORIZED,
                "Authenticated user was not found"
            ));
    }

    // Finds a food item referenced by a meal plan item request.
    private FoodItem findFoodEntity(Long id) {
        return foodItemRepository.findById(id)
        .orElseThrow(() -> new ResponseStatusException(
            HttpStatus.NOT_FOUND,
            "Food item not found with id: " + id
        ));
    }

    // Adds meal plan item entities to a meal plan from request item rows.
    private void replaceItems(
        MealPlan mealPlan,
        List<MealPlanItemRequest> itemRequests
    ) {
        // Rebuild the child collection from request rows; update callers clear existing rows first.
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

    // Converts a meal plan entity into the response DTO with item details and total calories.
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

    // Converts a meal plan item entity into a response DTO with its calorie subtotal.
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
