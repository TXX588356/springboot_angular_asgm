package com.example.meal_catalogue_planner.controller;

import java.util.List;

import com.example.meal_catalogue_planner.dto.FoodRequest;
import com.example.meal_catalogue_planner.entity.FoodItem;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.meal_catalogue_planner.service.FoodItemService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.PutMapping;



@RestController
@RequestMapping("/api/foods")
public class FoodItemController {
    private final FoodItemService foodItemService;

    // Injects the service used by food catalogue endpoints.
    public FoodItemController(FoodItemService foodItemService) {
        this.foodItemService = foodItemService;
    }

    // Lists food items with optional search, filter, calorie cap, and sorting query parameters.
    @GetMapping
    public List<FoodItem> findFoods(
        @RequestParam(required = false) String name,
        @RequestParam(required = false) String category,
        @RequestParam(required = false) Integer maxCalories,
        @RequestParam(required = false) String sortBy,
        @RequestParam(required = false) String direction
    ) {
        // Spring Boot requirement 1: REST endpoint supports query parameters for search, filter, and sorting.
        return foodItemService.findFoods(name, category, maxCalories, sortBy, direction);
    }

    // GET /api/foods?sortBy=calories&direction=desc
    // @GetMapping
    // public List<FoodItem> getAllFoods(
    //     @RequestParam(defaultValue = "name") String sortBy,
    //     @RequestParam(defaultValue = "asc") String direction
    // ) {
    //     return foodItemService.getAllFoods(sortBy, direction);
    // }

    // GET /api/foods/{id}
    // Gets one food item by its path ID.
    @GetMapping("/{id}")
    public FoodItem getFoodById(@PathVariable Long id) {
        // Spring Boot requirement 1: path parameter maps the URL food ID to a backend lookup.
        return foodItemService.getFoodById(id);
    }

    // POST /api/foods
    // Creates a new food item from the request body.
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FoodItem createFood(@Valid @RequestBody FoodRequest request) {
        // Spring Boot requirements 2 and 4: annotated handler creates a database row through Spring Data JPA.
        return foodItemService.createFood(request);
    }

    // PUT /api/foods/{id}
    // Updates an existing food item selected by its path ID.
    @PutMapping("/{id}")
    public FoodItem updateFood(@PathVariable Long id,@Valid @RequestBody FoodRequest request) {
       // Spring Boot requirements 2 and 4: annotated handler updates an existing database row.
       return foodItemService.updateFood(id, request);
    }

    // DELETE /api/foods/{id}
    // delete one food item
    // Deletes one food item selected by its path ID.
    @DeleteMapping("/{id}")
    public void deleteFood(@PathVariable Long id) {
        // Spring Boot requirements 2 and 4: annotated handler deletes a database row by ID.
        foodItemService.deleteFood(id);
    }

    // GET /api/foods/search?name={param}
    // search food name that contains {param}
    // Searches food items whose names contain the provided query text.
    @GetMapping("/search")
    public List<FoodItem> searchFoods(@RequestParam String name) {
        return foodItemService.searchFoodsByName(name);
    }

    // GET /api/foods/category/{category}
    // Get all {category} foods
    // Lists food items matching a category path value.
    @GetMapping("/category/{category}")
    public List<FoodItem> getFoodsByCategory(@PathVariable String category) {
        return foodItemService.getFoodsByCategory(category);
    }

    // GET /api/foods/filter?category={category}&maxCalories={maxCalories}
    // Filters food items with optional category and maximum calorie query parameters.
    @GetMapping("/filter")
    public List<FoodItem> filterFoods(
        @RequestParam(required = false) String category,
        @RequestParam(required = false) Integer maxCalories) {
        return foodItemService.filterFoods(category, maxCalories);
    }
    
    
    
}
