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

    constructor(private http: HttpClient) {}

    getMealPlans(mealDate?: string, mealType?: MealType | ''): Observable<MealPlanResponse[]> {
        let params = new HttpParams()

        if (mealDate) {
            params = params.set('mealDate', mealDate)
        }

        if (mealType) {
            params = params.set('mealDate', mealType)
        }

        return this.http.get<MealPlanResponse[]>(this.apiUrl, { params })
    }

    getMealPlanById(id: number): Observable<MealPlanResponse> {
        return this.http.get<MealPlanResponse>(`${this.apiUrl}/${id}`)
    }

    createMealPlan(request: MealPlanRequest): Observable<MealPlanResponse> {
        return this.http.post<MealPlanResponse>(this.apiUrl, request)
    }

    updateMelaPlan(id: number, request: MealPlanRequest): Observable<MealPlanResponse> {
        return this.http.put<MealPlanResponse>(`${this.apiUrl}/${id}`, request)
    }

    deleteMealPlan(id: number): Observable<void> {
        return this.http.delete<void>(`${this.apiUrl}/${id}`)
    }
}