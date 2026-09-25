package com.example.meal_catalogue_planner.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.example.meal_catalogue_planner.dto.MealPlanItemRequest;
import com.example.meal_catalogue_planner.dto.MealPlanRequest;
import com.example.meal_catalogue_planner.dto.MealPlanResponse;
import com.example.meal_catalogue_planner.entity.FoodItem;
import com.example.meal_catalogue_planner.entity.MealPlan;
import com.example.meal_catalogue_planner.entity.MealType;
import com.example.meal_catalogue_planner.entity.UserAccount;
import com.example.meal_catalogue_planner.repository.FoodItemRepository;
import com.example.meal_catalogue_planner.repository.MealPlanRepository;
import com.example.meal_catalogue_planner.repository.UserAccountRepository;

@ExtendWith(MockitoExtension.class)
class MealPlanServiceTest {
    @Mock
    private MealPlanRepository mealPlanRepository;

    @Mock
    private FoodItemRepository foodItemRepository;

    @Mock
    private UserAccountRepository userAccountRepository;

    @InjectMocks
    private MealPlanService mealPlanService;

    @Test
    void createMealPlanAttachesAuthenticatedUserAndCalculatesTotalCalories() {
        UserAccount userAccount = user("sam@example.com");
        FoodItem rice = food(10L, "Rice", 200);
        FoodItem egg = food(11L, "Egg", 80);
        MealPlanRequest request = new MealPlanRequest(
            "Lunch prep",
            LocalDate.of(2026, 9, 24),
            MealType.LUNCH,
            "keep it simple",
            List.of(
                new MealPlanItemRequest(10L, BigDecimal.valueOf(1.5)),
                new MealPlanItemRequest(11L, BigDecimal.valueOf(2))
            )
        );

        when(userAccountRepository.findByEmailIgnoreCase("sam@example.com")).thenReturn(Optional.of(userAccount));
        when(foodItemRepository.findById(10L)).thenReturn(Optional.of(rice));
        when(foodItemRepository.findById(11L)).thenReturn(Optional.of(egg));
        when(mealPlanRepository.save(any(MealPlan.class))).thenAnswer(invocation -> invocation.getArgument(0));

        MealPlanResponse response = mealPlanService.createMealPlan("sam@example.com", request);

        assertThat(response.name()).isEqualTo("Lunch prep");
        assertThat(response.items()).hasSize(2);
        assertThat(response.totalCalories()).isEqualByComparingTo("460.0");

        ArgumentCaptor<MealPlan> mealPlanCaptor = ArgumentCaptor.forClass(MealPlan.class);
        verify(mealPlanRepository).save(mealPlanCaptor.capture());
        MealPlan savedPlan = mealPlanCaptor.getValue();
        assertThat(savedPlan.getUserAccount()).isSameAs(userAccount);
        assertThat(savedPlan.getItems()).allSatisfy(item -> assertThat(item.getMealPlan()).isSameAs(savedPlan));
    }

    @Test
    void createMealPlanFailsWhenReferencedFoodDoesNotExist() {
        when(userAccountRepository.findByEmailIgnoreCase("sam@example.com")).thenReturn(Optional.of(user("sam@example.com")));
        when(foodItemRepository.findById(99L)).thenReturn(Optional.empty());

        MealPlanRequest request = new MealPlanRequest(
            "Missing food",
            LocalDate.of(2026, 9, 24),
            MealType.DINNER,
            null,
            List.of(new MealPlanItemRequest(99L, BigDecimal.ONE))
        );

        assertThatThrownBy(() -> mealPlanService.createMealPlan("sam@example.com", request))
            .isInstanceOf(ResponseStatusException.class)
            .satisfies(ex -> assertThat(((ResponseStatusException) ex).getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND));

        verify(mealPlanRepository, never()).save(any());
    }

    @Test
    void getMealPlansFiltersByAuthenticatedUserDateAndMealType() {
        MealPlan mealPlan = new MealPlan();
        mealPlan.setName("Breakfast");
        mealPlan.setMealDate(LocalDate.of(2026, 9, 24));
        mealPlan.setMealType(MealType.BREAKFAST);
        mealPlan.setNotes("oats");

        when(mealPlanRepository.findByUserAccountEmailIgnoreCaseAndMealDateAndMealType(
            "sam@example.com",
            LocalDate.of(2026, 9, 24),
            MealType.BREAKFAST
        )).thenReturn(List.of(mealPlan));

        List<MealPlanResponse> responses = mealPlanService.getMealPlans(
            "sam@example.com",
            LocalDate.of(2026, 9, 24),
            MealType.BREAKFAST
        );

        assertThat(responses).hasSize(1);
        assertThat(responses.getFirst().name()).isEqualTo("Breakfast");
        verify(mealPlanRepository).findByUserAccountEmailIgnoreCaseAndMealDateAndMealType(
            "sam@example.com",
            LocalDate.of(2026, 9, 24),
            MealType.BREAKFAST
        );
    }

    private static UserAccount user(String email) {
        UserAccount userAccount = new UserAccount();
        userAccount.setUsername("sam");
        userAccount.setEmail(email);
        return userAccount;
    }

    private static FoodItem food(Long id, String name, int calories) {
        FoodItem foodItem = new FoodItem();
        foodItem.setId(id);
        foodItem.setName(name);
        foodItem.setCalories(calories);
        return foodItem;
    }
}
