import { Component, OnInit, signal } from "@angular/core";
import { FoodItem } from "../../models/food-item";
import { ActivatedRoute } from "@angular/router";
import { FoodService } from "../../services/food.service";

@Component({
    selector: 'app-food-nutrition',
    templateUrl: './food-nutrition.html',
    styleUrl: './food-nutrition.css',
})
export class FoodNutrition implements OnInit {
    // Food item whose nutrition values are being shown.
    food = signal<FoodItem | null>(null)
    // Indicates whether nutrition data is loading.
    loading = signal<boolean>(false)
    // Stores the nutrition-view error message.
    error = signal<string>('')

    // Injects access to the parent route ID and food API service.
    constructor(
        private route: ActivatedRoute,
        private foodService: FoodService
    ) {}

    // Parent route is /foods/:id, so read ID from parent route.
    ngOnInit(): void {
        const id = Number(this.route.parent?.snapshot.paramMap.get('id'))

        this.loading.set(true)

        this.foodService.getFoodById(id).subscribe({
            next: (food) => {
                this.food.set(food)
                this.loading.set(false)
            },
            error: () => {
                this.error.set('Nutrition details not found')
                this.loading.set(false)
            }
        })
    }
}
