package com.example.meal_catalogue_planner.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.meal_catalogue_planner.entity.MealPlan;
import com.example.meal_catalogue_planner.entity.MealType;

import java.util.List;
import java.time.LocalDate;


public interface MealPlanRepository extends JpaRepository<MealPlan, Long>{
    // Spring Boot requirement 5: derived queries support meal-plan filtering by date and type.
    List<MealPlan> findByUserAccountEmailIgnoreCase(String email);

    List<MealPlan> findByUserAccountEmailIgnoreCaseAndMealDate(String email, LocalDate mealDate);

    List<MealPlan> findByUserAccountEmailIgnoreCaseAndMealType(String email, MealType mealType);

    List<MealPlan> findByUserAccountEmailIgnoreCaseAndMealDateAndMealType(
        String email,
        LocalDate mealDate,
        MealType mealType
    );

    java.util.Optional<MealPlan> findByIdAndUserAccountEmailIgnoreCase(Long id, String email);
}
