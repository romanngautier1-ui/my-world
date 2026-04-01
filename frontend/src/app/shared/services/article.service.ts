import { inject, Injectable } from '@angular/core';
import { HttpClient, httpResource, HttpResourceRef } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import {
  ArticleCreateDto,
  ArticleDto,
  ArticleListDto,
  ArticleUpdateDto,
} from '../dtos/article.dto';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class ArticleService {
  
  readonly #apiUrl = `${environment.apiUrl}/articles`;
  readonly #http = inject(HttpClient);

  getArticleList(): HttpResourceRef<ArticleListDto[]> {
    return httpResource<ArticleListDto[]>(() => ({ url: this.#apiUrl}), { defaultValue: [] });
  }

  getArticleById(articleId: number): HttpResourceRef<ArticleDto> {
    return httpResource<ArticleDto>(() => ({ url: this.#apiUrl + '/' + articleId }), { defaultValue: {} as ArticleDto });
  }

  getArticleNeighbors(articleId: number): HttpResourceRef<{ previousId: number | null, nextId: number | null }> {
    const url = this.#apiUrl + '/' + articleId + '/neighbors';
    return httpResource<{ previousId: number | null, nextId: number | null }>(() => ({ url }), { defaultValue: { previousId: null, nextId: null } });
  }

  addArticle(article: Omit<ArticleCreateDto, 'id'>): Observable<ArticleCreateDto> {
    const formData = new FormData();
    formData.append('title', article.title);
    formData.append('content', article.content);

    if (article.userId != null) {
      formData.append('userId', String(article.userId));
    }

    if (article.urlImage != null && article.urlImage.trim().length > 0) {
      formData.append('urlImage', article.urlImage);
    }

    if (article.uploadImage) {
      formData.append('imageFile', article.uploadImage, article.uploadImage.name);
    }

    return this.#http.post<ArticleCreateDto>(this.#apiUrl, formData);
  }
  
  updateArticle(articleId: number, patch: ArticleUpdateDto): Observable<ArticleDto> {
    const url = this.#apiUrl + '/' + articleId;

    const formData = new FormData();
    if (patch.title != null) {
      formData.append('title', patch.title);
    }

    if (patch.content != null) {
      formData.append('content', patch.content);
    }

    if (patch.urlImage != null && patch.urlImage.trim().length > 0) {
      formData.append('urlImage', patch.urlImage);
    }

    if (patch.uploadImage) {
      formData.append('imageFile', patch.uploadImage, patch.uploadImage.name);
    }
    return this.#http.patch<ArticleDto>(url, formData);
  }
  
  deleteArticle(id: number): Observable<void> {
    const url = this.#apiUrl + '/' + id;
    return this.#http.delete<void>(url);
  }
  
}
