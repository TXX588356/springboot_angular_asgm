package com.example.meal_catalogue_planner.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.meal_catalogue_planner.entity.FoodItem;


//<FoodItem, Long> = Generics
// FoodItem = entity
// Long = Entity's primary key
@Repository
public interface FoodItemRepository extends JpaRepository<FoodItem, Long> {
    // Spring reads the method name and auto creates the query.
    List<FoodItem> findByNameContainingIgnoreCase(String name);

    // Find foods where category matches, ignoring uppercase/lowercase
    List<FoodItem> findByCategoryIgnoreCase(String category);

    // JPQL custom query for optional filters.
    // Nothing to filter if category is NULL or maxCalories is NULL.
    @Query("""
            SELECT f FROM FoodItem f
            WHERE (:category IS NULL OR LOWER(f.category) = LOWER(:category))
            AND (:maxCalories IS NULL OR f.calories <= :maxCalories)
            ORDER BY f.name ASC
            """)
    List<FoodItem> filterFoods(String category, Integer maxCalories);
}
