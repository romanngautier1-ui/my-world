import { Component, effect, inject } from '@angular/core';
import { BookUpdateDto } from '../../../../shared/dtos/book.dto';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { BookService } from '../../../../shared/services/book.service';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { NavbarComponent } from '../../../navbar/navbar.component';
import { FooterComponent } from '../../../footer/footer.component';
import FormFilesUtils from '../../../../shared/utils/form-files.utils';

@Component({
  selector: 'app-book-edit',
  imports: [RouterLink, ReactiveFormsModule, NavbarComponent, FooterComponent],
  templateUrl: './book-edit.component.html',
  styleUrl: './book-edit.component.css',
})
export class BookEditComponent {
  readonly router = inject(Router);
  readonly route = inject(ActivatedRoute);
  readonly #bookService = inject(BookService);
  readonly bookId = Number(this.route.snapshot.paramMap.get('id'));
  readonly book = this.#bookService.getBookById(this.bookId);

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

  constructor() {
    effect(() => {
      const book = this.book.value();
      if (book) {
        this.form.patchValue({
          title: book.title,
          number: book.number,
          description: book.description,
          urlImage: book.urlImage,
          uploadImage: null,
        });
      }
    });
  }

  get bookTitle() {
    return this.form.get('title') as FormControl;
  }

  get bookNumber() {
    return this.form.get('number') as FormControl;
  }

  get bookDescription() {
    return this.form.get('description') as FormControl;
  }

  get bookUrlImage() {
    return this.form.get('urlImage') as FormControl;
  }

  get bookUploadImage() {
    return this.form.get('uploadImage') as FormControl<File | null>;
  }

  onFileSelected(event: Event) {
    FormFilesUtils.onFileSelected(event, this.bookUploadImage);
  }

  onSubmit(): void {
    console.log('submit');
    this.bookTitle.markAsTouched();
    this.bookNumber.markAsTouched();
    this.bookDescription.markAsTouched();
    this.bookUrlImage.markAsTouched();
    this.bookUploadImage.markAsTouched();

    if (this.form.valid) {
      const newBook: BookUpdateDto = {
        title: this.bookTitle.value,
        number: this.bookNumber.value,
        description: this.bookDescription.value,
        urlImage: this.bookUrlImage.value,
        uploadImage: this.bookUploadImage.value ?? undefined,
      };

      this.#bookService.updateBook(this.bookId, newBook).subscribe(() => {
        this.router.navigate(['/books']);
      })
    }
  }
}
