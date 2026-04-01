import { Component, inject, input, output, ViewChild } from '@angular/core';
import { DatePipe } from '@angular/common';

import { CommentDto } from '../../../../../../shared/dtos/comment.dto';
import { CommentService } from '../../../../../../shared/services/comment.service';
import { CommentEditModalComponent } from '../comment-edit-modal/comment-edit-modal.component';

@Component({
  selector: 'app-comment-modal',
  imports: [DatePipe, CommentEditModalComponent],
  templateUrl: './comment-modal.component.html',
  styleUrl: './comment-modal.component.css',
})
export class CommentModalComponent {
  isOpen = input(false);
  readonly comment = input<CommentDto | null>(null);

  readonly #commentService = inject(CommentService);

  @ViewChild(CommentEditModalComponent) private readonly editModal?: CommentEditModalComponent;

  isEdit = false;

  close = output<void>();
  deleted = output<void>();

  onBackdropClick() {
    this.close.emit();
    this.isEdit = false;
  }

  onEditClick() {
    if (!this.isEdit) {
      this.isEdit = true;
      return;
    }

    const comment = this.comment();
    if (!comment) {
      return;
    }

    const patch = this.editModal?.getUpdateDtoIfValid() ?? null;
    if (!patch) {
      return;
    }

    this.#commentService.updateComment(comment.id, patch).subscribe({
      next: (updated) => {
        // Keep the UI in sync without forcing a reload
        comment.content = updated.content;
        comment.rating = updated.rating;
        comment.createdAt = updated.createdAt;
        comment.username = updated.username;
        this.isEdit = false;
        this.close.emit();
      },
      error: (err) => {
        console.error('Error updating comment:', err);
      },
    });
  }

  deleteComment() {
    if (this.comment()) {
      this.#commentService.deleteComment(this.comment()!.id).subscribe(() => {
        this.close.emit();
        this.deleted.emit();
      });
    }
  }
}
