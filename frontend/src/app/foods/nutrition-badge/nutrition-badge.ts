import { Component, Input } from "@angular/core";
import { NgClass } from '@angular/common';

@Component({
	selector: 'app-nutrition-badge',
	templateUrl: './nutrition-badge.html',
	styleUrl: './nutrition-badge.css',
 imports: [NgClass]
})
export class NutritionBadge {
	// Parent sends calories into its component.
	@Input() calories!: number

	  // Return badge color based on calorie level.
  get badgeClass(): string {
    if (this.calories >= 600) {
      return 'badge-error';
    }

    if (this.calories >= 300) {
      return 'badge-warning';
    }

    return 'badge-success';
  }

	// Set different badge text based on calories.
	get level(): string {
		if (this.calories >= 600) return 'high'
		if (this.calories >= 300) return 'medium'
		return 'low'
	}
}