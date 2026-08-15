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
  currentUrl = signal<string>('/dashboard')

  constructor(
    private router: Router,
    public authService: AuthService,
    @Inject(PLATFORM_ID) private platformId: object,
  ) {}

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

  get isDashboardPage(): boolean {
    return this.currentUrl().startsWith('/dashboard')
  }

  get isFoodPage(): boolean {
    return this.currentUrl().startsWith('/foods')
  }

  get isMealPlanPage(): boolean {
    return this.currentUrl().startsWith('/meal-plans')
  }

  get isAuthPage(): boolean {
    return this.currentUrl().startsWith('/login') || this.currentUrl().startsWith('/register')
  }

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
