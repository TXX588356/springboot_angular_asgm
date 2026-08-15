import { Component, Inject, OnInit, PLATFORM_ID, signal } from "@angular/core";
import { isPlatformBrowser } from "@angular/common";
import { FoodItem } from "../models/food-item";
import { FormsModule } from "@angular/forms";
import { FoodService } from "../services/food.service";
import { FoodCard } from "./food-card/food-card";
import { ActivatedRoute, Router, RouterLink } from "@angular/router";

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
        private route: ActivatedRoute,
        private router: Router,
        @Inject(PLATFORM_ID) private platformId: object,
    ) {}

    // Load foods when the page opens.
    ngOnInit(): void {
        if (!isPlatformBrowser(this.platformId)) {
            return
        }

        // Angular requirement 12: restore search/filter/sort state from routed query parameters.
        const params = this.route.snapshot.queryParamMap
        this.searchText = params.get('name') ?? ''
        this.category = params.get('category') ?? ''
        this.sortBy = params.get('sortBy') ?? 'name'
        this.direction = params.get('direction') ?? 'asc'

        const maxCaloriesParam = params.get('maxCalories')
        this.maxCalories = maxCaloriesParam ? Number(maxCaloriesParam) : undefined

        this.applyQuery(false)
    }

    applyQuery(updateRoute = true): void {
        if (updateRoute) {
            // Angular requirement 12: pass filters as query parameters between routed views.
            this.router.navigate([], {
                relativeTo: this.route,
                queryParams: {
                    name: this.searchText || null,
                    category: this.category || null,
                    maxCalories: this.maxCalories ?? null,
                    sortBy: this.sortBy,
                    direction: this.direction,
                },
                queryParamsHandling: 'merge',
            })
        }

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
                this.currentPage = 1
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

    pageSize = 6
    currentPage = 1

    get totalPages(): number {
        return Math.ceil(this.foods().length / this.pageSize)
    }

    get paginatedFoods(): FoodItem[] {
        const start = (this.currentPage - 1) * this.pageSize
        return this.foods().slice(start, start + this.pageSize)
    }

    goToPage(page: number): void {
        if (page < 1 || page > this.totalPages) {
            return
        }

        this.currentPage = page
    }

    changePageSize(size: string): void {
        this.pageSize = Number(size)
        this.currentPage = 1
    }

}
