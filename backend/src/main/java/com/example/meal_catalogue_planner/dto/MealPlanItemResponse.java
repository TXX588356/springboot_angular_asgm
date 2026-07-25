package com.example.meal_catalogue_planner.dto;

import java.math.BigDecimal;

public record MealPlanItemResponse (
    Long id,
    Long foodItemId,
    String foodName,
    Integer calories,
    BigDecimal quantity,
    BigDecimal lineSubtotalCalories
) {
}
