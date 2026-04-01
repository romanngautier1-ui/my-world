import { Injectable, signal, computed } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { tap } from 'rxjs';
import { LoginRequest } from './models/login-request.model';
import { environment } from '../../../environments/environment';
import { AuthResponse } from './models/auth-response.model';
import { RegisterRequest } from './models/register-request.model';
import { ResetPasswordRequest } from './models/reset-password.model';

@Injectable({ providedIn: 'root' })
export class AuthService {

  private readonly API_URL = `${environment.apiUrl}/auth`;

  // Signal Angular 21 → état réactif global
  private _isAuthenticated = signal<boolean>(false);

  // Exposition en lecture seule
  isAuthenticated = computed(() => this._isAuthenticated());

  roles = computed(() => this.getRolesFromToken());

  username = computed(() => this.getUsernameFromToken());

  userId = computed(() => this.getUserIdFromToken());

  isAdmin = computed(() => this.hasRole('ROLE_ADMIN'));

  constructor(
    private http: HttpClient,
    private router: Router
  ) {
    // Au démarrage de l'application :
    const token = localStorage.getItem('token');
    this._isAuthenticated.set(!!token);
  }

  login(credentials: LoginRequest) {
    return this.http.post<AuthResponse>(`${this.API_URL}/login`, credentials)
      .pipe(
        tap(response => {
          localStorage.setItem('token', response.token);
          this._isAuthenticated.set(true);
          this.router.navigate(['/home']);
        })
      );
  }

  register(credentials: RegisterRequest) {
    return this.http.post(`${this.API_URL}/register`, credentials, {
      responseType: 'text',
    });
  }

  verifyEmail(token: string) {
    return this.http.get(`${this.API_URL}/verify-email`, {
      params: { token },
      responseType: 'text',
    });
  }

  resetPassword(email: string) {
    return this.http.post(`${this.API_URL}/reset-password`, email, {
      responseType: 'text',
    });
  }

   resetPasswordLink(credentials: ResetPasswordRequest) {
    return this.http.post(`${this.API_URL}/reset-password-link`, credentials, {
      responseType: 'text',
    });
  }

  logout() {
    localStorage.removeItem('token');
    this._isAuthenticated.set(false);
    this.router.navigate(['/login']);
  }

  setToken(token: string) {
    localStorage.setItem('token', token);
    this._isAuthenticated.set(true);
  }

  getToken(): string | null {
    return localStorage.getItem('token');
  }

  hasRole(role: string): boolean {
    return this.getRolesFromToken().includes(role);
  }

  hasAnyRole(roles: string[]): boolean {
    const actualRoles = this.getRolesFromToken();
    return roles.some(r => actualRoles.includes(r));
  }

  private getUsernameFromToken(): string | null {
    const token = this.getToken();
    if (!token) return null;
    const payload = this.decodeJwtPayload(token);
    if (!payload) return null;

    // Prefer explicit claim, else fallback to JWT subject
    const username = payload['username'];
    if (typeof username === 'string' && username.length > 0) return username;

    const sub = payload['sub'];
    if (typeof sub === 'string' && sub.length > 0) return sub;

    return null;
  }

  private getUserIdFromToken(): number | null {
    const token = this.getToken();
    if (!token) return null;
    const payload = this.decodeJwtPayload(token);
    if (!payload) return null;

    const userId = payload['userId'];
    if (typeof userId === 'number') return userId;
    if (typeof userId === 'string' && userId.trim().length > 0) {
      const parsed = Number(userId);
      return Number.isFinite(parsed) ? parsed : null;
    }
    return null;
  }

  private getRolesFromToken(): string[] {
    const token = this.getToken();
    if (!token) return [];

    const payload = this.decodeJwtPayload(token);
    if (!payload) return [];

    const rolesClaim = payload['roles'] ?? payload['role'] ?? payload['authorities'] ?? payload['authority'];
    if (!rolesClaim) return [];

    if (typeof rolesClaim === 'string') {
      return [rolesClaim];
    }

    if (Array.isArray(rolesClaim)) {
      const roles: string[] = [];
      for (const item of rolesClaim) {
        if (typeof item === 'string') {
          roles.push(item);
        } else if (item && typeof item === 'object') {
          const authority = (item as Record<string, unknown>)['authority'];
          if (typeof authority === 'string') roles.push(authority);
        }
      }
      return roles;
    }

    if (rolesClaim && typeof rolesClaim === 'object') {
      const authority = (rolesClaim as Record<string, unknown>)['authority'];
      if (typeof authority === 'string') return [authority];
    }

    return [];
  }

  private decodeJwtPayload(token: string): Record<string, unknown> | null {
    const parts = token.split('.');
    if (parts.length !== 3) return null;

    try {
      const payloadBase64Url = parts[1];
      const payloadBase64 = payloadBase64Url.replace(/-/g, '+').replace(/_/g, '/');
      const pad = payloadBase64.length % 4;
      const padded = pad ? payloadBase64 + '='.repeat(4 - pad) : payloadBase64;
      const json = decodeURIComponent(
        atob(padded)
          .split('')
          .map(c => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2))
          .join('')
      );
      const payload = JSON.parse(json);
      return payload && typeof payload === 'object' ? payload : null;
    } catch {
      return null;
    }
  }

  
}