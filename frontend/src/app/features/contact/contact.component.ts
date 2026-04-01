import { Component, inject } from '@angular/core';
import { Email } from '../../shared/models/email.model';
import { EmailService } from '../../shared/services/email.service';
import { FormControl, ReactiveFormsModule, Validators } from '@angular/forms';
import { FormGroup } from '@angular/forms';
import { NavbarComponent } from '../navbar/navbar.component';
import { FooterComponent } from '../footer/footer.component';

@Component({
  selector: 'app-contact',
  imports: [ReactiveFormsModule, NavbarComponent, FooterComponent],
  templateUrl: './contact.component.html',
  styleUrl: './contact.component.css',
})
export class ContactComponent {

  readonly #emailService = inject(EmailService);

  readonly form = new FormGroup({
    recipient: new FormControl('', {
      nonNullable: true,
      validators: [
        Validators.required,
        Validators.minLength(5),
        Validators.maxLength(150),
      ],
    }),
    subject: new FormControl('', {
      nonNullable: true,
      validators: [
        Validators.required,
        Validators.minLength(5),
      ],
    }),
    message: new FormControl('', {
      nonNullable: true,
      validators: [
        Validators.required,
      ],
    }),
  });

  get emailRecipient() {
    return this.form.get('recipient') as FormControl<string>;
  }

  get emailSubject() {
    return this.form.get('subject') as FormControl<string>;
  }

  get emailMessage() {
    return this.form.get('message') as FormControl<string>;
  }

  onSubmit() { 
    this.form.markAllAsTouched();
    const isFormValid = this.form.valid;

    if (isFormValid) {
      const newEmail: Email = {
        recipient: this.emailRecipient.value,
        subject: this.emailSubject.value,
        message: this.emailMessage.value,
      }

      this.#emailService.sendEmail(newEmail).subscribe(() => {
        // Handle success, e.g., show a success message or navigate
      });
    }
  }
}
