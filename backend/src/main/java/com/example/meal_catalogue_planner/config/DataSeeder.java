package com.example.meal_catalogue_planner.config;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import com.example.meal_catalogue_planner.entity.FoodItem;
import com.example.meal_catalogue_planner.repository.FoodItemRepository;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

@Configuration
public class DataSeeder {

    // Load starter food records from JSON when Spring Boot starts.
    @Bean
    public CommandLineRunner seedFoodItems(
            FoodItemRepository foodItemRepository,
            ObjectMapper objectMapper
    ) {
        return args -> {
            // Prevent duplicate seed data every time the app restarts.
            if (foodItemRepository.count() > 0) {
                return;
            }

            ClassPathResource seedFile = new ClassPathResource("data/food-items.json");
            List<FoodSeedItem> seedItems = objectMapper.readValue(
                    seedFile.getInputStream(),
                    new TypeReference<List<FoodSeedItem>>() {
                    }
            );

            List<FoodItem> foodItems = seedItems.stream()
                    .map(FoodSeedItem::toFoodItem)
                    .toList();

            foodItemRepository.saveAll(foodItems);
        };
    }

    // JSON row shape used only for converting seed data into FoodItem entities.
    private record FoodSeedItem(
            String name,
            String category,
            List<String> categoryCodes,
            String servingSize,
            Integer calories,
            BigDecimal protein,
            BigDecimal carbohydrates,
            BigDecimal fat,
            BigDecimal price
    ) {
        // Convert one JSON seed row into the current FoodItem entity model.
        private FoodItem toFoodItem() {
            return new FoodItem(
                    name,
                    category,
                    calories,
                    protein,
                    carbohydrates,
                    fat,
                    servingSize,
                    price
            );
        }
    }
}
