import { Component, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { BookService } from '../../../../shared/services/book.service';
import { BookCreateDto } from '../../../../shared/dtos/book.dto';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { NavbarComponent } from '../../../navbar/navbar.component';
import { FooterComponent } from '../../../footer/footer.component';
import FormFilesUtils from '../../../../shared/utils/form-files.utils';

@Component({
  selector: 'app-book-add',
  imports: [ReactiveFormsModule, NavbarComponent, FooterComponent],
  templateUrl: './book-add.component.html',
  styleUrl: './book-add.component.css',
})
export class BookAddComponent {
  readonly router = inject(Router);
  readonly route = inject(ActivatedRoute);
  readonly #bookService = inject(BookService);

  readonly form = new FormGroup({
    title: new FormControl('', [
      Validators.required,
    ]),
    number: new FormControl(0, [
      Validators.required,
      Validators.min(0),
    ]),
    description: new FormControl('', [
      Validators.required,
    ]),
    urlImage: new FormControl('', {
      nonNullable: true,
      validators: [
        Validators.pattern(
          /^(https?:\/\/)?([\w-]+(\.[\w-]+)+)(\/[\w-]*)*\/?(\?.*)?(#.*)?$/
        ),
      ],
    }),
    uploadImage: new FormControl<File | null>(null, {
      nonNullable: false,
    }),
  });

  get bookTitle() {
    return this.form.get('title') as FormControl;
  }

  get bookNumber() {
    return this.form.get('number') as FormControl;
  }

  get bookDescription() {
    return this.form.get('description') as FormControl;
  }

  get bookUploadImage() {
    return this.form.get('uploadImage') as FormControl<File | null>;
  }

  get bookUrlImage() {
    return this.form.get('urlImage') as FormControl;
  }

  onFileSelected(event: Event) {
    FormFilesUtils.onFileSelected(event, this.bookUploadImage);
  }

  onSubmit(): void {
    console.log('submit');
    this.bookTitle.markAsTouched();
    this.bookNumber.markAsTouched();
    this.bookDescription.markAsTouched();
    this.bookUploadImage.markAsTouched();
    this.bookUrlImage.markAsTouched();

    if (this.form.valid) {
      const newBook: BookCreateDto = {
        title: this.bookTitle.value,
        number: this.bookNumber.value,
        description: this.bookDescription.value,
        uploadImage: this.bookUploadImage.value ?? undefined,
        urlImage: this.bookUrlImage.value ?? undefined,
      };

      this.#bookService.addBook(newBook).subscribe(() => {
        this.router.navigate(['/books']);
      })
    }
  }

}
