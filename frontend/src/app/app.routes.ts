import { Routes } from '@angular/router';
import { FoodList } from './foods/food-list';
import { FoodForm } from './foods/food-form/food-form';
import { FoodDetail } from './foods/food-detail/food-detail';
import { FoodNutrition } from './foods/food-nutrition/food-nutrition';

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
		path: '*',
		redirectTo: "/foods"
	}
];
