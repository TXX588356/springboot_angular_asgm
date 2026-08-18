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

    // Injects the repository used for food catalogue persistence and queries.
    public FoodItemService(FoodItemRepository foodItemRepository) {
        this.foodItemRepository = foodItemRepository;
    }

    // Builds a dynamic query so search, category filter, calorie cap, and sorting can be combined.
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
                // Name search is partial and case-insensitive for catalogue-style browsing.
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

    // Builds a safe Sort object from client-provided sort field and direction values.
    private Sort buildSort(String sortBy, String direction) {
        String requestedSort = sortBy == null ? "name" : sortBy;

        // Do not pass arbitrary client strings into Sort because invalid properties fail at runtime.
        String safeSortBy = switch (requestedSort) {
            case "name", "category", "calories", "price" -> requestedSort;
            default -> "name";
        };

        if ("desc".equalsIgnoreCase(direction)) {
            return Sort.by(safeSortBy).descending();
        }
        
        return Sort.by(safeSortBy).ascending();
    }

    // Get every food item sorted by selected field.
    public List<FoodItem> getAllFoods(String sortBy, String direction) {
        Sort sort = Sort.by(sortBy);

        if("desc".equalsIgnoreCase(direction)) {
            sort = sort.descending();
        } else {
            sort = sort.ascending();
        }

        return foodItemRepository.findAll(sort);
    }

    // Gets one food item by ID or returns a 404 error when it does not exist.
    public FoodItem getFoodById(Long id) {
        return foodItemRepository.findById(id)
        // Error handling
        .orElseThrow(() -> new ResponseStatusException(
            HttpStatus.NOT_FOUND,
            "food item not found with id: " + id
        ));
    }

    // Creates a new food item from a request DTO.
    public FoodItem createFood(FoodRequest request) {
        FoodItem foodItem = new FoodItem();
        foodItem.setId(null);
        applyFoodRequest(foodItem, request);
        return foodItemRepository.save(foodItem);
    }

    // Updates an existing food item with values from a request DTO.
    public FoodItem updateFood(Long id, FoodRequest request) {
        FoodItem existingFood = getFoodById(id);

        applyFoodRequest(existingFood, request);

        return foodItemRepository.save(existingFood);
    }

    // Copies editable request fields onto a FoodItem entity.
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

    // Resolves the stored category code set from explicit codes or the legacy category field.
    private Set<String> resolveCategoryCodes(FoodRequest request) {
        Set<String> categoryCodes = new LinkedHashSet<>();

        if (request.categoryCodes() != null) {
            // Preserve request order while removing blank values and duplicate category codes.
            request.categoryCodes().stream()
                .filter(code -> code != null && !code.isBlank())
                .map(String::trim)
                .forEach(categoryCodes::add);
        }

        // Older clients may still submit only category, so keep it as a fallback category code.
        if (categoryCodes.isEmpty() && request.category() != null && !request.category().isBlank()) {
            categoryCodes.add(request.category().trim());
        }

        return categoryCodes;
    }

    // Returns zero when an optional decimal value is not provided.
    private BigDecimal defaultDecimal(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    // Deletes a food item, returning 409 if existing meal plans still reference it.
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

    // Searches food items by partial name match.
    public List<FoodItem> searchFoodsByName(String name) {
        return foodItemRepository.findByNameContainingIgnoreCase(name);
    }

    // Gets food items matching either category name or category code.
    public List<FoodItem> getFoodsByCategory(String category) {
        return foodItemRepository.findCategoryOrCategoryCodeIgnoreCase(category);
    }

    // Filters food items by category and maximum calories using the repository JPQL query.
    public List<FoodItem> filterFoods(String category, Integer maxCalories) {
        return foodItemRepository.filterFoods(category, maxCalories);
    }
}
