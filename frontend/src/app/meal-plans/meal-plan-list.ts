import { Component, OnInit, signal } from "@angular/core";
import { FormsModule } from "@angular/forms";
import { RouterLink } from "@angular/router";
import { MealPlanResponse } from "../models/meal-plan-response";
import { MealType } from "../models/meal-type";
import { MealPlanService } from "../services/meal-plan-service";

@Component({
    selector: 'app-meal-plan-list',
    imports: [FormsModule, RouterLink],
    templateUrl: './meal-plan-list.html',
    styleUrl: 'meal-plan-list.css'
})
export class MealPlanList implements OnInit {
    // Signals hold server state so the template updates when HTTP responses arrive.
    mealPlans = signal<MealPlanResponse[]>([])
    // Indicates whether the meal-plan list is loading.
    loading = signal<boolean>(false)
    // Stores the list-level error message.
    error = signal<string>('')

    // Current date filter value.
    mealDate = ''
    // Current meal-type filter value.
    mealType: MealType | '' = ''

    // Injects the service used to query and delete meal plans.
    constructor(private mealPlanService: MealPlanService) {}

    // Loads meal plans when the list page opens.
    ngOnInit(): void {
        this.loadMealPlans()
    }

    // Loads meal plans using the current filter values.
    loadMealPlans(): void {
        this.loading.set(true)
        this.error.set('')

        this.mealPlanService.getMealPlans(this.mealDate, this.mealType).subscribe({
            next: (response) => {
                // Store the backend response as the current list displayed by the template.
                this.mealPlans.set(response)
                this.loading.set(false)
            },
            error: () => {
                this.error.set('Failed to load meal plans')
                this.loading.set(false)
            }
        })
    }

    // Resets filters and reloads the full meal-plan list.
    clearFilters(): void {
        this.mealDate = ''
        this.mealType = ''
        this.loadMealPlans()
    }

    // Deletes one meal plan and refreshes the list.
    deleteMealPlan(id: number): void {
        this.mealPlanService.deleteMealPlan(id).subscribe({
            next: () => {
                this.loadMealPlans()
            },
            error: () => {
                this.error.set('Failed to delete a meal plan')
                this.loading.set(false)
            }
        })
    }
    
}  
