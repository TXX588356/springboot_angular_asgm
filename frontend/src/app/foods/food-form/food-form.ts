import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from "@angular/forms";
import { FoodService } from "../../services/food.service";
import { Component, OnInit, signal } from "@angular/core";
import { ActivatedRoute, Router } from "@angular/router";
import { FoodRequest } from "../../models/food-request";

@Component({
    selector: 'app-food-form',
    imports: [ReactiveFormsModule],
    templateUrl: './food-form.html',
    styleUrl: './food-form.css'
})
export class FoodForm implements OnInit {
    loading = signal<boolean>(false)
    error = signal<string>('')

    foodId: number | null = null
    isEditMode = false

		foodForm: FormGroup

    constructor(
        private fb: FormBuilder,
        private foodService: FoodService,
        private route: ActivatedRoute,
        private router: Router,
    ) {
			  this.foodForm = this.fb.group({
        name: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(120)]],
        category: ['', [Validators.required]],
        servingSize: ['', Validators.required],
        calories: [0, [Validators.required, Validators.min(0)]],
        price: [0, [Validators.required, Validators.min(0)]],

        nutrition: this.fb.group({
            protein: [0, [Validators.min(0)]],
            carbohydrates: [0, [Validators.min(0)]],
            fat: [0, [Validators.min(0)]],
        })
    })
		}



    // Decide whether this page is create mode or edit mode.
    ngOnInit(): void {
        const idParam = this.route.snapshot.paramMap.get('id')

        if (idParam) {
            this.foodId = Number(idParam)
            this.isEditMode = true
            this.loadFood(this.foodId)
        }
    }

    // Load existing food data into the food form for edit mode
    loadFood(id: number): void {
        this.loading.set(true)
        this.error.set('')

        this.foodService.getFoodById(id).subscribe({
            next: (food) => {
                this.foodForm.patchValue({
                    name: food.name,
                    category: food.category,
                    servingSize: food.servingSize,
                    calories: food.calories,
                    price: food.price,
                    nutrition: {
                        protein: food.protein,
                        carbohydrates: food.carbohydrates,
                        fat: food.fat,
                    }                
                })

                this.loading.set(false)
            },
            error: () => {
                this.error.set('Failed to load food')
                this.loading.set(false)
            }
        })
    }

    // Convert form value into the asme JSON shape expected
    buildRequest(): FoodRequest {
			const value = this.foodForm.getRawValue()

			return {
				name: value.name ?? '',
				category: value.category ?? '',
				servingSize: value.servingSize ?? '',
				calories: Number(value.calories ?? 0),
				price: Number(value.price ?? 0),
				protein: Number(value.nutrition?.protein ?? 0),
				carbohydrates: Number(value.nutrition?.carbohydrates ?? 0),
				fat: Number(value.nutrition?.fat ?? 0),
			}
    }

    // Submit create or update req
		saveFood(): void {
			if (this.foodForm.invalid) {
				this.foodForm.markAllAsTouched()
				return
			}

			const request = this.buildRequest()

			this.loading.set(true)
			this.error.set('')

			if (this.isEditMode && this.foodId != null) {
				this.foodService.updateFood(this.foodId, request).subscribe({
					next: () => {
						this.router.navigate(['/foods'])
					},
					error: () => {
						this.error.set('Failed to update food')
						this.loading.set(false)
					}
				})

				return
			}

			this.foodService.createFood(request).subscribe({
				next: () => {
					this.router.navigate(['/foods'])
				},
				error: () => {
					this.error.set('Failed to create food')
					this.loading.set(false)
				}
			})
		}

		// Go back to food list without saving
		cancel(): void {
			this.router.navigate(['/foods'])
		}
}
