import { Injectable } from "@angular/core";
import { FoodItem } from "../models/food-item";
import { Observable } from "rxjs";
import { HttpClient, HttpParams } from "@angular/common/http";
import { FoodRequest } from "../models/food-request";

@Injectable({
    providedIn: 'root'
})

export class FoodService {
    private apiUrl = '/api/foods'

    constructor(private http: HttpClient) {}

    // Get all foods with optional applied fields
    getFoods(
        name: string,
        category: string,
        maxCalories: number | undefined,
        sortBy: string, 
        direction: string,
    ): Observable<FoodItem[]> {
        let params = new HttpParams()
        .set('sortBy', sortBy)
        .set('direction', direction)

        if (name.trim()) {
            params = params.set('name', name.trim())
        }

        if (category.trim()) {
            params = params.set('category', category.trim())
        }

        if (maxCalories !== undefined && maxCalories !== null) {
            params = params.set('maxCalories', maxCalories)
        }

        // Angular requirement 10: HttpClient returns an Observable for async GET requests with query params.
        return this.http.get<FoodItem[]>(this.apiUrl, { params, withCredentials: true })
    }

    // Get one food by ID
    getFoodById(id: number): Observable<FoodItem> {
        return this.http.get<FoodItem>(`${this.apiUrl}/${id}`, { withCredentials: true })
    }

    // Create one food
    createFood(food: FoodRequest): Observable<FoodItem> {
        // Angular requirement 10: POST creates a backend food record asynchronously.
        return this.http.post<FoodItem>(this.apiUrl, food, { withCredentials: true })
    }

    // Update food
    updateFood(id: number, food: FoodRequest): Observable<FoodItem> {
        // Angular requirement 10: PUT updates a backend food record asynchronously.
        return this.http.put<FoodItem>(`${this.apiUrl}/${id}`, food, { withCredentials: true })
    }

    // Search foods by name.
    searchFoods(name: string): Observable<FoodItem[]> {
        const params = new HttpParams().set('name', name)

        return this.http.get<FoodItem[]>(`${this.apiUrl}/search`, { params, withCredentials: true })
    }

    // Filter foods by cat and max calories.
    filterFoods(category?: string, maxCalories?: number): Observable<FoodItem[]> {
        let params = new HttpParams()

        if (category) {
            params = params.set('category', category)
        }
        if (maxCalories !== undefined) {
            params = params.set('maxCalories', maxCalories)
        }

        return this.http.get<FoodItem[]>(`${this.apiUrl}/filter`, { params, withCredentials: true })
    }

    // Delete a food by ID
    deleteFood(id: number): Observable<void> {
        // Angular requirement 10: DELETE removes a backend food record asynchronously.
        return this.http.delete<void>(`${this.apiUrl}/${id}`, { withCredentials: true })
    }
}
