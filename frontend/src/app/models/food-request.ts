// Payload sent when creating or updating a food item.
export interface FoodRequest {
    // Food item display name.
    name: string
    // Primary category label.
    category: string
    // Calories per serving.
    calories: number
    // Protein amount per serving.
    protein: number
    // Carbohydrate amount per serving.
    carbohydrates: number
    // Fat amount per serving.
    fat: number
    // Human-readable serving size.
    servingSize: string
    // Price per serving or item.
    price: number
}
