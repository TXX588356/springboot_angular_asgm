package com.example.meal_catalogue_planner.dto;

import java.time.LocalDate;
import java.util.List;

import com.example.meal_catalogue_planner.entity.MealType;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record MealPlanRequest(
    @NotBlank(message = "Meal plan name is required")
    String name,

    @NotNull(message = "Meal date is required")
    LocalDate mealDate,

    @NotNull(message = "Meal type is required")
    MealType mealType,

    String notes,

    @Valid
    @NotEmpty(message = "At least one food item is required")
    List<MealPlanItemRequest> items
 ) {
    
}
