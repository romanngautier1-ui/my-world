import { Component, inject, signal } from '@angular/core';
import { AuthService } from '../../../core/auth/auth.service';
import { RouterLink } from '@angular/router';
import { NavbarComponent } from '../../navbar/navbar.component';
import { FooterComponent } from '../../footer/footer.component';

@Component({
  selector: 'app-verify-email',
  imports: [RouterLink, NavbarComponent, FooterComponent],
  templateUrl: './verify-email.component.html',
  styleUrl: './verify-email.component.css',
})
export class VerifyEmailComponent {
  readonly #authService = inject(AuthService);
  token: string | null = null;
  readonly message = signal<string | null>(null);

  constructor() {
    const urlParams = new URLSearchParams(window.location.search);
    this.token = urlParams.get('token');
    if (this.token) {
      this.#authService.verifyEmail(this.token).subscribe({
        next: (responseText) => {
          this.message.set(
            responseText ||
              'Votre adresse e-mail a été vérifiée avec succès ! Vous pouvez maintenant vous connecter.'
          );
        },
        error: (err) => {
          const backendMessage = typeof err?.error === 'string' ? err.error : null;
          this.message.set(
            backendMessage || 'Le lien de vérification est invalide ou a expiré. Veuillez réessayer.'
          );
        }
      });
    } else {
      this.message.set("Aucun token de vérification trouvé dans l'URL.");
    }
  }

}
