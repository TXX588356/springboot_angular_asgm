import { Component } from "@angular/core";
import { Router } from "@angular/router";

@Component({
    selector: 'app-not-found',
    templateUrl: './not-found.html',
    styleUrl: './not-found.css'
})
export class NotFound {
    constructor(private router: Router) {}

    goToDashboard(): void {
        // Programmatic navigation
        this.router.navigate(['/dashboard'])
    }
}