package com.example.meal_catalogue_planner.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.example.meal_catalogue_planner.dto.FoodRequest;
import com.example.meal_catalogue_planner.entity.FoodItem;
import com.example.meal_catalogue_planner.repository.FoodItemRepository;

@ExtendWith(MockitoExtension.class)
class FoodItemServiceTest {
    @Mock
    private FoodItemRepository foodItemRepository;

    @InjectMocks
    private FoodItemService foodItemService;

    @Test
    void createFoodDefaultsMissingDecimalsAndCleansCategoryCodes() {
        FoodRequest request = new FoodRequest(
            "Chicken Rice",
            "Meals",
            List.of("hawker", " ", "protein", "hawker"),
            620,
            null,
            BigDecimal.valueOf(72),
            null,
            "1 plate",
            null
        );

        when(foodItemRepository.save(any(FoodItem.class))).thenAnswer(invocation -> invocation.getArgument(0));

        FoodItem saved = foodItemService.createFood(request);

        assertThat(saved.getId()).isNull();
        assertThat(saved.getName()).isEqualTo("Chicken Rice");
        assertThat(saved.getCategoryCodes()).containsExactly("hawker", "protein");
        assertThat(saved.getProtein()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(saved.getCarbohydrates()).isEqualByComparingTo("72");
        assertThat(saved.getFat()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(saved.getPrice()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void updateFoodUsesCategoryAsFallbackCodeWhenCodesAreMissing() {
        FoodItem existingFood = new FoodItem();
        existingFood.setId(42L);
        when(foodItemRepository.findById(42L)).thenReturn(Optional.of(existingFood));
        when(foodItemRepository.save(any(FoodItem.class))).thenAnswer(invocation -> invocation.getArgument(0));

        FoodRequest request = new FoodRequest(
            "Greek Yogurt",
            "Breakfast",
            null,
            120,
            BigDecimal.TEN,
            BigDecimal.valueOf(8),
            BigDecimal.ONE,
            "1 cup",
            BigDecimal.valueOf(2.50)
        );

        FoodItem updated = foodItemService.updateFood(42L, request);

        assertThat(updated.getId()).isEqualTo(42L);
        assertThat(updated.getCategoryCodes()).containsExactly("Breakfast");
        assertThat(updated.getPrice()).isEqualByComparingTo("2.50");
    }

    @Test
    void deleteFoodReturnsConflictWhenMealPlansReferenceFood() {
        FoodItem existingFood = new FoodItem();
        when(foodItemRepository.findById(7L)).thenReturn(Optional.of(existingFood));
        org.mockito.Mockito.doThrow(new DataIntegrityViolationException("referenced"))
            .when(foodItemRepository)
            .delete(existingFood);

        assertThatThrownBy(() -> foodItemService.deleteFood(7L))
            .isInstanceOf(ResponseStatusException.class)
            .satisfies(ex -> assertThat(((ResponseStatusException) ex).getStatusCode()).isEqualTo(HttpStatus.CONFLICT));
    }

    @Test
    void getAllFoodsPassesRequestedSortDirectionToRepository() {
        foodItemService.getAllFoods("price", "desc");

        ArgumentCaptor<org.springframework.data.domain.Sort> sortCaptor =
            ArgumentCaptor.forClass(org.springframework.data.domain.Sort.class);
        verify(foodItemRepository).findAll(sortCaptor.capture());
        assertThat(sortCaptor.getValue().getOrderFor("price")).isNotNull();
        assertThat(sortCaptor.getValue().getOrderFor("price").isDescending()).isTrue();
    }
}
