package com.example.meal_catalogue_planner.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.example.meal_catalogue_planner.entity.MealType;

public record MealPlanResponse (
    Long id,
    String name,
    LocalDate mealDate,
    MealType mealType,
    String notes,
    List<MealPlanItemResponse> items,
    BigDecimal totalCalories
) {
    
}
