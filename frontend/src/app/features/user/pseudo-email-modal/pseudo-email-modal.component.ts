import { Component, effect, inject, input, output, signal } from '@angular/core';
import { UserService } from '../../../shared/services/user.service';
import { AuthService } from '../../../core/auth/auth.service';

@Component({
  selector: 'app-pseudo-email-modal',
  imports: [],
  templateUrl: './pseudo-email-modal.component.html',
  styleUrl: './pseudo-email-modal.component.css',
})
export class PseudoEmailModalComponent {
  readonly isOpen = input(false);
  readonly close = output<void>();
  readonly updated = output<void>();
  readonly userService = inject(UserService);
  readonly authService = inject(AuthService);
  readonly currentUsername = input('');
  readonly currentEmail = input('');
  newUsername = signal('');
  newEmail = signal('');
  message = signal('');

  constructor() {
    effect(() => {
      if (!this.isOpen()) return;
      this.newUsername.set(this.currentUsername() ?? '');
      this.newEmail.set(this.currentEmail() ?? '');
      this.message.set('');
    });
  }

  get isFormValid() {
    return (
      this.newUsername().length > 0 &&
      this.newEmail().includes('@') &&
      this.newEmail().length > 5
    );
  }

  onSubmit(event: Event) {
    event.preventDefault();
    this.message.set('Mise à jour en cours...');

    if (this.isFormValid) {
      this.userService
        .updateUser({ username: this.newUsername(), email: this.newEmail() })
        .subscribe({
          next: (response) => {
            if (response?.token) {
              this.authService.setToken(response.token);
            }
            this.message.set('Mise à jour réussie.');
            this.updated.emit();
            this.close.emit();
          },
          error: (err) => {
            this.message.set(err?.error?.message || 'Erreur lors de la mise à jour.');
          }
        });
    }
  }

  onBackdropClick() {
    this.close.emit();
    this.message.set('');
  }
}
