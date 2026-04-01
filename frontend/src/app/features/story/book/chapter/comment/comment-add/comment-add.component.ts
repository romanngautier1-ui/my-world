import { Component, inject, output } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { CommentService } from '../../../../../../shared/services/comment.service';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { CommentCreateDto } from '../../../../../../shared/dtos/comment.dto';
import { AuthService } from '../../../../../../core/auth/auth.service';

@Component({
  selector: 'app-comment-add',
  imports: [ReactiveFormsModule],
  templateUrl: './comment-add.component.html',
  styleUrl: './comment-add.component.css',
})
export class CommentAddComponent {
  readonly router = inject(Router);
  readonly #route = inject(ActivatedRoute);
  readonly #chapterId = Number(this.#route.snapshot.paramMap.get('chapterId'));
  readonly #bookId = Number(this.#route.snapshot.paramMap.get('bookId'));

  readonly #commentService = inject(CommentService);
  readonly #auth = inject(AuthService);

  added = output<void>();

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

  get commentContent() {
    return this.form.get('content') as FormControl<string>;
  }

  get commentRating() {
    return this.form.get('rating') as FormControl<number>;
  }

  onSubmit() { 
    const isFromValid = this.form.valid;

    this.commentContent.markAsDirty();
    this.commentRating.markAsDirty();

    if(isFromValid) {
      const userId = this.#auth.userId();
      if (userId == null) {
        console.error('Cannot add comment: user is not authenticated');
        return;
      }

      const newComment: CommentCreateDto ={
        content: this.commentContent.value,
        rating: this.commentRating.value,
        chapterId: this.#chapterId,
        userId,
      };
      this.#commentService.addComment(newComment).subscribe({
        next: () => {
          this.router.navigate(['/books', this.#bookId, 'chapters', this.#chapterId]);
          this.added.emit();
        },
        error: (err) => {
          console.error('Error adding comment:', err);
        },
      });
    }

  }
}
