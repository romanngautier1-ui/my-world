import { Component, effect, inject } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { ChapterService } from '../../../../../shared/services/chapter.service';
import { FormControl, ReactiveFormsModule, Validators } from '@angular/forms';
import { FormGroup } from '@angular/forms';
import { ChapterUpdateDto } from '../../../../../shared/dtos/chapter.dto';
import { NavbarComponent } from '../../../../navbar/navbar.component';
import { FooterComponent } from '../../../../footer/footer.component';
import FormFilesUtils from '../../../../../shared/utils/form-files.utils';

@Component({
  selector: 'app-chapter-edit',
  imports: [RouterLink, ReactiveFormsModule, NavbarComponent, FooterComponent],
  templateUrl: './chapter-edit.component.html',
  styleUrl: './chapter-edit.component.css',
})
export class ChapterEditComponent {
  readonly router = inject(Router);
  readonly #route = inject(ActivatedRoute);
  readonly #chapterService = inject(ChapterService);
  readonly bookId = Number(this.#route.snapshot.paramMap.get('bookId'));
  readonly chapterId = Number(this.#route.snapshot.paramMap.get('chapterId'));

  readonly chapter = this.#chapterService.getChapterById(this.chapterId);

  readonly form = new FormGroup({
    title: new FormControl('', {
      nonNullable: true,
      validators: [
        Validators.required,
        Validators.minLength(3),
        Validators.maxLength(150),
      ],
    }),
    number: new FormControl(1, {
      nonNullable: true,
      validators: [
        Validators.required,
        Validators.min(1)
      ],
    }),
    content: new FormControl('', {
      nonNullable: true,
      validators: [
        // Validators.required,
        // Validators.minLength(3),
      ],
    }),
    uploadFile: new FormControl<File | null>(null, {
      nonNullable: false,
    }),
  });

  constructor() {
    effect(() => {
      if (this.chapter.value()) {
        this.form.patchValue({
          title: this.chapter.value().title,
          number: this.chapter.value().number,
          content: this.chapter.value().content,
          uploadFile: null,
        });
      }
    });
  }

  get chapterTitle() {
    return this.form.get('title') as FormControl<string>;
  }

  get chapterNumber() {
    return this.form.get('number') as FormControl<number>;
  }

  get chapterContent() {
    return this.form.get('content') as FormControl<string>;
  }

  get chapterUploadFile() {
    return this.form.get('uploadFile') as FormControl<File | null>;
  }

  onFileSelected(event: Event) {
    FormFilesUtils.onFileSelected(event, this.chapterUploadFile);
  }

  onSubmit() { 
    const isFromValid = this.form.valid;

    this.chapterTitle.markAsDirty();
    this.chapterNumber.markAsDirty();
    this.chapterContent.markAsDirty();
    this.chapterUploadFile.markAsDirty();

    if (isFromValid) {
      const newChapter: ChapterUpdateDto = {
        title: this.chapterTitle.value,
        number: this.chapterNumber.value,
        content: this.chapterContent.value,
        bookId: this.bookId,
        uploadFile: this.chapterUploadFile.value ?? undefined,
      }

      this.#chapterService.updateChapter(this.chapterId, newChapter).subscribe((updatedChapter) => {
        this.router.navigate(['/books', this.bookId, 'chapters', updatedChapter.id]);
      });
    }
  }
}
