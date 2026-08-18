package com.example.meal_catalogue_planner.dto;

import java.math.BigDecimal;

// Response body row for one food item inside a meal plan.
public record MealPlanItemResponse (
    // Database ID of the meal plan item row.
    Long id,

    // Database ID of the referenced food item.
    Long foodItemId,

    // Display name of the referenced food item.
    String foodName,

    // Calories per serving for the referenced food item.
    Integer calories,

    // Quantity of the food item included in the meal plan.
    BigDecimal quantity,

    // Calories for this row after multiplying calories by quantity.
    BigDecimal lineSubtotalCalories
) {
}
