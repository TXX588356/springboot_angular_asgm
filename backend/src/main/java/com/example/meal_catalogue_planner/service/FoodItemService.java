package com.example.meal_catalogue_planner.service;

import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.meal_catalogue_planner.entity.FoodItem;
import com.example.meal_catalogue_planner.repository.FoodItemRepository;

@Service
public class FoodItemService {
    private final FoodItemRepository foodItemRepository;

    public FoodItemService(FoodItemRepository foodItemRepository) {
        this.foodItemRepository = foodItemRepository;
    }

    // Get every food item.
    public List<FoodItem> getAllFoods() {
        return foodItemRepository.findAll();
    }

    public FoodItem getFoodById(Long id) {
        return foodItemRepository.findById(id)
        // Error handling
        .orElseThrow(() -> new ResponseStatusException(
            HttpStatus.NOT_FOUND,
            "food item not found with id: " + id
        ));
    }

    // Create new food item
    public FoodItem createFood(FoodItem foodItem) {
        foodItem.setId(null);
        return foodItemRepository.save(foodItem);
    }

    // Update existing food item
    public FoodItem updateFood(Long id, FoodItem updatedFood) {
        FoodItem existingFood = getFoodById(id);

        existingFood.setName(updatedFood.getName());
        existingFood.setCategory(updatedFood.getCategory());
        existingFood.setCalories(updatedFood.getCalories());
        existingFood.setProtein(updatedFood.getProtein());
        existingFood.setCarbohydrates(updatedFood.getCarbohydrates());
        existingFood.setFat(updatedFood.getFat());
        existingFood.setServingSize(updatedFood.getServingSize());
        existingFood.setPrice(updatedFood.getPrice());

        return foodItemRepository.save(existingFood);
    }

    // Delete food. If it is used by a meal plan later, return 409
    public void deleteFood(Long id) {
        FoodItem foodItem = getFoodById(id);

        try {
            foodItemRepository.delete(foodItem);
        } catch (DataIntegrityViolationException ex) {
            throw new ResponseStatusException(
                HttpStatus.CONFLICT,
                "Food item cannot be deleted because it is used in a meal plan"
            );
        }
    }

    // Search food by name
    public List<FoodItem> searchFoodsByName(String name) {
        return foodItemRepository.findByNameContainingIgnoreCase(name);
    }

    // Get foods by category
    public List<FoodItem> getFoodsByCategory(String category) {
        return foodItemRepository.findByCategoryIgnoreCase(category);
    }

    // Filter foods by cat and max calories.
    public List<FoodItem> filterFoods(String category, Integer maxCalories) {
        return foodItemRepository.filterFoods(category, maxCalories);
    }
}
