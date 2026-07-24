import { Component, Inject, OnInit, PLATFORM_ID, signal } from "@angular/core";
import { isPlatformBrowser } from "@angular/common";
import { FoodItem } from "../models/food-item";
import { FormsModule } from "@angular/forms";
import { FoodService } from "../services/food.service";
import { FoodCard } from "./food-card/food-card";
import { RouterLink } from "@angular/router";

@Component({
    selector: 'app-food-list',
    imports: [FormsModule, FoodCard, RouterLink],
    templateUrl: './food-list.html',
    styleUrl: './food-list.css',
})
export class FoodList implements OnInit {
    foods = signal<FoodItem[]>([])
    loading = signal<boolean>(false)
    error = signal<string>('')

    searchText = ''
    category = ''
    maxCalories: undefined | number = undefined

    sortBy = 'name'
    direction = 'asc'

    constructor(
        private foodService: FoodService,
        @Inject(PLATFORM_ID) private platformId: object,
    ) {}

    // Load foods when the page opens.
    ngOnInit(): void {
        if (!isPlatformBrowser(this.platformId)) {
            return
        }

        this.applyQuery()
    }

    applyQuery(): void {
        this.loading.set(true)
        this.error.set('')

        this.foodService.getFoods(
            this.searchText,
            this.category,
            this.maxCalories,
            this.sortBy,
            this.direction,
        ).subscribe({
            next: (foods) => {
                this.foods.set(foods)
                this.loading.set(false)
            },
            error: () => {
                this.error.set('Failed to load foods')
                this.loading.set(false)
            }
        })
    }

    deleteFood(id: number): void {
        this.foodService.deleteFood(id).subscribe({
            next: () => {
                this.applyQuery()
            },
            error: () => {
                this.error.set('Delete failed')
            }
        })
    }

    // Call GET /api/foods
    // loadFoods(): void {
    //     this.loading.set(true)
    //     this.error.set('')

    //     this.foodService.getFoods(this.sortBy, this.direction).subscribe({
    //         next: (foods) => {
    //             this.foods.set(foods)
    //             this.loading.set(false)
    //         },
    //         error: () => {
    //             this.error.set('Failed to load foods')
    //             this.loading.set(false)
    //         }
    //     })
    // }

    // // Call GET /api/foods/search?name=xxx
    // searchFoods(): void {
    //     // call loadFoods() if trimmed searchText is empty
    //     if (!this.searchText.trim()) {
    //         this.loadFoods()
    //         return
    //     }

    //     this.loading.set(true)


    //     this.foodService.searchFoods(this.searchText).subscribe({
    //         next: (foods) => {
    //             this.foods.set(foods)
    //             this.loading.set(false)
    //         },
    //         error: () => {
    //             this.error.set('Search failed')
    //             this.loading.set(false)
    //         }
    //     })
    // }

    // // Call /api/foods/filter?category=xxxx&maxCalories=yyy
    // filterFoods(): void {
    //     this.loading.set(true)

    //     this.foodService.filterFoods(this.category, this.maxCalories).subscribe({
    //         next: (foods) => {
    //             this.foods.set(foods)
    //             this.loading.set(false)
    //         },
    //         error: () => {
    //             this.error.set('Filter failed')
    //             this.loading.set(false)
    //         }
    //     })
    // }
}
