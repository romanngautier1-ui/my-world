import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from './auth.service';

/**
 * Guard basé sur les rôles.
 * À utiliser avec: data: { roles: ['ROLE_ADMIN'] } (ou ROLE_USER)
 */
export const roleGuard: CanActivateFn = (route) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  if (!authService.isAuthenticated()) {
    router.navigate(['/login']);
    return false;
  }

  const requiredRoles = (route.data?.['roles'] as string[] | undefined) ?? [];
  if (requiredRoles.length === 0) {
    return true;
  }

  if (authService.hasAnyRole(requiredRoles)) {
    return true;
  }

  router.navigate(['/home']);
  return false;
};
