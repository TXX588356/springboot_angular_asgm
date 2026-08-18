package com.example.meal_catalogue_planner.dto;

import java.math.BigDecimal;
import java.util.List;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

// Request body used to create or update a food catalogue item.
public record FoodRequest(
    // Food item name shown in the catalogue.
    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 120, message = "Name must be between 2 to 120 characters")
    String name,

    // Primary category label for display and legacy category filtering.
    @NotBlank(message = "Category is required")
    String category,

    // Optional normalized category codes used for multi-category matching.
    List<String> categoryCodes,

    // Calories per serving for the food item.
    @NotNull(message = "Calories is required")
    @Min(value = 0, message = "Calories must be zero or greater")
    Integer calories,

    // Protein amount per serving.
    @DecimalMin(value = "0.0", message = "Protein must be zero or greater")
    BigDecimal protein,

    // Carbohydrate amount per serving.
    @DecimalMin(value = "0.0", message = "Carbohydrates must be zero or greater")
    BigDecimal carbohydrates,

    // Fat amount per serving.
    @DecimalMin(value = "0.0", message = "Fat must be zero or greater")
    BigDecimal fat,

    // Human-readable serving size, such as "100g" or "1 bowl".
    @NotBlank(message = "Serving size is required")
    String servingSize,

    // Optional price per serving or item.
    @DecimalMin(value = "0.0", message = "Price must be zero or greater")
    BigDecimal price
) {
}
