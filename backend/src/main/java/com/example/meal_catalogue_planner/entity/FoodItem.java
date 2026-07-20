package com.example.meal_catalogue_planner.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
public class FoodItem {
    // Primary key
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Food name
    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 120, message = "Name must be between 2 to 120 characters")
    @Column(nullable = false, length = 120)
    private String name;

    // Food category
    @NotBlank(message = "Category is required")
    @Column(nullable = false)
    private String category;

    // Calories per serving
    @NotNull(message = "Calories is required")
    @Min(value = 1, message = "Calories must be greater than 0")
    @Column(nullable = false)
    private Integer calories;

    // Macronutrients use BigDecimal because they can have decimal values
    @DecimalMin(value = "0.0", message = "Protein must be zero or greater")
    private BigDecimal protein = BigDecimal.ZERO;

    @DecimalMin(value = "0.0", message = "Carbohydrates must be zero or greater")
    private BigDecimal carbohydrates = BigDecimal.ZERO;

    @DecimalMin(value = "0.0", message = "Fat must be zero or greater")
    private BigDecimal fat = BigDecimal.ZERO;

    // Example: "1 plate", "1 cup", "300 ml"
    @NotBlank(message = "Serving size is required")
    @Column(nullable = false)
    private String servingSize;

    @DecimalMin(value = "0.0", message = "Price must be zero or greater")
    private BigDecimal price = BigDecimal.ZERO;

    public FoodItem() {
    }

    public FoodItem(String name, String category, Integer calories,
                    BigDecimal protein, BigDecimal carbohydrates, BigDecimal fat,
                    String servingSize, BigDecimal price) {
        this.name = name;
        this.category = category;
        this.calories = calories;
        this.protein = protein;
        this.carbohydrates = carbohydrates;
        this.fat = fat;
        this.servingSize = servingSize;
        this.price = price;
    }

        // Getter returns the ID.
    public Long getId() {
        return id;
    }

    // Setter changes the ID.
    public void setId(Long id) {
        this.id = id;
    }

    // Getter returns the food name.
    public String getName() {
        return name;
    }

    // Setter changes the food name.
    public void setName(String name) {
        this.name = name;
    }

    // Getter returns the category.
    public String getCategory() {
        return category;
    }

    // Setter changes the category.
    public void setCategory(String category) {
        this.category = category;
    }

    // Getter returns calories.
    public Integer getCalories() {
        return calories;
    }

    // Setter changes calories.
    public void setCalories(Integer calories) {
        this.calories = calories;
    }

    public BigDecimal getProtein() {
        return protein;
    }

    public void setProtein(BigDecimal protein) {
        this.protein = protein;
    }

    public BigDecimal getCarbohydrates() {
        return carbohydrates;
    }

    public void setCarbohydrates(BigDecimal carbohydrates) {
        this.carbohydrates = carbohydrates;
    }

    public BigDecimal getFat() {
        return fat;
    }

    public void setFat(BigDecimal fat) {
        this.fat = fat;
    }

    public String getServingSize() {
        return servingSize;
    }

    public void setServingSize(String servingSize) {
        this.servingSize = servingSize;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

}
