import { inject } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { PLATFORM_ID } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { map } from 'rxjs';
import { AuthService } from '../services/auth.service';

export const authGuard: CanActivateFn = () => {
  const authService = inject(AuthService)
  const router = inject(Router)
  const platformId = inject(PLATFORM_ID)

  if (!isPlatformBrowser(platformId)) {
    return router.createUrlTree(['/login'])
  }

  // Authentication requirement: protected routes redirect anonymous users to the login page.
  return authService.isAuthenticated().pipe(
    map((authenticated) => authenticated ? true : router.createUrlTree(['/login']))
  )
}
