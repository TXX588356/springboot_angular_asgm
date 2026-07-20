package com.example.meal_catalogue_planner.controller;

import java.util.List;

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

    public FoodItemController(FoodItemService foodItemService) {
        this.foodItemService = foodItemService;
    }

    // GET /api/foods
    // get full food list
    @GetMapping
    public List<FoodItem> getAllFoods() {
        return foodItemService.getAllFoods();
    }

    // GET /api/foods/{id}
    // get specific food
    @GetMapping("/{id}")
    public FoodItem getFoodById(@PathVariable Long id) {
        return foodItemService.getFoodById(id);
    }

    // POST /api/foods
    // add food item
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FoodItem createFood(@Valid @RequestBody FoodItem foodItem) {
        return foodItemService.createFood(foodItem);
    }

    // PUT /api/foods/{id}
    // update one food item
    @PutMapping("/{id}")
    public FoodItem updateFood(@PathVariable Long id,@Valid @RequestBody FoodItem foodItem) {
       return foodItemService.updateFood(id, foodItem);
    }

    // DELETE /api/foods/{id}
    // delete one food item
    @DeleteMapping("/{id}")
    public void deleteFood(@PathVariable Long id) {
        foodItemService.deleteFood(id);
    }

    // GET /api/foods/search?name={param}
    // search food name that contains {param}
    @GetMapping("/search")
    public List<FoodItem> searchFoods(@RequestParam String name) {
        return foodItemService.searchFoodsByName(name);
    }

    // GET /api/foods/category/{category}
    // Get all {category} foods
    @GetMapping("/category/{category}")
    public List<FoodItem> getFoodsByCategory(@PathVariable String category) {
        return foodItemService.getFoodsByCategory(category);
    }

    // GET /api/foods/filter?category={category}&maxCalories={maxCalories}
    @GetMapping("filter")
    public List<FoodItem> filterFoods(
        @RequestParam(required = false) String category,
        @RequestParam(required = false) Integer maxCalories) {
        return foodItemService.filterFoods(category, maxCalories);
    }
    
    
    
}
