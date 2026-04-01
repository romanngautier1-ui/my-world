import { HttpClient, httpResource, HttpResourceRef } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { UserCreateDto, UserDto, UserUpdateDto } from '../dtos/user.dto';
import { AuthResponse } from '../../core/auth/models/auth-response.model';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class UserService {
  readonly #apiUrl = `${environment.apiUrl}/users`;
  readonly #http = inject(HttpClient);

  getUserById(id: number | null): HttpResourceRef<UserDto> {
    return httpResource<UserDto>(() => ({url: this.#apiUrl + '/me' }), { defaultValue: {} as UserDto });
  }

  addUser(user: UserCreateDto): Observable<UserCreateDto> {
    return this.#http.post<UserCreateDto>(this.#apiUrl, user);
  }
    
  updateUser(patch: UserUpdateDto): Observable<AuthResponse> {
    const url = this.#apiUrl + '/me/update-current-user';
    return this.#http.patch<AuthResponse>(url, patch);
  }

  updatePassword(oldPassword: string, newPassword: string): Observable<string> {
    const url = this.#apiUrl + '/me/change-password';
    return this.#http.patch(url, { oldPassword, newPassword }, { responseType: 'text' });
  }

  deleteUser(id: number): Observable<void> {
    const url = this.#apiUrl + '/' + id;
    return this.#http.delete<void>(url);
  }
}
