import { Component } from "@angular/core";
import { Router } from "@angular/router";

@Component({
    selector: 'app-not-found',
    templateUrl: './not-found.html',
    styleUrl: './not-found.css'
})
export class NotFound {
    // Injects Router for programmatic navigation from the 404 page.
    constructor(private router: Router) {}

    // Sends the user back to the dashboard.
    goToDashboard(): void {
        // Programmatic navigation
        this.router.navigate(['/dashboard'])
    }
}
