import { Component, OnInit, signal } from "@angular/core"
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from "@angular/forms"
import { FoodItem } from "../models/food-item"
import { MealType } from "../models/meal-type"
import { ActivatedRoute, Router } from "@angular/router"
import { FoodService } from "../services/food.service"
import { MealPlanService } from "../services/meal-plan-service"
import { MealPlanRequest } from "../models/meal-plan-requests"
import { MealPlanItemRequest } from "../models/meal-plan-item-request"

interface SelectedMealPlanItem {
    foodItemId: number
    foodName: string
    calories: number
    quantity: number
    lineSubtotalCalories: number
}

@Component({
    selector: 'app-meal-plan-form',
    imports: [ReactiveFormsModule],
    templateUrl: './meal-plan-form.html',
    styleUrl: './meal-plan-form.css'
})
export class MealPlanForm implements OnInit {
    loading = signal<boolean>(false)
    error = signal<string>('')

    // Catalogue foods are loaded so users can choose existing foods for the meal plan.
    foods = signal<FoodItem[]>([])

    // Selected items are kept outside the form because they are a repeatable table, not one input.
    selectedItems = signal<SelectedMealPlanItem[]>([])

    mealPlanId: number | null = null
    isEditMode = false

    // Used by the template to render meal type options.
    mealTypes: MealType[] = ['BREAKFAST', 'LUNCH', 'DINNER', 'SNACK']

    mealPlanForm: FormGroup

    constructor(
        private fb: FormBuilder,
        private route: ActivatedRoute,
        private router: Router,
        private foodService: FoodService,
        private mealPlanService: MealPlanService
    ) {
        // foodItemId and quantity are temporary controls used to add rows into selectedItems.
        this.mealPlanForm = this.fb.group({
            name: ['', [Validators.required]],
            mealDate: ['', [Validators.required]],
            mealType: ['LUNCH', [Validators.required]],
            notes: [''],
            foodItemId: ['', [Validators.required]],
            quantity: [1, [Validators.required, Validators.min(0.01)]],
        })
    }

    ngOnInit(): void {
        this.loadFoods()

        const idParam = this.route.snapshot.paramMap.get('id')

        // Presence of an ID switches the reusable form from create mode to edit mode.
        if (idParam) {
            this.mealPlanId = Number(idParam)
            this.isEditMode = true
            this.loadMealPlan(this.mealPlanId)
        }
    }

    loadFoods(): void {
        // Load all foods so the user can build a meal plan from catalogue records.
        this.foodService.getFoods('', '', undefined, 'name', 'asc').subscribe({
            next: (foods) => {
                this.foods.set(foods)
            },
            error: () => {
                this.error.set('Failed to load foods')
            }
        })
    }

    loadMealPlan(id: number): void {
        this.loading.set(true)
        this.error.set('')

        this.mealPlanService.getMealPlanById(id).subscribe({
            next: (mealPlan) => {
                this.mealPlanForm.patchValue({
                    name: mealPlan.name,
                    mealDate: mealPlan.mealDate, 
                    mealType: mealPlan.mealType,
                    notes: mealPlan.notes ?? '',
                })

                this.selectedItems.set(
                    // Convert persisted response items back into the editable selected-items table.
                    mealPlan.items.map((item) => ({
                        foodItemId: item.foodItemId,
                        foodName: item.foodName,
                        calories: item.calories,
                        quantity: item.quantity,
                        lineSubtotalCalories: item.lineSubtotalCalories,
                    }))
                )

                this.loading.set(false)
            },
            error: () => {
                this.error.set('Failed to load meal plan')
                this.loading.set(false)
            }
        })
    }

    addSelectedFood(): void {
        // Read the temporary food selector and quantity input.
        const foodItemId = Number(this.mealPlanForm.get('foodItemId')?.value)
        const quantity = Number(this.mealPlanForm.get('quantity')?.value)

        if (!foodItemId || quantity <= 0) {
            this.mealPlanForm.get('foodItemId')?.markAsTouched()
            this.mealPlanForm.get('quantity')?.markAsTouched()
            return
        }

        const food = this.foods().find((item) => item.id === foodItemId)

        if (!food) {
            this.error.set('Selected food was not found')
            return
        }

        const existingItems = this.selectedItems()
        const existingItem = existingItems.find((item) => item.foodItemId === foodItemId)

        if (existingItem) {
            // If the same food is added again, increase its quantity instead of duplicating the row.
            const updatedItems = existingItems.map((item) => {
                if(item.foodItemId !== foodItemId) {
                    return item
                }

                const updatedQuantity = item.quantity + quantity

                return {
                    ...item,
                    quantity: updatedQuantity,
                    lineSubtotalCalories: item.calories * updatedQuantity,
                }
            })

            this.selectedItems.set(updatedItems)
        } else {
            // Add a new display row with enough food data to show subtotals immediately.
            this.selectedItems.set([
                ...existingItems,
                {
                    foodItemId: food.id,
                    foodName: food.name,
                    calories: food.calories,
                    quantity,
                    lineSubtotalCalories: food.calories * quantity,
                }
            ])
        }

        // Reset only the item picker controls after adding one row.
        this.mealPlanForm.patchValue({
            foodItemId: '',
            quantity: 1,
        })
    }

    removeSelectedFood(foodItemId: number): void {
        // Remove a selected row without changing the rest of the form
        this.selectedItems.set(
            this.selectedItems().filter((item) => item.foodItemId !== foodItemId)
        )
    }

    get totalCalories(): number {
        // Running total shown in the form before saving.
        return this.selectedItems().reduce((total, item) => total + item.lineSubtotalCalories, 0)
    }

    buildRequest(): MealPlanRequest {
        const value = this.mealPlanForm.getRawValue()

        // Backend only needs IDs and quantities, not the full food display data.
        const items: MealPlanItemRequest[] = this.selectedItems().map((item) => ({
            foodItemId: item.foodItemId,
            quantity: item.quantity,
        }))

        return {
            name: value.name ?? '',
            mealDate: value.mealDate ?? '',
            mealType: value.mealType,
            notes: value.notes ?? '',
            items,
        }
    }

    saveMealPlan(): void {
        // Only validate the actual meal-plan metadata here; foodItemId is just a temporary picker.
         if (this.mealPlanForm.get('name')?.invalid ||
            this.mealPlanForm.get('mealDate')?.invalid ||
            this.mealPlanForm.get('mealType')?.invalid) {
            this.mealPlanForm.markAllAsTouched()
            return
        }

        if (this.selectedItems().length === 0) {
            this.error.set('Add at least one food item')
            return
        }

        const request = this.buildRequest()

        this.loading.set(true)
        this.error.set('')

        if (this.isEditMode && this.mealPlanId != null) {
            this.mealPlanService.updateMealPlan(this.mealPlanId, request).subscribe({
                next: (mealPlan) => {
                    this.router.navigate(['/meal-plans', mealPlan.id])
                },
                error: () => {
                    this.error.set('Failed to update meal plan')
                    this.loading.set(false)
                }
            })

            return
        }

        this.mealPlanService.createMealPlan(request).subscribe({
            next: (mealPlan) => {
                this.router.navigate(['/meal-plans', mealPlan.id])
            },
            error: () => {
                this.error.set('Failed to create meal plan')
                this.loading.set(false)
            }
        })
    }

    cancel(): void {
        // Edit mode returns to detail; create mode returns to meal plan list.
        if (this.isEditMode && this.mealPlanId != null) {
            this.router.navigate(['/meal-plans', this.mealPlanId])
            return
        }

        this.router.navigate(['/meal-plans'])
    }
}
