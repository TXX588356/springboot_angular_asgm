import { Component, OnInit, signal } from "@angular/core";
import { ActivatedRoute, Router, RouterLink } from "@angular/router";
import { MealPlanResponse } from "../models/meal-plan-response";
import { MealPlanService } from "../services/meal-plan-service";

@Component({
    selector: 'app-meal-plan-detail',
    imports: [RouterLink],
    templateUrl: './meal-plan-detail.html',
    styleUrl: './meal-plan-detail.css',
})
export class MealPlanDetail implements OnInit {
    // Null means the detail request has not returned a usable meal plan yet.
    mealPlan = signal<MealPlanResponse | null> (null)
    // Indicates whether the detail view is loading.
    loading = signal<boolean>(false)
    // Stores the detail-view error message.
    error = signal<string>('')

    // Injects route access, navigation, and meal-plan API dependencies.
    constructor(
        private route: ActivatedRoute,
        private router: Router,
        private mealPlanService: MealPlanService
    ) {}

    // Loads the route-selected meal plan when the detail page opens.
    ngOnInit(): void {
        // Detail pages are route-driven, so the meal plan ID comes from /meal-plans/:id.
        const id = Number(this.route.snapshot.paramMap.get('id'))
        this.loadMealPlan(id)
    }

    // Loads one meal plan by ID for display.
    loadMealPlan(id: number): void {
        this.loading.set(true)
        this.error.set('')

        this.mealPlanService.getMealPlanById(id).subscribe({
            next: (mealPlan) => {
                this.mealPlan.set(mealPlan)
                this.loading.set(false)
            },
            error: () => {
                this.error.set('Meal plan not found')
                this.loading.set(false)
            }
        })
    }

    // Deletes the current meal plan and returns to the list.
    deleteMealPlan(): void {
        const currentMealPlan = this.mealPlan()

        // Guard against delete clicks before the detail request has finished.
        if (!currentMealPlan) {
            return
        }

        this.mealPlanService.deleteMealPlan(currentMealPlan.id).subscribe({
            next: () => {
                this.router.navigate(['/meal-plans'])
            },
            error: () => {
                this.error.set('Failed to delete meal plan')
            }
        })
    }
}
