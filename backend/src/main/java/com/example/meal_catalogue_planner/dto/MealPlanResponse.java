package com.example.meal_catalogue_planner.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.example.meal_catalogue_planner.entity.MealType;

// Response body returned for meal plan reads and writes.
public record MealPlanResponse (
    // Database ID of the meal plan.
    Long id,

    // User-facing name for the meal plan.
    String name,

    // Calendar date assigned to the meal plan.
    LocalDate mealDate,

    // Meal slot, such as breakfast, lunch, or dinner.
    MealType mealType,

    // Optional free-form notes for the meal plan.
    String notes,

    // Food item rows included in the meal plan.
    List<MealPlanItemResponse> items,

    // Total calories calculated from all item row subtotals.
    BigDecimal totalCalories
) {
    
}
