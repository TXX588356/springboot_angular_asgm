// Payload row for one food item inside a meal plan request.
export interface MealPlanItemRequest {
    // Selected food catalogue item ID.
    foodItemId: number
    // Number of servings or units.
    quantity: number
}
