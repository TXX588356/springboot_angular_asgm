package com.example.meal_catalogue_planner.dto;

import java.math.BigDecimal;
import java.util.List;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record FoodRequest(
    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 120, message = "Name must be between 2 to 120 characters")
    String name,

    @NotBlank(message = "Category is required")
    String category,

    List<String> categoryCodes,

    @NotNull(message = "Calories is required")
    @Min(value = 0, message = "Calories must be zero or greater")
    Integer calories,

    @DecimalMin(value = "0.0", message = "Protein must be zero or greater")
    BigDecimal protein,

    @DecimalMin(value = "0.0", message = "Carbohydrates must be zero or greater")
    BigDecimal carbohydrates,

    @DecimalMin(value = "0.0", message = "Fat must be zero or greater")
    BigDecimal fat,

    @NotBlank(message = "Serving size is required")
    String servingSize,

    @DecimalMin(value = "0.0", message = "Price must be zero or greater")
    BigDecimal price
) {
}
