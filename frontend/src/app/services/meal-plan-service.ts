import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { MealPlanResponse } from "../models/meal-plan-response";
import { HttpClient, HttpParams } from "@angular/common/http";
import { MealType } from "../models/meal-type";
import { MealPlanRequest } from "../models/meal-plan-requests";

@Injectable({
    providedIn: 'root'
})
export class MealPlanService {
    private apiUrl = '/api/meal-plans'

    // Injects HttpClient for meal-plan API requests.
    constructor(private http: HttpClient) {}

    // Gets meal plans with optional date and meal-type query filters.
    getMealPlans(mealDate?: string, mealType?: MealType | ''): Observable<MealPlanResponse[]> {
        let params = new HttpParams()

        if (mealDate) {
            params = params.set('mealDate', mealDate)
        }

        if (mealType) {
            params = params.set('mealType', mealType)
        }

        return this.http.get<MealPlanResponse[]>(this.apiUrl, { params, withCredentials: true })
    }

    // Gets one meal plan by ID.
    getMealPlanById(id: number): Observable<MealPlanResponse> {
        return this.http.get<MealPlanResponse>(`${this.apiUrl}/${id}`, { withCredentials: true })
    }

    // Creates a new meal plan.
    createMealPlan(request: MealPlanRequest): Observable<MealPlanResponse> {
        return this.http.post<MealPlanResponse>(this.apiUrl, request, { withCredentials: true })
    }

    // Updates an existing meal plan by ID.
    updateMealPlan(id: number, request: MealPlanRequest): Observable<MealPlanResponse> {
        return this.http.put<MealPlanResponse>(`${this.apiUrl}/${id}`, request, { withCredentials: true })
    }

    // Deletes a meal plan by ID.
    deleteMealPlan(id: number): Observable<void> {
        return this.http.delete<void>(`${this.apiUrl}/${id}`, { withCredentials: true })
    }
}
