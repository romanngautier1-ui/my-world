import { Component, computed, inject, Injector, runInInjectionContext } from '@angular/core';
import { ChapterService } from '../../../../../shared/services/chapter.service';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { DatePipe } from '@angular/common';
import { CommentAddComponent } from '../comment/comment-add/comment-add.component';
import { ChapterReadService } from '../../../../../shared/services/chapter-read.service';
import { CommentService } from '../../../../../shared/services/comment.service';
import { CommentModalComponent } from '../comment/comment-modal/comment-modal.component';
import { AuthService } from '../../../../../core/auth/auth.service';
import { FooterComponent } from '../../../../footer/footer.component';
import { ChapterReadCreateDto } from '../../../../../shared/dtos/chapter-read.dto';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';

@Component({
  selector: 'app-chapter',
  imports: [DatePipe, RouterLink, CommentAddComponent, CommentModalComponent, FooterComponent],
  templateUrl: './chapter-profile.component.html',
  styleUrl: './chapter-profile.component.css',
})
export class ChapterProfileComponent {
  readonly #route = inject(ActivatedRoute);
  readonly #router = inject(Router);
  readonly #chapitreService = inject(ChapterService);
  readonly #chapterReadService = inject(ChapterReadService);
  readonly #commentService = inject(CommentService);
  readonly #auth = inject(AuthService);
  #chapterId = Number(this.#route.snapshot.paramMap.get('chapterId'));
  readonly #bookId = Number(this.#route.snapshot.paramMap.get('bookId'));
  readonly userId = this.#auth.userId();
  readonly #username = this.#auth.username();
  readonly #inj = inject(Injector);
  readonly isAdmin = this.#auth.isAdmin();

  chapter = this.#chapitreService.getChapterById(this.#chapterId);
  chapterHtml = this.#chapitreService.getChapterHtmlById(this.#chapterId);
  isChapterRead = this.#chapterReadService.getChapterReadByUserIdAndChapterId(this.userId, this.#chapterId);
  isCommentWritten = this.#commentService.getCommentByUserIdAndChapterId(this.userId, this.#chapterId);
  chapterNeighbors = this.#chapitreService.getChapterNeighbors(this.#chapterId);

  constructor() {
    this.#route.paramMap.pipe(takeUntilDestroyed()).subscribe((params) => {
      const id = Number(params.get('chapterId'));
      this.#chapterId = id;
      this.chapter = runInInjectionContext(this.#inj, () => this.#chapitreService.getChapterById(id));
      this.chapterHtml = runInInjectionContext(this.#inj, () => this.#chapitreService.getChapterHtmlById(id));
      this.isChapterRead = runInInjectionContext(this.#inj, () => this.#chapterReadService.getChapterReadByUserIdAndChapterId(this.userId, id));
      this.isCommentWritten = runInInjectionContext(this.#inj, () => this.#commentService.getCommentByUserIdAndChapterId(this.userId, id));
      this.chapterNeighbors = runInInjectionContext(this.#inj, () => this.#chapitreService.getChapterNeighbors(id));
    });
  }

  readonly averageRating = computed(() => {
    const comments = this.chapter.value().comments;
    if (comments.length === 0) {
      return null;
    }
    const totalRating = comments.reduce((sum, comment) => sum + comment.rating, 0);
    return totalRating / comments.length;
  });

  readonly commentByUsername = computed(() =>
    this.chapter.value().comments?.find((comment) => comment.username === this.#username) ?? null
  );

  isLiked: boolean = false;

  isModalOpen = false;

  onCommentAdded() {
    this.chapter.reload();
    this.isCommentWritten.reload();
  }

  onCommentDeleted() {
    this.isModalOpen = false;
    this.chapter.reload();
    this.isCommentWritten.reload();
  }

  deleteChapter() {
    this.#chapitreService.deleteChapter(this.#chapterId).subscribe(() => {
      this.#router.navigate(['/books', this.#bookId, 'chapters']);
    });
  }

  downloadPdf() {
    this.#chapitreService.getChapterPdfById(this.#chapterId).subscribe((blob) => {
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = `chapter-${this.#chapterId}.pdf`;
      a.click();
      window.URL.revokeObjectURL(url);
    });
  }

  onChapterRead() {
    const chapterRead: ChapterReadCreateDto = {
            userId: this.#auth.userId(),
            chapterId: this.#chapterId
          }
    this.#chapterReadService.addChapterRead(chapterRead).subscribe(() => {
      this.isChapterRead.reload();
    });
  }

  onChapterLike() {
    if(this.isLiked) {
      this.onDecrementChapterLike();
    } else {
      this.onIncrementChapterLike();
    }
    this.isLiked = !this.isLiked;
  }

  onIncrementChapterLike() {
    const currentLikes = this.chapter.value().like || 0;
    this.chapter.value().like = currentLikes + 1;
    this.#chapitreService.updateIncrementLike(this.#chapterId).subscribe(() => {
      this.chapter.reload();
    });
  }

  onDecrementChapterLike() {
    const currentLikes = this.chapter.value().like || 0;
    this.chapter.value().like = currentLikes > 0 ? currentLikes - 1 : 0; 
    this.#chapitreService.updateDecrementLike(this.#chapterId).subscribe(() => {
      this.chapter.reload();
    }); 
  }

  previousId(): number | null {
    return this.chapterNeighbors.value()[0] ?? null;
  }

  nextId(): number | null {
    return this.chapterNeighbors.value()[1] ?? null;
  }

  previousChapter() {
    this.#router.navigate(['books', this.#bookId, 'chapters', this.previousId()]);
  }

  nextChapter() {
    this.#router.navigate(['books', this.#bookId, 'chapters', this.nextId()]);
  }
}
