import { Routes } from '@angular/router';
import { FoodList } from './foods/food-list';
import { FoodForm } from './foods/food-form/food-form';
import { FoodDetail } from './foods/food-detail/food-detail';
import { FoodNutrition } from './foods/food-nutrition/food-nutrition';
import { MealPlanList } from './meal-plans/meal-plan-list'
// import { MealPlanForm } from './meal-plans/meal-plan-form/meal-plan-form'
// import { MealPlanDetail } from './meal-plans/meal-plan-detail/meal-plan-detail'

export const routes: Routes = [
	{
		path: '',
		redirectTo: 'foods',
		pathMatch: 'full',
	}, 
	{
		path: 'foods',
		component: FoodList,
	},
	{
		path: 'foods/new',
		component: FoodForm,
	},
	{
		path: 'foods/:id/edit',
		component: FoodForm,
	},
	{
		path: 'foods/:id',
		component: FoodDetail,
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
	},
	// {
	// 	path: 'meal-plans/new',
	// 	component: MealPlanForm,
	// },
	// {
	// 	path: 'meal-plans/:id/edit',
	// 	component: MealPlanForm,
	// },
	// {
	// 	path: 'meal-plans/:id',
	// 	component: MealPlanDetail,
	// },
	{
		path: '*',
		redirectTo: "/foods"
	}
];
