package com.example.meal_catalogue_planner.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.meal_catalogue_planner.entity.FoodItem;
import com.example.meal_catalogue_planner.repository.FoodItemRepository;

import jakarta.persistence.criteria.Predicate;

@Service
public class FoodItemService {
    private final FoodItemRepository foodItemRepository;

    public FoodItemService(FoodItemRepository foodItemRepository) {
        this.foodItemRepository = foodItemRepository;
    }

    // Search, filter, sorting all can be applied together
    public List<FoodItem> findFoods(
        String name, 
        String category,
        Integer maxCalories,
        String sortBy,
        String direction
    ) {
        Sort sort = buildSort(sortBy, direction);

        Specification<FoodItem> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (name != null && !name.isBlank()) {
                predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("name")),
                    "%" + name.toLowerCase()  + "%"));
            }
            if (category != null && !category.isBlank()) {
                predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("category")),
                    category.toLowerCase()
                ));
            }

            if (maxCalories != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(
                    root.get("calories"), 
                    maxCalories));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        return foodItemRepository.findAll(spec, sort);

    }

    // Only allow sorting by real FoodItem fields.
    private Sort buildSort(String sortBy, String direction) {
        String safeSortBy = switch (sortBy) {
            case "name", "category", "calories", "price" -> sortBy;
            default -> "name";
        };

        if ("desc".equalsIgnoreCase(direction)) {
            return Sort.by(safeSortBy).descending();
        }
        
        return Sort.by(safeSortBy).ascending();
    }

    // Get every food item sotred by selected field.
    public List<FoodItem> getAllFoods(String sortBy, String direction) {
        Sort sort = Sort.by(sortBy);

        if("desc".equalsIgnoreCase(direction)) {
            sort = sort.descending();
        } else {
            sort = sort.ascending();
        }

        return foodItemRepository.findAll(sort);
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
        boolean hasCategory = category != null && !category.isBlank();
        boolean hasMaxCalories = maxCalories != null;

        // Case 1: category + maxCalories
        if (hasCategory && hasMaxCalories) {
            return foodItemRepository.findByCategoryIgnoreCaseAndCaloriesLessThanEqualOrderByNameAsc(
                category, 
                maxCalories
            );
        }

        // Case 2: only category
        if (hasCategory) {
            return foodItemRepository.findByCategoryIgnoreCaseOrderByNameAsc(category);
        }

        // Case 3: only max calories
        if (hasMaxCalories) {
            return foodItemRepository.findByCaloriesLessThanEqualOrderByNameAsc(maxCalories);
        }

        // Case 4: no filter
        return foodItemRepository.findAll();
    }
}
