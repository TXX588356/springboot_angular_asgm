package com.example.meal_catalogue_planner.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.meal_catalogue_planner.dto.MealPlanRequest;
import com.example.meal_catalogue_planner.dto.MealPlanResponse;
import com.example.meal_catalogue_planner.entity.MealType;
import com.example.meal_catalogue_planner.service.MealPlanService;

import jakarta.validation.Valid;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.PutMapping;



@RestController
@RequestMapping("/api/meal-plans")
public class MealPlanController {
    private final MealPlanService mealPlanService;

    // Injects the service used by meal plan endpoints.
    public MealPlanController(MealPlanService mealPlanService) {
        this.mealPlanService = mealPlanService;
    }

    // Lists meal plans for the authenticated user with optional date and meal-type filters.
    @GetMapping
    public List<MealPlanResponse> getMealPlans(
        Principal principal,
        @RequestParam(required = false) LocalDate mealDate,
        @RequestParam(required = false) MealType mealType
    ) {
        // Spring Boot requirement 1: optional query parameters filter meal plans without extra endpoints.
        return mealPlanService.getMealPlans(principal.getName(), mealDate, mealType);
    }

    // Gets one meal plan owned by the authenticated user.
    @GetMapping("/{id}")
    public MealPlanResponse getMealPlanById(Principal principal, @PathVariable Long id) {
        return mealPlanService.getMealPlanById(principal.getName(), id);
    }

    // Creates a meal plan for the authenticated user.
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MealPlanResponse createMealPlan(
        Principal principal,
        @Valid @RequestBody MealPlanRequest request
    ) {
        // Spring Boot requirements 2 and 4: POST handler creates meal-plan rows and child item rows.
        return mealPlanService.createMealPlan(principal.getName(), request);
    }

    // Updates a meal plan owned by the authenticated user.
    @PutMapping("/{id}")
    public MealPlanResponse updateMealPlan(Principal principal, @PathVariable Long id, @Valid @RequestBody MealPlanRequest request) {
        return mealPlanService.updateMealPlan(principal.getName(), id, request);
    }

    // Deletes a meal plan owned by the authenticated user.
    @DeleteMapping("/{id}")
    public void deleteMealPlan(Principal principal, @PathVariable Long id) {
        mealPlanService.deleteMealPlan(principal.getName(), id);
    }
    
    
}
