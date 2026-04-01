import { Component, effect, inject } from '@angular/core';
import { ArticleService } from '../../../shared/services/article.service';
import { DatePipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { NavbarComponent } from '../../navbar/navbar.component';
import { FooterComponent } from '../../footer/footer.component';
import { AuthService } from '../../../core/auth/auth.service';

@Component({
  selector: 'app-article-list',
  imports: [DatePipe, RouterLink, NavbarComponent, FooterComponent],
  templateUrl: './article-list.component.html',
  styleUrl: './article-list.component.css',
})
export class ArticleListComponent {
  readonly #articleService = inject(ArticleService);
  readonly #authService = inject(AuthService);
  readonly articleList = this.#articleService.getArticleList();

  readonly isAdmin = this.#authService.isAdmin();

  constructor() {
    effect(() => {
    if (!this.articleList.isLoading()) {
      console.log('count', this.articleList.value().length);
    }});
  }

}
