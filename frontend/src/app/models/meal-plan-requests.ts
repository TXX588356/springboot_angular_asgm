import { MealPlanItemRequest } from "./meal-plan-item-request";
import { MealType } from "./meal-type";

// Payload sent when creating or updating a meal plan.
export interface MealPlanRequest {
    // User-facing meal plan name.
    name: string,
    // Calendar date in backend-compatible string form.
    mealDate: string,
    // Meal slot for the plan.
    mealType: MealType,
    // Optional notes entered by the user.
    notes: string,
    // Food rows included in the meal plan.
    items: MealPlanItemRequest[]
}
