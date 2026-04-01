import { HttpClient, httpResource, HttpResourceRef } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { BookCreateDto, BookDto, BookListDto, BookUpdateDto } from '../dtos/book.dto';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { form } from '@angular/forms/signals';

@Injectable({
  providedIn: 'root',
})
export class BookService {
  readonly #apiUrl = `${environment.apiUrl}/books`;
  readonly #http = inject(HttpClient);

  getBookList(): HttpResourceRef<BookListDto[]> {
    return httpResource<BookListDto[]>(() => ({ url: this.#apiUrl}), { defaultValue: [] });
  }

  getBookById(bookId: number): HttpResourceRef<BookDto> {
    return httpResource<BookDto>(
      () => ({ url: this.#apiUrl + '/' + bookId }),
      {
        defaultValue: {
          id: 0,
          title: '',
          number: 0,
          description: '',
          createdAt: new Date(0),
          chapters: [],
          urlImage: '',
        } as BookDto,
      }
    );
  }

  addBook(book: BookCreateDto): Observable<BookCreateDto> {
    // return this.#http.post<BookCreateDto>(this.#apiUrl, book);
    const formData = new FormData();
        formData.append('title', book.title);
        formData.append('number', String(book.number));
        formData.append('description', book.description);

        if (book.urlImage != null && book.urlImage.trim().length > 0) {
          formData.append('urlImage', book.urlImage);
        }
    
        if (book.uploadImage) {
          formData.append('imageFile', book.uploadImage, book.uploadImage.name);
        }
    
        return this.#http.post<BookCreateDto>(this.#apiUrl, formData);
  }

  updateBook(bookId: number, patch: BookUpdateDto): Observable<BookDto> {
    const url = this.#apiUrl + '/' + bookId;
    // return this.#http.patch<BookDto>(url, patch);
    
        const formData = new FormData();
        if (patch.title != null) {
          formData.append('title', patch.title);
        }
    
        if (patch.number != null) {
          formData.append('number', String(patch.number));
        }

        if (patch.description != null) {
          formData.append('description', patch.description);
        }
    
        if (patch.urlImage != null && patch.urlImage.trim().length > 0) {
          formData.append('urlImage', patch.urlImage);
        }
    
        if (patch.uploadImage) {
          formData.append('imageFile', patch.uploadImage, patch.uploadImage.name);
        }
        return this.#http.patch<BookDto>(url, formData);
  }

  deleteBook(id: number): Observable<void> {
    const url = this.#apiUrl + '/' + id;
    return this.#http.delete<void>(url);
  }
}
