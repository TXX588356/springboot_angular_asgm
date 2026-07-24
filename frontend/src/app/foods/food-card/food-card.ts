import { Component, EventEmitter, Input, Output } from "@angular/core";
import { NutritionBadge } from "../nutrition-badge/nutrition-badge";
import { FoodItem } from "../../models/food-item";
import { RouterLink } from "@angular/router";

@Component({
    selector: 'app-food-card',
    imports: [NutritionBadge, RouterLink],
    templateUrl: './food-card.html',
    styleUrl: './food-card.css',
})
export class FoodCard {
    // FoodList sends one food item into this component.
    @Input() food!: FoodItem

    // FoodCard sends the seleced food ID back to FoodList.
    @Output() deleteFood = new EventEmitter<number>()

    // Tell the parent component which food should be deleted.
    onDelete(): void {
        this.deleteFood.emit(this.food.id)
    }
}