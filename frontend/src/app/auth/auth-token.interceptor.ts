import { HttpInterceptorFn } from '@angular/common/http';

const storageKey = 'mealPlanner.currentUser'

export const authTokenInterceptor: HttpInterceptorFn = (request, next) => {
  if (!request.url.startsWith('/api') || typeof sessionStorage === 'undefined') {
    return next(request)
  }

  const rawUser = sessionStorage.getItem(storageKey)

  if (!rawUser) {
    return next(request)
  }

  try {
    const user = JSON.parse(rawUser) as { sessionToken?: string | null }

    if (!user.sessionToken) {
      return next(request)
    }

    // Server-side session token is sent on every API request so refresh keeps authentication.
    return next(request.clone({
      setHeaders: {
        Authorization: `Bearer ${user.sessionToken}`,
      },
    }))
  } catch {
    sessionStorage.removeItem(storageKey)
    return next(request)
  }
}
