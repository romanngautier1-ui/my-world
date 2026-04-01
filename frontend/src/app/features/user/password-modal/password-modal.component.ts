import { Component, inject, input, output, signal } from '@angular/core';
import { UserService } from '../../../shared/services/user.service';

@Component({
  selector: 'app-password-modal',
  imports: [],
  templateUrl: './password-modal.component.html',
  styleUrl: './password-modal.component.css',
})
export class PasswordModalComponent {
  readonly isOpen = input(false);
  readonly close = output<void>();

  readonly userService = inject(UserService);
  oldPassword = signal('');
  newPassword = signal('');
  confirmPassword = signal('');
  message = signal('');

  get isFormValid() {
    return (
      this.oldPassword().length > 0 &&
      this.newPassword().length >= 6 &&
      this.newPassword() === this.confirmPassword()
    );
  }

  onSubmit(event: Event) {
    event.preventDefault();
    this.message.set('Mise à jour du mot de passe en cours...');

    if (this.isFormValid) {
      this.userService
        .updatePassword(this.oldPassword(), this.newPassword())
        .subscribe({
          next: () => {
            this.message.set('Mot de passe mis à jour avec succès.');
            this.oldPassword.set('');
            this.newPassword.set('');
            this.confirmPassword.set('');
            this.close.emit();
          },
          error: (err) => {
            this.message.set(err?.error?.message || 'Erreur lors de la mise à jour du mot de passe.');
          }
        });
    }
  }

  onBackdropClick() {
    this.close.emit();
    this.message.set('');
  }
}
