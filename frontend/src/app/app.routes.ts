import { Routes } from '@angular/router';
import { FoodList } from './foods/food-list';
import { FoodForm } from './foods/food-form/food-form';

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
	}
];
