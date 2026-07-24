import { Component, Input } from "@angular/core";

@Component({
	selector: 'app-nutrition-badge',
	templateUrl: './nutrition-badge.html',
	styleUrl: './nutrition-badge.css'
})
export class NutritionBadge {
	// Parent sends calories into its component.
	@Input() calories!: number

	// Set different badge text based on calories.
	get level(): string {
		if (this.calories >= 600) return 'high'
		if (this.calories >= 300) return 'medium'
		return 'low'
	}
}