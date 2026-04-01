import { HttpClient, httpResource, HttpResourceRef } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { ChapterReadCreateDto, ChapterReadDto } from '../dtos/chapter-read.dto';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class ChapterReadService {
  readonly #apiUrl = `${environment.apiUrl}/chapter-reads`;
  readonly #http = inject(HttpClient);

  getChapterList(): HttpResourceRef<ChapterReadDto[]> {
    return httpResource<ChapterReadDto[]>(() => ({ url: this.#apiUrl}), { defaultValue: [] });
  }

  getChapterReadByUserIdAndChapterId(userId: number | null, chapterId: number): HttpResourceRef<boolean> {
    return httpResource<boolean>(
      () => {
        if (userId == null) return undefined;
        return { url: this.#apiUrl + '/exists', params: { userId, chapterId } };
      },
      { defaultValue: false }
    );
  }

  addChapterRead(chapterRead: ChapterReadCreateDto): Observable<ChapterReadCreateDto> {
        return this.#http.post<ChapterReadCreateDto>(this.#apiUrl, chapterRead);
  }

  deleteChapterRead(id: number): Observable<void> {
    const url = this.#apiUrl + '/' + id;
    return this.#http.delete<void>(url);
 }
}
