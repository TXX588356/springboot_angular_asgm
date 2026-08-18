import { Component, OnInit, signal } from "@angular/core";
import { ActivatedRoute, RouterLink, RouterOutlet } from "@angular/router";
import { FoodItem } from "../../models/food-item";
import { FoodService } from "../../services/food.service";

@Component({
    selector: 'app-food-detail',
    imports: [RouterLink, RouterOutlet],
    templateUrl: './food-detail.html',
    styleUrl: './food-detail.css',
})
export class FoodDetail implements OnInit {
    // Selected food item loaded from the backend.
    food = signal<FoodItem | null>(null)
    // Indicates whether the detail view is loading.
    loading = signal<boolean>(false)
    // Stores the detail-view error message.
    error = signal<string>('')

    // Injects route access and food API service dependencies.
    constructor(
        private route: ActivatedRoute,
        private foodService: FoodService
    ) {}

    // Loads one food item using the ID from the route.
    ngOnInit(): void {
        const id = Number(this.route.snapshot.paramMap.get('id'))

        this.loading.set(true)

        this.foodService.getFoodById(id).subscribe({
            next: (food) => {
                this.food.set(food)
                this.loading.set(false)
            },
            error: () => {
                this.error.set('Food not found')
                this.loading.set(false)
            }
        })
    }
}
