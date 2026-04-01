import { Component, inject } from '@angular/core';
import { UserService } from '../../shared/services/user.service';
import { AuthService } from '../../core/auth/auth.service';
import { DatePipe } from '@angular/common';
import { NavbarComponent } from '../navbar/navbar.component';
import { FooterComponent } from '../footer/footer.component';
import { PasswordModalComponent } from './password-modal/password-modal.component';
import { PseudoEmailModalComponent } from './pseudo-email-modal/pseudo-email-modal.component';

@Component({
  selector: 'app-user',
  imports: [DatePipe, NavbarComponent, FooterComponent, PasswordModalComponent, PseudoEmailModalComponent],
  templateUrl: './user.component.html',
  styleUrl: './user.component.css',
})
export class UserComponent {
  readonly #userService = inject(UserService);
  readonly #authService = inject(AuthService);
  readonly #userId = this.#authService.userId();
  readonly user = this.#userService.getUserById(this.#userId);

  isPasswordModalOpen = false;
  isPseudoEmailModalOpen = false;

  openPasswordModal() {
    this.isPasswordModalOpen = true;
  }

  closePasswordModal() {
    this.isPasswordModalOpen = false;
  }

  openPseudoEmailModal() {
    this.isPseudoEmailModalOpen = true;
  }

  closePseudoEmailModal() {
    this.isPseudoEmailModalOpen = false;
  }

  onUserUpdated() {
    this.user.reload();
  }

  logout() {
    this.#authService.logout();
  }
}
