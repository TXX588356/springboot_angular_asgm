package com.example.meal_catalogue_planner.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.meal_catalogue_planner.entity.MealPlan;
import com.example.meal_catalogue_planner.entity.MealType;

import java.util.List;
import java.time.LocalDate;


public interface MealPlanRepository extends JpaRepository<MealPlan, Long>{
    List<MealPlan> findByMealDate(LocalDate mealDate);

    List<MealPlan> findByMealType(MealType mealType);

    List<MealPlan> findByMealDateAndMealType(LocalDate mealDate, MealType mealType);
}
