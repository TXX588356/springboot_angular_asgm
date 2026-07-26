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
    mealPlans = signal<MealPlanResponse[]>([])
    loading = signal<boolean>(false)
    error = signal<string>('')

    mealDate = ''
    mealType: MealType | '' = ''

    constructor(private mealPlanService: MealPlanService) {}

    ngOnInit(): void {
        this.loadMealPlans()
    }

    loadMealPlans(): void {
        this.loading.set(true)
        this.error.set('')

        this.mealPlanService.getMealPlans(this.mealDate, this.mealType).subscribe({
            // get data from backend after successful HTTP requests.
            next: (response) => {
                // Put backend response into the signal. (this.mealPlans)
                this.mealPlans.set(response)
                this.loading.set(false)
            },
            error: () => {
                this.error.set('Failed to load meal plans')
                this.loading.set(false)
            }
        })
    }

    clearFilters(): void {
        this.mealDate = ''
        this.mealType = ''
        this.loadMealPlans()
    }

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
