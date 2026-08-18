import { MealPlanItemResponse } from "./meal-plan-item-response"
import { MealType } from "./meal-type"

// Meal plan data returned by the backend.
export interface MealPlanResponse {
    // Database ID of the meal plan.
    id: number
    // User-facing meal plan name.
    name: string
    // Calendar date in backend-compatible string form.
    mealDate: string
    // Meal slot for the plan.
    mealType: MealType
    // Optional notes entered by the user.
    notes: string
    // Food rows included in the meal plan.
    items: MealPlanItemResponse[]
    // Total calories across all meal plan item rows.
    totalCalories: number
}
