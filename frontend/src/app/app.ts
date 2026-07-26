import { Component, OnInit, signal } from '@angular/core';
import { NavigationEnd, Router, RouterLink, RouterOutlet } from '@angular/router';
import { filter } from 'rxjs';

@Component({
  selector: 'app-root',
  imports: [RouterLink, RouterOutlet],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App implements OnInit {
  currentUrl = signal<string>('/dashboard')

  constructor(private router: Router) {}

  ngOnInit(): void {
    this.currentUrl.set(this.router.url)

    // Keep the navbar in sync with the active page after each completed navigation.
    this.router.events
      .pipe(filter((event): event is NavigationEnd => event instanceof NavigationEnd))
      .subscribe((event) => {
        this.currentUrl.set(event.urlAfterRedirects)
      })
  }

  get isDashboardPage(): boolean {
    return this.currentUrl().startsWith('/dashboard')
  }

  get isFoodPage(): boolean {
    return this.currentUrl().startsWith('/foods')
  }

  get isMealPlanPage(): boolean {
    return this.currentUrl().startsWith('/meal-plans')
  }
}
