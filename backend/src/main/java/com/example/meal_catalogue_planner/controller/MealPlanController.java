package com.example.meal_catalogue_planner.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.meal_catalogue_planner.dto.MealPlanRequest;
import com.example.meal_catalogue_planner.dto.MealPlanResponse;
import com.example.meal_catalogue_planner.entity.MealType;
import com.example.meal_catalogue_planner.service.MealPlanService;

import jakarta.validation.Valid;

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

    public MealPlanController(MealPlanService mealPlanService) {
        this.mealPlanService = mealPlanService;
    }

    @GetMapping
    public List<MealPlanResponse> getMealPlans(
        @RequestParam(required = false) LocalDate mealDate,
        @RequestParam(required = false) MealType mealType
    ) {
        // Optional query parameters let the frontend filter the list without extra endpoints.
        return mealPlanService.getMealPlans(mealDate, mealType);
    }

    @GetMapping("/{id}")
    public MealPlanResponse getMealPlanById(@PathVariable Long id) {
        return mealPlanService.getMealPlanById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MealPlanResponse createMealPlan(
        @Valid @RequestBody MealPlanRequest request
    ) {
        // The request DTO contains food IDs and quantities; the service resolves them to entities.
        return mealPlanService.createMealPlan(request);
    }

    @PutMapping("/{id}")
    public MealPlanResponse updateMealPlan(@PathVariable Long id, @Valid @RequestBody MealPlanRequest request) {
        return mealPlanService.updateMealPlan(id, request);
    }

    @DeleteMapping("/{id}")
    public void deleteMealPlan(@PathVariable Long id) {
        mealPlanService.deleteMealPlan(id);
    }
    
    
}
