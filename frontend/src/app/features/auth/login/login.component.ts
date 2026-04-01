import { Component, computed, inject, signal } from '@angular/core';
import { AuthService } from '../../../core/auth/auth.service';
import { Router, RouterLink } from '@angular/router';
import { NavbarComponent } from '../../navbar/navbar.component';
import { FooterComponent } from '../../footer/footer.component';

@Component({
  selector: 'app-login',
  imports: [RouterLink, NavbarComponent, FooterComponent],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css',
})
export class LoginComponent {
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);
  readonly username = signal('');
  readonly password = signal('');
  readonly message = signal('');

  readonly validationMessage = signal<string | null>(null);

  constructor() {
    const message = typeof history.state?.validationMessage === 'string'
      ? (history.state.validationMessage as string)
      : null;

    if (message) {
      this.validationMessage.set(message);
      const { validationMessage, ...rest } = history.state ?? {};
      history.replaceState(rest, '');
    }
  }

  // Vérifie si username est valide
  isUsernameValid = computed(() => {
    return this.username().trim().length >= 3;
  });

  // Vérifie si mot de passe est valide
  isPasswordValid = computed(() => {
    return this.password().length >= 6;
  });

  // Vérifie si formulaire est valide
  isFormValid = computed(() => {
    return this.isUsernameValid() && this.isPasswordValid();
  });

  onSubmit(event: Event) {
    event.preventDefault();
    this.message.set('Tentative de connexion en cours ...');

    this.authService
      .login({
        username: this.username(),
        password: this.password()
      })
      .subscribe({
      error: (err) => {
        this.message.set(
          err?.error?.message);
        this.username.set('');
        this.password.set('');
      },
      next: () => {
        this.router.navigate(['/home']);
      }
    });
  }
}
