import { isPlatformBrowser } from '@angular/common';
import { Inject, Injectable, PLATFORM_ID, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, catchError, map, of, tap } from 'rxjs';
import { AuthUser } from '../models/auth-user';
import { LoginRequest } from '../models/login-request';
import { RegisterRequest } from '../models/register-request';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = '/api/auth'
  private storageKey = 'mealPlanner.currentUser'

  // Holds the current user so components can react to login/logout state.
  currentUser = signal<AuthUser | null>(null)

  // Restores any cached session user when the service is created in the browser.
  constructor(
    private http: HttpClient,
    @Inject(PLATFORM_ID) private platformId: object,
  ) {
    const cachedUser = this.readSessionUser()

    if (cachedUser) {
      this.currentUser.set(cachedUser)
    }
  }

  // Sends a registration request to create a new backend account.
  register(request: RegisterRequest): Observable<AuthUser> {
    return this.http.post<AuthUser>(`${this.apiUrl}/register`, request, {
      withCredentials: true,
    })
  }

  // Sends login credentials and stores the returned authenticated user.
  login(request: LoginRequest): Observable<AuthUser> {
    return this.http.post<AuthUser>(`${this.apiUrl}/login`, request, {
      withCredentials: true,
    }).pipe(
      // Authentication requirement: keep the logged-in user in a signal for navbar/guard state.
      tap((user) => this.setSessionUser(user))
    )
  }

  // Loads the current user from the backend and clears local state if the session is invalid.
  loadCurrentUser(): Observable<AuthUser | null> {
    return this.http.get<AuthUser>(`${this.apiUrl}/me`, {
      withCredentials: true,
    }).pipe(
      tap((user) => this.setSessionUser(user)),
      catchError(() => {
        this.clearSessionUser()
        return of(null)
      })
    )
  }

  // Logs out on the backend and clears the cached frontend session.
  logout(): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/logout`, {}, {
      withCredentials: true,
    }).pipe(
      tap(() => this.clearSessionUser())
    )
  }

  // Checks whether the backend currently recognizes the user as authenticated.
  isAuthenticated(): Observable<boolean> {
    return this.loadCurrentUser().pipe(map((user) => user !== null))
  }

  // Stores the authenticated user in memory and browser session storage.
  private setSessionUser(user: AuthUser): void {
    this.currentUser.set(user)

    if (isPlatformBrowser(this.platformId)) {
      sessionStorage.setItem(this.storageKey, JSON.stringify(user))
    }
  }

  // Clears the authenticated user from memory and browser session storage.
  clearSessionUser(): void {
    this.currentUser.set(null)

    if (isPlatformBrowser(this.platformId)) {
      sessionStorage.removeItem(this.storageKey)
    }
  }

  // Reads and validates the cached session user from browser session storage.
  private readSessionUser(): AuthUser | null {
    if (!isPlatformBrowser(this.platformId)) {
      return null
    }

    const rawUser = sessionStorage.getItem(this.storageKey)

    if (!rawUser) {
      return null
    }

    try {
      const parsedUser = JSON.parse(rawUser) as AuthUser

      if (!parsedUser.sessionToken) {
        sessionStorage.removeItem(this.storageKey)
        return null
      }

      return parsedUser
    } catch {
      sessionStorage.removeItem(this.storageKey)
      return null
    }
  }
}
