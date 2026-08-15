package com.example.meal_catalogue_planner.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.meal_catalogue_planner.entity.FoodItem;


//<FoodItem, Long> = Generics
// FoodItem = entity
// Long = Entity's primary key
@Repository
public interface FoodItemRepository extends JpaRepository<FoodItem, Long>, JpaSpecificationExecutor<FoodItem>{
    // Spring Boot requirements 4 and 5: JpaRepository provides CRUD, while this derived query searches by name.
    List<FoodItem> findByNameContainingIgnoreCase(String name);

    // Spring Boot requirement 5: JPQL query matches either the display category or any category code.
    @Query("""
                SELECT DISTINCT f
                FROM FoodItem f
                LEFT JOIN f.categoryCodes code
                WHERE LOWER(f.category) = LOWER(:category)
                OR LOWER(code) = LOWER(:category)
                """)
    List<FoodItem> findCategoryOrCategoryCodeIgnoreCase(@Param("category") String category);

    // Spring Boot requirement 5: JPQL query combines optional category/code and calorie filters.
    @Query("""
            SELECT DISTINCT f
            FROM FoodItem f
            LEFT JOIN f.categoryCodes code
            WHERE (:category IS NULL
            OR :category = ''
            OR LOWER(f.category) = LOWER(:category)
            OR LOWER(code) = LOWER(:category))
            AND (:maxCalories IS NULL OR f.calories <= :maxCalories)
            ORDER BY f.name ASC
            """)
    List<FoodItem> filterFoods(
        @Param("category") String category, 
        @Param("maxCalories")Integer maxCalories);
}
