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

  currentUser = signal<AuthUser | null>(null)

  constructor(
    private http: HttpClient,
    @Inject(PLATFORM_ID) private platformId: object,
  ) {
    const cachedUser = this.readSessionUser()

    if (cachedUser) {
      this.currentUser.set(cachedUser)
    }
  }

  register(request: RegisterRequest): Observable<AuthUser> {
    return this.http.post<AuthUser>(`${this.apiUrl}/register`, request, {
      withCredentials: true,
    })
  }

  login(request: LoginRequest): Observable<AuthUser> {
    return this.http.post<AuthUser>(`${this.apiUrl}/login`, request, {
      withCredentials: true,
    }).pipe(
      // Authentication requirement: keep the logged-in user in a signal for navbar/guard state.
      tap((user) => this.setSessionUser(user))
    )
  }

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

  logout(): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/logout`, {}, {
      withCredentials: true,
    }).pipe(
      tap(() => this.clearSessionUser())
    )
  }

  isAuthenticated(): Observable<boolean> {
    return this.loadCurrentUser().pipe(map((user) => user !== null))
  }

  private setSessionUser(user: AuthUser): void {
    this.currentUser.set(user)

    if (isPlatformBrowser(this.platformId)) {
      sessionStorage.setItem(this.storageKey, JSON.stringify(user))
    }
  }

  clearSessionUser(): void {
    this.currentUser.set(null)

    if (isPlatformBrowser(this.platformId)) {
      sessionStorage.removeItem(this.storageKey)
    }
  }

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
