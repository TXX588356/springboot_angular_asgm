import { Routes } from '@angular/router';
import { FoodList } from './foods/food-list';

export const routes: Routes = [
	{
		path: '',
		redirectTo: 'foods',
		pathMatch: 'full',
	}, 
	{
		path: 'foods',
		component: FoodList,
	}
];
