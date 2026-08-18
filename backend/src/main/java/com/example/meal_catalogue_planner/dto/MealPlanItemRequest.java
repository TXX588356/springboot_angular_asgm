package com.example.meal_catalogue_planner.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

// Request body row for one food item inside a meal plan.
public record MealPlanItemRequest(
    // Food catalogue item to include in the meal plan.
    @NotNull(message = "Food item is required")
    Long foodItemId,

    // Number of servings or units of the selected food item.
    @NotNull(message = "Quantity is required")
    @DecimalMin(value = "0.01", message = "Quantity must be greater than zero")
    BigDecimal quantity
) {
    
}
