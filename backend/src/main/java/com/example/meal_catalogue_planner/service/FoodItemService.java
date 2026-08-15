package com.example.meal_catalogue_planner.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.meal_catalogue_planner.dto.FoodRequest;
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
        String requestedSort = sortBy == null ? "name" : sortBy;

        String safeSortBy = switch (requestedSort) {
            case "name", "category", "calories", "price" -> requestedSort;
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
    public FoodItem createFood(FoodRequest request) {
        FoodItem foodItem = new FoodItem();
        foodItem.setId(null);
        applyFoodRequest(foodItem, request);
        return foodItemRepository.save(foodItem);
    }

    // Update existing food item
    public FoodItem updateFood(Long id, FoodRequest request) {
        FoodItem existingFood = getFoodById(id);

        applyFoodRequest(existingFood, request);

        return foodItemRepository.save(existingFood);
    }

    private void applyFoodRequest(FoodItem foodItem, FoodRequest request) {
        // Request DTO keeps JSON input separate from the JPA entity and avoids deserialization issues.
        foodItem.setName(request.name());
        foodItem.setCategory(request.category());
        foodItem.setCategoryCodes(resolveCategoryCodes(request));
        foodItem.setCalories(request.calories());
        foodItem.setProtein(defaultDecimal(request.protein()));
        foodItem.setCarbohydrates(defaultDecimal(request.carbohydrates()));
        foodItem.setFat(defaultDecimal(request.fat()));
        foodItem.setServingSize(request.servingSize());
        foodItem.setPrice(defaultDecimal(request.price()));
    }

    private Set<String> resolveCategoryCodes(FoodRequest request) {
        Set<String> categoryCodes = new LinkedHashSet<>();

        if (request.categoryCodes() != null) {
            request.categoryCodes().stream()
                .filter(code -> code != null && !code.isBlank())
                .map(String::trim)
                .forEach(categoryCodes::add);
        }

        if (categoryCodes.isEmpty() && request.category() != null && !request.category().isBlank()) {
            categoryCodes.add(request.category().trim());
        }

        return categoryCodes;
    }

    private BigDecimal defaultDecimal(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
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
        return foodItemRepository.findCategoryOrCategoryCodeIgnoreCase(category);
    }

    // Filter foods by cat and max calories using JPQL query.
    public List<FoodItem> filterFoods(String category, Integer maxCalories) {
        return foodItemRepository.filterFoods(category, maxCalories);
    }
}
