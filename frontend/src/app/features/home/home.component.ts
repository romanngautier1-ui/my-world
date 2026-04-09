import { Component, inject, signal } from '@angular/core';
import { AuthService } from '../../core/auth/auth.service';
import { NavbarComponent } from "../navbar/navbar.component";
import { FooterComponent } from '../footer/footer.component';

@Component({
  selector: 'app-home',
  imports: [NavbarComponent, NavbarComponent, FooterComponent],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css',
})
export class HomeComponent {
  readonly authService = inject(AuthService);
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

  isAuthenticated(): boolean{
    return this.authService.isAuthenticated();
  }
}
