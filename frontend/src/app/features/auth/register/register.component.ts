import { Component, computed, inject, signal } from '@angular/core';
import { AuthService } from '../../../core/auth/auth.service';
import { Router, RouterLink } from '@angular/router';
import { NavbarComponent } from '../../navbar/navbar.component';
import { FooterComponent } from '../../footer/footer.component';

@Component({
  selector: 'app-register',
  imports: [RouterLink, NavbarComponent, FooterComponent],
  templateUrl: './register.component.html',
  styleUrl: './register.component.css',
})
export class RegisterComponent {
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);
  readonly email = signal('');
  readonly username = signal('');
  readonly password = signal('');
  readonly confirmPassword = signal('');
  readonly errorMessage = signal<string | null>(null);
  readonly isLoading = signal(false);

  isEmailValid = computed(() =>
    this.email().includes('@') && this.email().length > 5
  );

  isPasswordValid = computed(() =>
    this.password().length >= 6
  );

  passwordsMatch = computed(() =>
    this.password() === this.confirmPassword()
  );

  isFormValid = computed(() =>
    this.isEmailValid() &&
    this.isPasswordValid() &&
    this.passwordsMatch()
  );

  onSubmit(event: Event) {
    event.preventDefault();
    if (!this.isFormValid()) return;

    this.isLoading.set(true);
    this.errorMessage.set(null);

    this.authService.register({
      email: this.email(),
      username: this.username(),
      password: this.password()
    })
    .subscribe({
      error: (err) => {
        this.errorMessage.set(
          err?.error?.message || 'An error occurred during registration. Please try again.');
        this.isLoading.set(false);
      },
      next: () => {
        this.router.navigate(['/home'], {
          state: {
            validationMessage:
              "Un mail d'activation a été envoyé. Pense à vérifier ta boîte mail (et les spams).",
          },
        });
      }
    });
  }
}
