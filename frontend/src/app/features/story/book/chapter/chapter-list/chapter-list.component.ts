import { Component, computed, inject, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { DatePipe } from '@angular/common';
import { BookService } from '../../../../../shared/services/book.service';
import { NavbarComponent } from '../../../../navbar/navbar.component';
import { FooterComponent } from "../../../../footer/footer.component";
import { AuthService } from '../../../../../core/auth/auth.service';
import { UserService } from '../../../../../shared/services/user.service';

@Component({
  selector: 'app-chapter-list',
  imports: [DatePipe, RouterLink, NavbarComponent, FooterComponent],
  templateUrl: './chapter-list.component.html',
  styleUrl: './chapter-list.component.css',
})
export class ChapterListComponent {
  readonly #chapterService = inject(BookService);
  readonly #route = inject(ActivatedRoute);
  readonly #authService = inject(AuthService);
  readonly #userService = inject(UserService);
  readonly bookId = Number(this.#route.snapshot.paramMap.get('bookId'));
  readonly chapterList = this.#chapterService.getBookById(this.bookId);

  readonly userID = this.#authService.userId();
  readonly isAdmin = this.#authService.isAdmin();
  readonly isAuthenticated = this.#authService.isAuthenticated();
  readonly user = this.#userService.getUserById(this.userID);

	readonly readChapterIds = computed(() => {
    if (!this.isAuthenticated) {
      return new Set<number>();
    }
		const reads = this.user.value()?.lastReads ?? [];
		return new Set(reads.map((r) => r.chapterId));
	});

	isChapterRead(chapterId: number): boolean {
		return this.readChapterIds().has(chapterId);
	}

  readonly searchTerm = signal('');

  filteredChapterList = computed(() => {
    const searchTerm = this.searchTerm().toLowerCase().trim();
    const chapterList = this.chapterList.value()?.chapters ?? [];

    return chapterList.filter(chapter =>
      chapter.title.toLowerCase().includes(searchTerm)
    );
  });

}
