import { Component, computed, inject, signal } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { AuthService } from '../../../core/auth/auth.service';
import { NavbarComponent } from '../../navbar/navbar.component';
import { FooterComponent } from '../../footer/footer.component';

@Component({
  selector: 'app-reset-password-link',
  imports: [RouterLink, NavbarComponent, FooterComponent],
  templateUrl: './reset-password-link.component.html',
  styleUrl: './reset-password-link.component.css',
})
export class ResetPasswordLinkComponent {

  private readonly authService = inject(AuthService);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);

  token = signal<string>('');
  newPassword = signal<string>('');
  confirmPassword = signal<string>('');

  message = signal<string | null>(null);
  isLoading = signal(false);

  isTokenValid = computed(() => this.token().trim().length > 0);
  isPasswordValid = computed(() => this.newPassword().length >= 6);
  passwordsMatch = computed(() => this.newPassword() === this.confirmPassword());
  isFormValid = computed(
    () => this.isTokenValid() && this.isPasswordValid() && this.passwordsMatch()
  );

  constructor() {
    const token = this.route.snapshot.queryParamMap.get('token');
    this.token.set(token ?? '');
    if (!token) {
      this.message.set('Lien invalide: token manquant.');
    }
  }

  onSubmit(event: Event) {
    event.preventDefault();
    if (!this.isFormValid()) return;

    this.isLoading.set(true);
    this.message.set(null);

    this.authService
      .resetPasswordLink({ token: this.token(), newPassword: this.newPassword() })
      .subscribe({
        next: (response) => {
          this.message.set(response || 'Mot de passe mis à jour.');
          this.isLoading.set(false);
          this.router.navigate(['/login'], {
            state: {
              validationMessage:
                "Mot de passe mis à jour avec succès. Vous pouvez maintenant vous connecter.",
            },
          });
        },
        error: (err) => {
          this.message.set(
            err?.error?.message ||
              err?.error ||
              'Lien invalide ou expiré. Merci de recommencer.'
          );
          this.isLoading.set(false);
        },
      });
  }

}
