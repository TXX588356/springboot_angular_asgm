// Response row for one food item inside a meal plan.
export interface MealPlanItemResponse {
    // Database ID of the meal plan item row.
    id: number
    // Referenced food catalogue item ID.
    foodItemId: number
    // Display name of the referenced food item.
    foodName: string
    // Calories per serving.
    calories: number
    // Number of servings or units.
    quantity: number
    // Calories for this row after multiplying calories by quantity.
    lineSubtotalCalories: number
}
