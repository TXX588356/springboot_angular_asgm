import { Routes } from '@angular/router';
import { FoodList } from './foods/food-list';
import { FoodForm } from './foods/food-form/food-form';
import { FoodDetail } from './foods/food-detail/food-detail';
import { FoodNutrition } from './foods/food-nutrition/food-nutrition';
import { MealPlanList } from './meal-plans/meal-plan-list'
import { MealPlanForm } from './meal-plans/meal-plan-form'
import { MealPlanDetail } from './meal-plans/meal-plan-detail'
import { Dashboard } from './dashboard/dashboard';
import { NotFound } from './not-found/not-found';
import { Login } from './auth/login/login';
import { Register } from './auth/register/register';
import { authGuard } from './auth/auth.guard';

export const routes: Routes = [
	// Angular requirements 11 and 13: routes include redirect, wildcard, parameterized paths, and a nested nutrition child route.
	{
		path: '',
		redirectTo: 'dashboard',
		pathMatch: 'full',
	}, 
	{
		path: 'login',
		component: Login,
	},
	{
		path: 'register',
		component: Register,
	},
	{
		path: 'foods',
		component: FoodList,
		canActivate: [authGuard],
	},
	{
		path: 'foods/new',
		component: FoodForm,
		canActivate: [authGuard],
	},
	{
		path: 'foods/:id/edit',
		component: FoodForm,
		canActivate: [authGuard],
	},
	{
		path: 'foods/:id',
		component: FoodDetail,
		canActivate: [authGuard],
		children: [
			{
				path: 'nutrition',
				component: FoodNutrition
			}
		]
	},
	{
		path: 'meal-plans',
		component: MealPlanList,
		canActivate: [authGuard],
	},
	{
		path: 'meal-plans/new',
		component: MealPlanForm,
		canActivate: [authGuard],
	},
	{
		path: 'meal-plans/:id/edit',
		component: MealPlanForm,
		canActivate: [authGuard],
	},
	{
		path: 'meal-plans/:id',
		component: MealPlanDetail,
		canActivate: [authGuard],
	},
	{
		path: 'dashboard',
		component: Dashboard,
		canActivate: [authGuard],
	},
	{
		path: '**',
		component: NotFound
	}
];
