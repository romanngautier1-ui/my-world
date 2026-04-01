import { Component, inject, Injector, runInInjectionContext } from '@angular/core';
import { ArticleService } from '../../../shared/services/article.service';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { DatePipe } from '@angular/common';
import { NavbarComponent } from "../../navbar/navbar.component";
import { FooterComponent } from '../../footer/footer.component';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { AuthService } from '../../../core/auth/auth.service';

@Component({
  selector: 'app-article',
  imports: [DatePipe, RouterLink, NavbarComponent, FooterComponent],
  templateUrl: './article-profile.component.html',
  styleUrls: ['./article-profile.component.css'],
  standalone: true,
})
export class ArticleProfileComponent {
  readonly #route = inject(ActivatedRoute);
  readonly #router = inject(Router);
  readonly #articleService = inject(ArticleService);
  readonly #authService = inject(AuthService);
  readonly #inj = inject(Injector);
  #articleId = Number(this.#route.snapshot.paramMap.get('id'));
  article: any = runInInjectionContext(this.#inj, () => this.#articleService.getArticleById(this.#articleId));
  articleIdNeighbors: any = runInInjectionContext(this.#inj, () => this.#articleService.getArticleNeighbors(this.#articleId));

  readonly isAdmin = this.#authService.isAdmin();


  constructor() {
    this.#route.paramMap.pipe(takeUntilDestroyed()).subscribe((params) => {
      const id = Number(params.get('id'));
      this.#articleId = id;
      this.article = runInInjectionContext(this.#inj, () => this.#articleService.getArticleById(id));
      this.articleIdNeighbors = runInInjectionContext(this.#inj, () => this.#articleService.getArticleNeighbors(id));
    });
  }

  isPrevArticleAvailable(): boolean {
    return this.articleIdNeighbors.value()[0] != undefined;
  }

  isNextArticleAvailable(): boolean {
    return this.articleIdNeighbors.value()[1] != undefined;
  }

  previousId(): number | null {
    return this.articleIdNeighbors.value()[0];
  }

  nextId(): number | null {
    return this.articleIdNeighbors.value()[1];
  }
 
  previousArticle() {
    this.#router.navigate(['/articles', this.previousId()]);
  }

  nextArticle() {
    this.#router.navigate(['/articles', this.nextId()]);
  }

  deleteArticle() {
    this.#articleService.deleteArticle(this.#articleId).subscribe(() => {
      this.#router.navigate(['/articles']);
    });
  }
}
