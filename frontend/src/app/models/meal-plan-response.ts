import { MealPlanItemResponse } from "./meal-plan-item-response"
import { MealType } from "./meal-type"

export interface MealPlanResponse {
    id: number
    name: string
    mealDate: string
    mealType: MealType
    notes: string
    items: MealPlanItemResponse[]
    totalCalories: number
}