package com.example.meal_catalogue_planner.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
public class MealPlan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Meal plan name is required")
    private String name;

    @NotNull(message = "Meal date is required")
    private LocalDate mealDate;

    @NotNull(message = "Meal type is required")
    @Enumerated(EnumType.STRING)
    private MealType mealType;

    private String notes;

    @OneToMany(mappedBy = "mealPlan", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MealPlanItem> items = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public LocalDate getMealDate() {
        return mealDate;
    }

    public MealType getMealType() {
        return mealType;
    }

    public String getNotes() {
        return notes;
    }

    public List<MealPlanItem> getItems() {
        return items;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setMealDate(LocalDate mealDate) {
        this.mealDate = mealDate;
    }

    public void setMealType(MealType mealType) {
        this.mealType = mealType;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public void setItems(List<MealPlanItem> items) {
        this.items = items;
    }
}
