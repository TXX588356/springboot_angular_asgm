import { Component, OnInit, signal } from "@angular/core";
import { RouterLink } from "@angular/router";
import { FoodItem } from "../models/food-item";
import { MealPlanResponse } from "../models/meal-plan-response";
import { FoodService } from "../services/food.service";
import { MealPlanService } from "../services/meal-plan-service";

@Component({
    selector: 'app-dashboard',
    imports: [RouterLink],
    templateUrl: './dashboard.html',
    styleUrl: './dashboard.css'
})
export class Dashboard implements OnInit {
    foods = signal<FoodItem[]>([])
    mealPlans = signal<MealPlanResponse[]>([])
    loading = signal<boolean>(false)
    error = signal<string>('')

    constructor(
        private foodService: FoodService,
        private mealPlanService: MealPlanService,
    ) {}

    ngOnInit(): void {
        this.loadDashboardData()
    }

    loadDashboardData(): void {
        this.loading.set(true)
        this.error.set('')

        // Load both modules so the dashboard summary reflects the current backend data.
        this.foodService.getFoods('', '', undefined, 'name', 'asc').subscribe({
            next: (foods) => {
                this.foods.set(foods)
                this.loadMealPlans()
            },
            error: () => {
                this.error.set('Failed to load dashboard data')
                this.loading.set(false)
            }
        })

    }

    private loadMealPlans(): void {
        this.mealPlanService.getMealPlans().subscribe({
            next: (mealPlans) => {
                this.mealPlans.set(mealPlans)
                this.loading.set(false)
            },
            error: () => {
                this.error.set('Failed to load dashboard data')
                this.loading.set(false)
            }
        })
    }

    get totalFoods(): number {
        return this.foods().length
    }

    get categoryCount(): number {
        return new Set(this.foods().map((food) => food.category)).size
    }

    get averageCalories(): number {
        if (this.foods().length === 0) {
            return 0
        }

        const totalCalories = this.foods().reduce((total, food) => total + food.calories, 0)

        return Math.round(totalCalories / this.foods().length)
    }

    get mealPlanCount(): number {
        return this.mealPlans().length
    }
}