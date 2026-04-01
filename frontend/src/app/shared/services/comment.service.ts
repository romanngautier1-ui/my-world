import { HttpClient, httpResource, HttpResourceRef } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { CommentCreateDto, CommentDto, CommentUpdateDto } from '../dtos/comment.dto';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class CommentService {
  readonly #apiUrl = `${environment.apiUrl}/comments`;
  readonly #http = inject(HttpClient);

  getCommentById(commentId: number): HttpResourceRef<CommentDto> {
    return httpResource<CommentDto>(() => ({ url: this.#apiUrl + '/' + commentId }), { defaultValue: {} as CommentDto });
  }

  getCommentByUserIdAndChapterId(userId: number | null, chapterId: number): HttpResourceRef<Boolean> {
    return httpResource<Boolean>(
      () => {
        if (userId == null) return undefined;
        return { url: this.#apiUrl + '/exists', params: { userId, chapterId } };
      },
      { defaultValue: false }
    );
  }

  addComment(comment: CommentCreateDto): Observable<CommentCreateDto> {
        return this.#http.post<CommentCreateDto>(this.#apiUrl, comment);
      }
    
  updateComment(commentId: number, patch: CommentUpdateDto): Observable<CommentDto> {
    const url = this.#apiUrl + '/' + commentId;
    return this.#http.patch<CommentDto>(url, patch);
  }
    
  deleteComment(id: number): Observable<void> {
    const url = this.#apiUrl + '/' + id;
    return this.#http.delete<void>(url);
  }
}
