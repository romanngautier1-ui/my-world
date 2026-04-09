import { HttpClient, httpResource, HttpResourceRef } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { ChapterCreateDto, ChapterDto, ChapterListDto, ChapterUpdateDto } from '../dtos/chapter.dto';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ActivatedRoute, Router } from '@angular/router';

@Injectable({
  providedIn: 'root',
})
export class ChapterService {
  readonly #apiUrl = `${environment.apiUrl}/chapters`;
  readonly #http = inject(HttpClient);

  getChapterList(): HttpResourceRef<ChapterListDto[]> {
    return httpResource<ChapterListDto[]>(() => ({ url: this.#apiUrl}), { defaultValue: [] });
  }

  getChapterById(chapterId: number): HttpResourceRef<ChapterDto> {
    return httpResource<ChapterDto>(
      () => ({ url: this.#apiUrl + '/' + chapterId }),
      {
        defaultValue: {
          id: 0,
          title: '',
          number: 1,
          content: '',
          pdfUrl: null,
          book: {
            id: 0,
            title: '',
            number: 0,
          },
          createdAt: new Date(0),
          comments: [],
          like: 0,
        } as ChapterDto,
      }
    );
  }

  getChapterPdfById(chapterId: number): Observable<Blob> {
    const url = this.#apiUrl + '/' + chapterId + '/pdf';
    return this.#http.get(url, { responseType: 'blob' });
  }

  getChapterNeighbors(chapterId: number): HttpResourceRef<(number | undefined)[]> {
    return httpResource<(number | undefined)[]>(() => ({ url: this.#apiUrl + '/' + chapterId + '/neighbors' }), { defaultValue: [] });
  }

  addChapter(chapter: Omit<ChapterCreateDto, 'id'>): Observable<ChapterCreateDto> {
      const formData = new FormData();
      formData.append('title', chapter.title);
      formData.append('number', String(chapter.number));
      formData.append('bookId', String(chapter.bookId));

      if (chapter.content != null) {
        formData.append('content', chapter.content);
      }

      if (chapter.uploadFile) {
        formData.append('pdfFile', chapter.uploadFile, chapter.uploadFile.name);
      }
        return this.#http.post<ChapterCreateDto>(this.#apiUrl, formData);
  }
    
  updateChapter(chapterId: number, patch: ChapterUpdateDto): Observable<ChapterDto> {
    const url = this.#apiUrl + '/' + chapterId;

    const formData = new FormData();
    if (patch.title != null) {
      formData.append('title', patch.title);
    }

    if (patch.number != null) {
      formData.append('number', String(patch.number));
    }

    if (patch.content != null) {
      formData.append('content', patch.content);
    }

    if (patch.bookId != null) {
      formData.append('bookId', String(patch.bookId));
    }

    if (patch.uploadFile) {
      formData.append('pdfFile', patch.uploadFile, patch.uploadFile.name);
    }
    return this.#http.patch<ChapterDto>(url, formData);
  }

  updateIncrementLike(chapterId: number): Observable<ChapterDto> {
    const url = this.#apiUrl + '/' + chapterId + '/likes/increment';
    return this.#http.post<ChapterDto>(url, {});
  }

  updateDecrementLike(chapterId: number): Observable<ChapterDto> {
    const url = this.#apiUrl + '/' + chapterId + '/likes/decrement';
    return this.#http.post<ChapterDto>(url, {});
  }
    
  deleteChapter(id: number): Observable<void> {
    const url = this.#apiUrl + '/' + id;
    return this.#http.delete<void>(url);
  }
}
