package com.example.meal_catalogue_planner.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public record MealPlanItemRequest(
    @NotNull(message = "Food item is required")
    Long foodItemId,

    @NotNull(message = "Quantity is required")
    @DecimalMin(value = "0.01", message = "Quantity must be greater than zero")
    BigDecimal quantity
) {
    
}
