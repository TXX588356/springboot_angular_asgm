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
    // Food data used for dashboard summary metrics.
    foods = signal<FoodItem[]>([])
    // Meal-plan data used for dashboard summary metrics.
    mealPlans = signal<MealPlanResponse[]>([])
    // Indicates whether dashboard data is currently loading.
    loading = signal<boolean>(false)
    // Stores the dashboard load error message.
    error = signal<string>('')

    // Injects services used to load dashboard summary data.
    constructor(
        private foodService: FoodService,
        private mealPlanService: MealPlanService,
    ) {}

    // Loads dashboard data when the component starts.
    ngOnInit(): void {
        this.loadDashboardData()
    }

    // Loads food data first, then loads meal-plan data for the dashboard.
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

    // Loads meal-plan data after the food request completes.
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

    // Number of food catalogue items.
    get totalFoods(): number {
        return this.foods().length
    }

    // Number of unique food categories.
    get categoryCount(): number {
        return new Set(this.foods().map((food) => food.category)).size
    }

    // Average calories across all food items, rounded to the nearest whole number.
    get averageCalories(): number {
        if (this.foods().length === 0) {
            return 0
        }

        const totalCalories = this.foods().reduce((total, food) => total + food.calories, 0)

        return Math.round(totalCalories / this.foods().length)
    }

    // Number of meal plans owned by the current user.
    get mealPlanCount(): number {
        return this.mealPlans().length
    }
}
