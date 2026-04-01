import { Component, computed, inject, signal } from '@angular/core';
import { AuthService } from '../../../core/auth/auth.service';
import { RouterLink } from '@angular/router';
import { NavbarComponent } from '../../navbar/navbar.component';
import { FooterComponent } from '../../footer/footer.component';

@Component({
  selector: 'app-reset-password',
  imports: [RouterLink, NavbarComponent, FooterComponent],
  templateUrl: './reset-password.component.html',
  styleUrl: './reset-password.component.css',
})
export class ResetPasswordComponent {
  readonly authService = inject(AuthService);

  email = signal<string>('');
  message = signal<string | null>(null);
  isLoading = signal(false);

  isEmailValid = computed(() => {
    return this.email().trim().length >= 3 && this.email().includes('@');
  });

  isFormValid = computed(() => {
    return this.isEmailValid();
  });

  onSubmit(event: Event) {
    event.preventDefault();
    if (!this.isFormValid()) return;

    this.isLoading.set(true);
    this.message.set(null);

    this.authService
      .resetPassword(this.email().trim())
      .subscribe({
        next: (response) => {
          this.email.set('');
          this.message.set(
            response ||
              'Si un compte associé à cet email existe, un lien de réinitialisation a été envoyé.'
          );
          this.isLoading.set(false);
        },
        error: (err) => {
          this.message.set(
            err?.error?.message ||
              'Une erreur est survenue. Merci de réessayer plus tard.'
          );
          this.isLoading.set(false);
        },
      });
  }
}
