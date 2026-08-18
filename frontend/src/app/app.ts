import { isPlatformBrowser } from '@angular/common';
import { Component, Inject, OnInit, PLATFORM_ID, signal } from '@angular/core';
import { NavigationEnd, Router, RouterLink, RouterOutlet } from '@angular/router';
import { filter } from 'rxjs';
import { AuthService } from './services/auth.service';

@Component({
  selector: 'app-root',
  imports: [RouterLink, RouterOutlet],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App implements OnInit {
  // Tracks the active URL for navbar link state and auth-page layout decisions.
  currentUrl = signal<string>('/dashboard')

  // Injects routing, authentication state, and platform detection dependencies.
  constructor(
    private router: Router,
    public authService: AuthService,
    @Inject(PLATFORM_ID) private platformId: object,
  ) {}

  // Initializes session state and keeps currentUrl updated after navigation.
  ngOnInit(): void {
    this.currentUrl.set(this.router.url)

    if (isPlatformBrowser(this.platformId)) {
      this.authService.loadCurrentUser().subscribe()
    }

    // Keep the navbar in sync with the active page after each completed navigation.
    this.router.events
      .pipe(filter((event): event is NavigationEnd => event instanceof NavigationEnd))
      .subscribe((event) => {
        this.currentUrl.set(event.urlAfterRedirects)
      })
  }

  // Returns true when the dashboard route is active.
  get isDashboardPage(): boolean {
    return this.currentUrl().startsWith('/dashboard')
  }

  // Returns true when a food catalogue route is active.
  get isFoodPage(): boolean {
    return this.currentUrl().startsWith('/foods')
  }

  // Returns true when a meal-plan route is active.
  get isMealPlanPage(): boolean {
    return this.currentUrl().startsWith('/meal-plans')
  }

  // Returns true for login and registration pages.
  get isAuthPage(): boolean {
    return this.currentUrl().startsWith('/login') || this.currentUrl().startsWith('/register')
  }

  // Clears local auth state, calls backend logout, and returns to the login page.
  logout(): void {
    this.authService.clearSessionUser()

    this.authService.logout().subscribe({
      next: () => {
        this.router.navigate(['/login'])
      },
      error: () => {
        this.router.navigate(['/login'])
      }
    })
  }
}
