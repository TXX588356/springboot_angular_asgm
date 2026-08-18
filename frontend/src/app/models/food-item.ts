// Food catalogue item returned by the backend.
export interface FoodItem {
    // Database ID of the food item.
    id: number,
    // Food item display name.
    name: string,
    // Primary category label.
    category: string,
    // Calories per serving.
    calories: number,
    // Protein amount per serving.
    protein: number,
    // Carbohydrate amount per serving.
    carbohydrates: number,
    // Fat amount per serving.
    fat: number,
    // Human-readable serving size.
    servingSize: string, 
    // Price per serving or item.
    price: number,
}
