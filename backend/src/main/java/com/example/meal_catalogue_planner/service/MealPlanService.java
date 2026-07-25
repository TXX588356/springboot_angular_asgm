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
import com.example.meal_catalogue_planner.repository.FoodItemRepository;
import com.example.meal_catalogue_planner.repository.MealPlanRepository;

@Service
public class MealPlanService {
    private final MealPlanRepository mealPlanRepository;
    private final FoodItemRepository foodItemRepository;

    public MealPlanService(
        MealPlanRepository mealPlanRepository,
        FoodItemRepository foodItemRepository
    ) {
        this.mealPlanRepository = mealPlanRepository;
        this.foodItemRepository = foodItemRepository;
    }

    public List<MealPlanResponse> getMealPlans(LocalDate mealDate, MealType mealType) {
        List<MealPlan> mealPlans;

        if (mealDate != null && mealType != null) {
            mealPlans = mealPlanRepository.findByMealDateAndMealType(mealDate, mealType);
        } else if (mealDate != null) {
            mealPlans = mealPlanRepository.findByMealDate(mealDate);
        } else if (mealType != null) {
            mealPlans = mealPlanRepository.findByMealType(mealType);
        } else {
            mealPlans = mealPlanRepository.findAll();
        }

        return mealPlans.stream()
            .map(this::toResponse)
            .toList();
    }

    public MealPlanResponse getMealPlanById(Long id) {
        MealPlan mealPlan = findMealPlanEntity(id);
        return toResponse(mealPlan);
    }

    public MealPlanResponse createMealPlan(MealPlanRequest request) {
        MealPlan mealPlan = new MealPlan();

        mealPlan.setName(request.name());
        mealPlan.setMealDate(request.mealDate());
        mealPlan.setMealType(request.mealType());
        mealPlan.setNotes(request.notes());

        replaceItems(mealPlan, request.items());

        MealPlan savedMealPlan = mealPlanRepository.save(mealPlan);
        return toResponse(savedMealPlan);
    }

    public MealPlanResponse updateMealPlan(Long id, MealPlanRequest request) {
        MealPlan mealPlan = findMealPlanEntity(id);

        mealPlan.setName(request.name());
        mealPlan.setMealDate(request.mealDate());
        mealPlan.setMealType(request.mealType());
        mealPlan.setNotes(request.notes());

        mealPlan.getItems().clear();
        replaceItems(mealPlan, request.items());

        MealPlan savedMealPlan = mealPlanRepository.save(mealPlan);
        return toResponse(savedMealPlan);
    }

    public void deleteMealPlan(Long id) {
        MealPlan mealPlan = findMealPlanEntity(id);
        mealPlanRepository.delete(mealPlan);
    }

    private MealPlan findMealPlanEntity(Long id) {
        return mealPlanRepository.findById(id)
        .orElseThrow(() -> new ResponseStatusException(
            HttpStatus.NOT_FOUND,
            "Meal plan not found with id: " + id
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
            FoodItem foodItem = findFoodEntity(itemRequest.foodItemId());

            MealPlanItem item = new MealPlanItem();
            item.setMealPlan(mealPlan);
            item.setFoodItem(foodItem);
            item.setQuantity(itemRequest.quantity());

            mealPlan.getItems().add(item);
        }
    }

    private MealPlanResponse toResponse(MealPlan mealPlan) {
        List<MealPlanItemResponse> itemResponses = mealPlan.getItems()
            .stream()
            .map(this::toItemResponse)
            .toList();

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
