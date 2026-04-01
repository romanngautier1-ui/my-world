import { Component, inject } from '@angular/core';
import { BookService } from '../../../../shared/services/book.service';
import { Router, RouterLink } from '@angular/router';
import { DatePipe } from '@angular/common';
import { NavbarComponent } from '../../../navbar/navbar.component';
import { FooterComponent } from '../../../footer/footer.component';
import { AuthService } from '../../../../core/auth/auth.service';

@Component({
  selector: 'app-book-list',
  imports: [RouterLink, DatePipe, NavbarComponent, FooterComponent],
  templateUrl: './book-list.component.html',
  styleUrl: './book-list.component.css',
})
export class BookListComponent {
  readonly #router = inject(Router);
  readonly #bookService = inject(BookService);
  readonly #authService = inject(AuthService);
  readonly bookList = this.#bookService.getBookList();

  readonly isAdmin = this.#authService.isAdmin();

  // deleteBook(bookId: number): void {
  //   if (confirm('Êtes-vous sûr de vouloir supprimer ce livre ?')) {
  //     this.#bookService.deleteBook(bookId).subscribe(() => {
  //     });
  //   }
  // }

  deleteBook(bookId: number): void {
    this.#bookService.deleteBook(bookId).subscribe(() => {
      this.bookList.reload();
    });
  }
}
