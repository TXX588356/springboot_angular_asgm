package com.example.meal_catalogue_planner.dto;

import java.time.LocalDate;
import java.util.List;

import com.example.meal_catalogue_planner.entity.MealType;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

// Request body used to create or update a meal plan.
public record MealPlanRequest(
    // User-facing name for the meal plan.
    @NotBlank(message = "Meal plan name is required")
    String name,

    // Calendar date assigned to the meal plan.
    @NotNull(message = "Meal date is required")
    LocalDate mealDate,

    // Meal slot, such as breakfast, lunch, or dinner.
    @NotNull(message = "Meal type is required")
    MealType mealType,

    // Optional free-form notes for the meal plan.
    String notes,

    // Food item rows included in the meal plan.
    @Valid
    @NotEmpty(message = "At least one food item is required")
    List<MealPlanItemRequest> items
 ) {
    
}
