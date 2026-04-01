import { Component, effect, inject } from '@angular/core';
import { ArticleService } from '../../../shared/services/article.service';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ArticleUpdateDto } from '../../../shared/dtos/article.dto';
import { NavbarComponent } from '../../navbar/navbar.component';
import { FooterComponent } from '../../footer/footer.component';
import FormFilesUtils from '../../../shared/utils/form-files.utils';

@Component({
  selector: 'app-article-edit',
  imports: [ReactiveFormsModule, RouterLink, NavbarComponent, FooterComponent],
  templateUrl: './article-edit.component.html',
  styleUrl: './article-edit.component.css',
})
export class ArticleEditComponent {
  readonly #route = inject(ActivatedRoute);
  readonly #router = inject(Router);
  readonly #articleService = inject(ArticleService);
  readonly #articleId = Number(this.#route.snapshot.paramMap.get('id'));

  readonly article = this.#articleService.getArticleById(this.#articleId);

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

  constructor() {
    effect(() => {
      if (this.article.value()) {
        this.form.patchValue({
          title: this.article.value().title,
          content: this.article.value().content,
          urlImage: this.article.value().urlImage,
          uploadImage: null,
        });
      }
    });
  }

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
    const article = this.article.value();
    const isFromValid = this.form.valid;

    this.articleTitle.markAsDirty();
    this.articleContent.markAsDirty();
    this.articleUrlImage.markAsDirty();

    if (isFromValid && article) {
      const updatedArticle: ArticleUpdateDto = {
        title: this.articleTitle.value,
        content: this.articleContent.value,
        urlImage: this.articleUrlImage.value,
        uploadImage: this.articleUploadImage.value ?? undefined,
      };

      this.#articleService.updateArticle(this.#articleId, updatedArticle).subscribe(() => {
        this.#router.navigate(['/articles', this.#articleId]);
      });
    }
  }
}
