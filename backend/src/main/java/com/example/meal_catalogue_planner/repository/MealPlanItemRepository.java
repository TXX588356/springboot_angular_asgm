package com.example.meal_catalogue_planner.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.meal_catalogue_planner.entity.MealPlanItem;

public interface MealPlanItemRepository extends JpaRepository<MealPlanItem, Long> {
    
}
