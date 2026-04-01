import { Component, effect, input } from '@angular/core';
import { CommentDto, CommentUpdateDto } from '../../../../../../shared/dtos/comment.dto';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';

@Component({
  selector: 'app-comment-edit-modal',
  imports: [ReactiveFormsModule],
  templateUrl: './comment-edit-modal.component.html',
  styleUrl: './comment-edit-modal.component.css',
})
export class CommentEditModalComponent {
  comment = input<CommentDto | null>(null);

  readonly form: FormGroup = new FormGroup({
    content: new FormControl('', {
      nonNullable: true,
      validators: [
        Validators.required,
        Validators.minLength(3),
      ],
    }),
    rating: new FormControl(1, {
      nonNullable: true,
      validators: [
        Validators.required,
        Validators.min(1),
        Validators.max(10),
      ],
    }),
  });

  constructor() {
    effect(() => {
      const comment = this.comment();
      console.log('Comment in effect:', comment);
      if (comment) {
        this.form.patchValue({
          content: comment.content,
          rating: comment.rating,
        });
      }
    });
  }

  get commentContent() {
    return this.form.get('content') as FormControl<string>;
  }

  get commentRating() {
    return this.form.get('rating') as FormControl<number>;
  }

  getUpdateDtoIfValid(): CommentUpdateDto | null {
    this.commentContent.markAsDirty();
    this.commentRating.markAsDirty();

    const isFormValid = this.form.valid;

    if (!isFormValid) {
      return null;
    }

    return {
      content: this.commentContent.value,
      rating: this.commentRating.value,
    };
  }
}
