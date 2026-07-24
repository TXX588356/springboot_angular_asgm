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

        return this.http.get<FoodItem[]>(this.apiUrl, { params })
    }

    // Get one food by ID
    getFoodById(id: number): Observable<FoodItem> {
        return this.http.get<FoodItem>(`${this.apiUrl}/${id}`)
    }

    // Create one food
    createFood(food: FoodRequest): Observable<FoodItem> {
        return this.http.post<FoodItem>(this.apiUrl, food)
    }

    // Update food
    updateFood(id: number, food: FoodRequest): Observable<FoodItem> {
        return this.http.put<FoodItem>(`${this.apiUrl}/${id}`, food)
    }

    // Search foods by name.
    searchFoods(name: string): Observable<FoodItem[]> {
        const params = new HttpParams().set('name', name)

        return this.http.get<FoodItem[]>(`${this.apiUrl}/search`, { params })
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

        return this.http.get<FoodItem[]>(`${this.apiUrl}/filter`, { params })
    }

    // Delete a food by ID
    deleteFood(id: number): Observable<void> {
        return this.http.delete<void>(`${this.apiUrl}/${id}`)
    }
}