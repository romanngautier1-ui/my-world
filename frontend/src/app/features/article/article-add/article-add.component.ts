import { Component, inject } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { ArticleService } from '../../../shared/services/article.service';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ArticleCreateDto } from '../../../shared/dtos/article.dto';
import { AuthService } from '../../../core/auth/auth.service';
import { NavbarComponent } from '../../navbar/navbar.component';
import { FooterComponent } from '../../footer/footer.component';
import FormFilesUtils from '../../../shared/utils/form-files.utils';

@Component({
  selector: 'app-article-add',
  imports: [ReactiveFormsModule, RouterLink, NavbarComponent, FooterComponent],
  templateUrl: './article-add.component.html',
  styleUrl: './article-add.component.css',
})
export class ArticleAddComponent {
  readonly router = inject(Router);
  readonly #route = inject(ActivatedRoute);
  readonly #articleService = inject(ArticleService);
  readonly #auth = inject(AuthService);

  readonly form = new FormGroup({
    title: new FormControl('', {
      nonNullable: true,
      validators: [
        Validators.required,
        Validators.minLength(3),
        Validators.maxLength(150),
      ],
    }),
    content: new FormControl('', {
      nonNullable: true,
      validators: [
        Validators.required,
        Validators.minLength(3),
      ],
    }),
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

  get articleTitle() {
    return this.form.get('title') as FormControl<string>;
  }

  get articleContent() {
    return this.form.get('content') as FormControl<string>;
  }

  get articleUrlImage() {
    return this.form.get('urlImage') as FormControl<string>;
  }

  get articleUploadImage() {
    return this.form.get('uploadImage') as FormControl<File | null>;
  }

  onFileSelected(event: Event) {
    FormFilesUtils.onFileSelected(event, this.articleUploadImage);
  }

  onSubmit() { 
    const isFormValid = this.form.valid;

    this.articleTitle.markAsDirty();
    this.articleContent.markAsDirty();
    this.articleUrlImage.markAsDirty();
    this.articleUploadImage.markAsDirty();

    if (isFormValid) {
      const newArticle: Omit<ArticleCreateDto, 'id'> = {
        title: this.articleTitle.value,
        content: this.articleContent.value,
        urlImage: this.articleUrlImage.value,
        uploadImage: this.articleUploadImage.value ?? undefined,
        userId: this.#auth.userId(),
      }

      this.#articleService.addArticle(newArticle).subscribe((createdArticle) => {
        this.router.navigate(['/articles', createdArticle.id]);
      });
    }
  }
}
