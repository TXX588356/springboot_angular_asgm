import { MealPlanItemRequest } from "./meal-plan-item-request";
import { MealType } from "./meal-type";

export interface MealPlanRequest {
    name: string,
    mealDate: string,
    mealType: MealType,
    notes: string,
    items: MealPlanItemRequest[]
}