import { Routes } from '@angular/router';
import { HomeComponent } from './features/home/home.component';
import { AboutComponent } from './features/about/about.component';
import { ArticleListComponent } from './features/article/article-list/article-list.component';
import { ArticleAddComponent } from './features/article/article-add/article-add.component';
import { ArticleEditComponent } from './features/article/article-edit/article-edit.component';
import { ArticleProfileComponent } from './features/article/article-profile/article-profile.component';
import { BookListComponent } from './features/story/book/book-list/book-list.component';
import { BookAddComponent } from './features/story/book/book-add/book-add.component';
import { BookEditComponent } from './features/story/book/book-edit/book-edit.component';
import { ChapterListComponent } from './features/story/book/chapter/chapter-list/chapter-list.component';
import { ChapterAddComponent } from './features/story/book/chapter/chapter-add/chapter-add.component';
import { ChapterEditComponent } from './features/story/book/chapter/chapter-edit/chapter-edit.component';
import { ChapterProfileComponent } from './features/story/book/chapter/chapter-profile/chapter-profile.component';
import { authGuard } from './core/auth/auth.guard';
import { RegisterComponent } from './features/auth/register/register.component';
import { LoginComponent } from './features/auth/login/login.component';
import { PageNotFoundComponent } from './features/page-not-found/page-not-found.component';
import { roleGuard } from './core/auth/role.guard';
import { ContactComponent } from './features/contact/contact.component';
import { VerifyEmailComponent } from './features/auth/verify-email/verify-email.component';
import { UserComponent } from './features/user/user.component';
import { ResetPasswordComponent } from './features/auth/reset-password/reset-password.component';
import { ResetPasswordLinkComponent } from './features/auth/reset-password-link/reset-password-link.component';

export const routes: Routes = [
    { path: 'login',    component: LoginComponent, title: 'Page de connexion' },
    { path: 'reset-password', component: ResetPasswordComponent, title: 'Réinitialisation du mot de passe' },
    { path: 'reset-password-link', component: ResetPasswordLinkComponent, title: 'Réinitialisation du mot de passe' },
    { path: 'register', component: RegisterComponent, title: 'Enregistrement' },
    { path: 'verify-email', component: VerifyEmailComponent, title: 'Vérification de l\'email' },
    { path: 'home', component: HomeComponent, title: 'My World' },
    { path: 'about', component: AboutComponent, title: 'About' },

    { path: 'articles', component: ArticleListComponent, title: 'Articles'},
    { path: 'articles/add', component: ArticleAddComponent, canActivate: [roleGuard], data: { roles: ['ROLE_ADMIN'] }, title: 'Ajout d\'un Article'},
    { path: 'articles/:id/edit', component: ArticleEditComponent, canActivate: [roleGuard], data: { roles: ['ROLE_ADMIN'] }, title: 'Modification d\'un Article'},
    { path: 'articles/:id', component: ArticleProfileComponent, title: 'Article Id', runGuardsAndResolvers: 'always' },

    { path: 'books', component: BookListComponent, title: 'Books' },
    { path: 'books/add', component: BookAddComponent, canActivate: [roleGuard], data: { roles: ['ROLE_ADMIN'] }, title: 'Ajout d\'un livre' },
    { path: 'books/:id/edit', component: BookEditComponent, canActivate: [roleGuard], data: { roles: ['ROLE_ADMIN'] }, title: 'Modification d\'un livre' },

    { path: 'books/:bookId/chapters', component: ChapterListComponent, title: 'Chapters' },
    { path: 'books/:bookId/chapters/add', component: ChapterAddComponent, canActivate: [roleGuard], data: { roles: ['ROLE_ADMIN'] }, title: 'Ajout d\'un chapitre' },
    { path: 'books/:bookId/chapters/:chapterId/edit', component: ChapterEditComponent, canActivate: [roleGuard], data: { roles: ['ROLE_ADMIN'] }, title: 'Modification d\'un chapitre' },
    { path: 'books/:bookId/chapters/:chapterId', component: ChapterProfileComponent, canActivate: [authGuard], title: 'Chapter' },
    { path: 'books/:bookId/chapters/:chapterId/html', component: ChapterProfileComponent, canActivate: [authGuard], title: 'Chapter' },

    { path: 'contact', component: ContactComponent, title: 'Contact' },

    { path: 'me', component: UserComponent, canActivate: [authGuard], title: 'Mon profil' },

    { path: 'not-found', component: PageNotFoundComponent, title: 'Page non trouvée' },

    { path: '', redirectTo: '/home', pathMatch: 'full' },
    { path: '**', component: PageNotFoundComponent , title: 'Page non trouvée' },

];
