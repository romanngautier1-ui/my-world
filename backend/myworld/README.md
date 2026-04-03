# MyWorld — Backend

Backend Spring Boot du projet **MyWorld**.

## Description du projet

MyWorld est une API REST qui alimente une application de lecture/écriture avec :

- **Bibliothèque** : gestion de **livres** et **chapitres**.
- **Contenus éditoriaux** : gestion d’**articles** (avec image).
- **Interaction** : **commentaires** sur les chapitres et **historique de lecture** (chapitres lus).
- **Comptes utilisateurs** : inscription, **vérification email**, connexion **JWT**, réinitialisation de mot de passe.
- **Notifications email** : envoi automatique d’emails lors de l’ajout d’un article / chapitre (utilisateurs actifs).
- **Uploads** : stockage de fichiers (images + PDF) sur disque, servis via `GET /api/uploads/{filename}`.
- **Service PDF** : un chapitre peut être stocké soit comme **texte brut**, soit comme **URL vers un PDF uploadé** ; le backend peut **extraire le texte du PDF** et le rendre en HTML.

## Stack

- Java **21**
- Spring Boot **4.0.3**
- Maven (wrapper: `./mvnw`)
- Persistence: Spring Data JPA
- Mapping: MapStruct + Lombok

## Prérequis

- JDK 21 installé
- (Optionnel, profil `local`) MySQL en local

## Lancer le projet

### 1) Démarrage sans profil (config via variables d'environnement)

Le fichier `src/main/resources/application.properties` contient uniquement de la configuration **commune** (sans secrets).
Pour démarrer sans profil, il faut fournir la configuration via variables d'environnement.

- Exemple (DB + JWT) :

```bash
export SPRING_DATASOURCE_URL='jdbc:mysql://localhost:3306/myworld'
export SPRING_DATASOURCE_USERNAME='myworld'
export SPRING_DATASOURCE_PASSWORD='myworld'
export JWT_SECRET='change-me-please-change-me-please-32bytes+'
./mvnw spring-boot:run
```

### 2) Profil `local` (MySQL + seed)

Le profil `local` est prévu pour démarrer avec MySQL et initialiser la base avec un script SQL.

- Configuration: `src/main/resources/application-local.properties`
  - Contient `spring.datasource.*` + config JPA + activation du seed `classpath:db/mysql/data.sql`

- Run avec profil `local`:

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=local
```

Tu peux surcharger n'importe quel paramètre via env (ex: `SPRING_DATASOURCE_URL`, `JWT_SECRET`, `APP_FRONTEND_BASE_URL`).

### 3) Profil `prod`

Le profil `prod` s'attend à recevoir toute la configuration (DB/JWT/CORS/URLs) via variables d'environnement ou un gestionnaire de secrets.

```bash
java -jar target/myworld-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```

Ou via le jar:

```bash
java -jar target/myworld-0.0.1-SNAPSHOT.jar --spring.profiles.active=local
```

## Tests

Les tests utilisent une base H2 en mémoire via `src/test/resources/application.properties`.

- Lancer les tests:

```bash
./mvnw test
```

## Routes API

Les routes ci-dessous sont dérivées des controllers Spring (`src/main/java/com/app/myworld/controller`).
Par défaut, Spring Security est en mode **stateless** (JWT) :

- **Publiques**: endpoints d’auth (`/api/auth/**`), `GET /api/uploads/**`, `GET /api/books` / `GET /api/books/{id}`, `GET /api/articles` / `GET /api/articles/{id}` / `GET /api/articles/{id}/neighbors`, endpoints mail (`/api/mails/**`).
- **Authentifiées**: tout le reste.
- **ADMIN**: certaines routes sont restreintes via `@PreAuthorize("hasRole('ADMIN')")`.

### Auth (JWT) et gestion de compte

- `POST /api/auth/register` (public)
  - Body JSON (`RegisterRequest`) : `username`, `email`, `password`
  - Effet : crée l’utilisateur (role `USER`, `isActive=false`), puis déclenche l’envoi d’un email de vérification.
- `GET /api/auth/verify-email?token=...` (public)
  - Effet : active le compte (`isActive=true`).
- `POST /api/auth/login` (public)
  - Body JSON (`LoginRequest`) : `username`, `password`
  - Réponse (`AuthResponse`) : `token`, `username`, `email`, `role`
- `POST /api/auth/reset-password` (public)
  - Body JSON: **string** contenant l’email (ex: `"user@example.com"`)
  - Effet : si l’utilisateur existe, génère un token de reset (30 min) et envoie un lien vers le front.
- `POST /api/auth/reset-password-link` (public)
  - Body JSON (`ResetPasswordRequest`) : `token`, `newPassword`

Utilisation du token :

- Header: `Authorization: Bearer <JWT>`
- Le JWT inclut notamment `userId` et `tokenVersion`. Le backend invalide un token si la `tokenVersion` ne correspond plus à celle stockée en base.

Routes utilisateur :

- `GET /api/users/me` (authentifié)
- `PATCH /api/users/me/change-password` (authentifié)
- `PATCH /api/users/me/update-current-user` (authentifié)
  - Effet : met à jour l’utilisateur et retourne un **nouveau JWT** (rotation via `tokenVersion`).
- `DELETE /api/users/me` (ADMIN)

### Uploads (images + PDF)

Le backend ne fournit pas d’endpoint “upload générique”. Les uploads se font via certains endpoints `multipart/form-data` (création/mise à jour), et les fichiers sont ensuite servis par:

- `GET /api/uploads/{filename}` (public)

Stockage & limites:

- Dossier: `app.upload.dir` (par défaut `${user.home}/myworld/uploads`)
- Taille max: `10MB` par fichier (`spring.servlet.multipart.max-file-size`) et `12MB` par requête.
- Extensions supportées:
  - images: `jpg`, `jpeg`, `png`, `gif`, `webp`
  - PDF: `pdf`

Cas d’usage:

- Articles/livres : `imageFile` (upload) **ou** `urlImage` (URL externe)
- Chapitres : `pdfFile` (upload) **ou** `content` (texte brut)

### Service PDF (chapitres)

Un chapitre peut stocker son contenu comme une URL vers un PDF uploadé (`.../api/uploads/<uuid>.pdf`) ou comme texte brut.

- `GET /api/chapters/{id}/html` (authentifié)
  - Retourne un HTML “safe” en `<p>` :
    - si `content` pointe vers un PDF uploadé, le backend extrait le texte via **PDFBox** puis formate en paragraphes
    - sinon, formate directement le texte brut en paragraphes
- `GET /api/chapters/{id}/pdf` (ADMIN)
  - Télécharge le PDF associé au chapitre si le contenu est un upload PDF.

### Articles

- `GET /api/articles`
- `GET /api/articles/{id}`
- `GET /api/articles/{id}/neighbors`
- `POST /api/articles` (ADMIN) (multipart) (envoie de mail automatique pour avertir les utilisateurs d'un nouvel article)
- `PATCH /api/articles/{id}` (ADMIN) (multipart)
- `DELETE /api/articles/{id}` (ADMIN)

### Livres

- `GET /api/books`
- `GET /api/books/{id}`
- `POST /api/books` (ADMIN) (multipart)
- `PATCH /api/books/{id}` (ADMIN) (multipart)
- `DELETE /api/books/{id}` (ADMIN)

### Chapitres

- `GET /api/chapters`
- `GET /api/chapters/{id}`
- `GET /api/chapters/{id}/html`
- `GET /api/chapters/{id}/pdf` (ADMIN)
- `GET /api/chapters/{id}/neighbors`
- `POST /api/chapters/{id}/likes/increment`
- `POST /api/chapters/{id}/likes/decrement`
- `POST /api/chapters` (ADMIN) (multipart) (envoie de mail automatique pour avertir les utilisateurs d'un nouveau chapitre)
- `PATCH /api/chapters/{id}` (ADMIN) (multipart)
- `DELETE /api/chapters/{id}` (ADMIN)

### Lecture de chapitres (Historique des chapitres lus)

- `GET /api/chapter-reads`
- `GET /api/chapter-reads/exists?userId=...&chapterId=...`
- `POST /api/chapter-reads`
- `DELETE /api/chapter-reads/{id}`

### Commentaires

- `GET /api/comments`
- `GET /api/comments/{id}`
- `GET /api/comments/exists?userId=...&chapterId=...`
- `POST /api/comments` (authentifié)
- `PATCH /api/comments/{id}` (authentifié)
- `DELETE /api/comments/{id}` (authentifié)

### Utilisateurs

- `GET /api/users/me` (authentifié)
- `PATCH /api/users/me/change-password` (authentifié)
- `PATCH /api/users/me/update-current-user` (authentifié)
- `DELETE /api/users/me` (ADMIN)

### Mails

Endpoints actuellement **publics** (voir `SecurityConfig`) :

- `POST /api/mails/send`
- `POST /api/mails/sendWithAttachment`
- `POST /api/mails/receive`

### Uploads

- `GET /api/uploads/{filename}` (public) — sert une image/PDF déjà stocké sur disque.

## Structure du projet

- `src/main/java/com/app/myworld/`
  - `controller/` : contrôleurs REST (Auth, Article, Book, Chapter, ChapterRead, Comment, Email, Upload, User)
  - `service/` : logique métier
  - `repository/` : repositories Spring Data
  - `model/` : entités JPA
  - `dto/` : DTOs (requests/responses)
  - `mapper/` : mappers MapStruct
  - `security/` : sécurité (Spring Security)
  - `config/` : configuration applicative

- `src/main/resources/`
  - `application.properties` : configuration par défaut
  - `application-local.properties` : profil `local` (MySQL + seed)
  - `db/mysql/data.sql` : données de seed

## Notes

- Les implémentations de mappers MapStruct sont générées dans `target/generated-sources/annotations`.
- Si ton IDE indique que la configuration Maven/Java n’est pas à jour avec le `pom.xml`, un rechargement du workspace Java/Maven règle généralement le problème.

## Pistes d’amélioration / idées de fonctionnalités

Ces idées sont volontairement “incrémentales” et s’appuient sur les patterns déjà présents (services, DTO, events, security).

### Sécurité / Auth

- **Endpoint de logout** : incrémenter `User.tokenVersion` pour invalider le JWT courant côté serveur (le mécanisme existe déjà via `generateNewToken`).
- **Refresh tokens** (optionnel) : access token court + refresh token (rotation + révocation), plutôt que JWT longue durée.
- **Sécuriser /api/mails/** : aujourd’hui les endpoints mail sont publics (voir `SecurityConfig`) → ajouter auth + rate-limit + anti-abus.
- **Validation d’entrée homogène** : standardiser les erreurs (format JSON unique) via `@ControllerAdvice` (déjà un `GlobalExceptionHandler`).

### Uploads / Fichiers

- **Nettoyage des fichiers orphelins** : lors d’un delete d’article/livre/chapitre, supprimer le fichier uploadé associé (si le contenu pointe vers `/api/uploads/...`).
- **Mise en cache** : `Cache-Control` plus agressif pour les images (si les filenames sont immuables UUID), tout en gardant `no-cache` si souhaité pour les PDFs.

### PDF / Chapitres

- **Prévisualisation plus riche** : conserver en base une version “texte extrait” (ou HTML) à la création/màj, pour éviter de retraiter le PDF à chaque `GET /html`.

### Paiement (Stripe) + facture PDF (en réflexion)

Objectif envisagé (pas encore figé) : permettre à un utilisateur de **payer** pour **lire des chapitres** ou **débloquer un livre entier**, puis générer une **facture PDF**.

Pistes d’implémentation backend :

- **Modèle d’accès (entitlements)** :
  - `BOOK_UNLOCK` (débloque tout un livre) et/ou `CHAPTER_UNLOCK` (débloque un chapitre).
  - Stocker une “preuve d’accès” en base (ex: table `entitlements` liée à `userId`, `bookId/chapterId`, `grantedAt`, `sourcePaymentId`).
- **Stripe Checkout (recommandé pour démarrer)** :
  - Endpoint backend pour créer une session Checkout (ex: `POST /api/billing/checkout` avec `{ type, bookId?, chapterId? }`).
  - Redirection front vers Stripe, puis retour front sur success/cancel.
  - **Webhooks Stripe** (ex: `POST /api/billing/webhooks/stripe`) pour valider le paiement côté serveur et accorder l’accès.
- **Sécurisation & robustesse** :
  - Vérifier la signature webhook (`Stripe-Signature`) + secret de webhook.
  - Idempotence (un event Stripe peut être reçu plusieurs fois) via stockage `stripeEventId`/`paymentIntentId`.
  - Gérer les statuts (paiement échoué, refund, chargeback) → retrait d’accès si nécessaire.
- **Gating des routes de lecture** :
  - À ajouter au niveau service (ex: `ChapterService.get(...)` / `getHtml(...)`) : vérifier que l’utilisateur a un entitlement valide avant de retourner le contenu.
  - Option “preview” : exposer un extrait public (si souhaité) tout en protégeant le reste.
- **Facture PDF** :
  - Générer une facture au moment de la confirmation webhook (ou à la demande) et la stocker (DB + fichier dans `app.upload.dir`).
  - Réutiliser **PDFBox** (déjà présent) pour produire un PDF simple (numéro de facture, date, client, items, total, devise).
  - Endpoint de récupération (ex: `GET /api/invoices/{id}/pdf` authentifié) avec contrôle d’accès (facture du user courant uniquement).

Configuration à prévoir (env) : `STRIPE_SECRET_KEY`, `STRIPE_WEBHOOK_SECRET`, `APP_FRONTEND_BASE_URL` (success/cancel URLs), et éventuellement `APP_PUBLIC_BASE_URL` si le backend est derrière un reverse proxy.

### API / DX

- **OpenAPI/Swagger** : exposer la doc des routes + modèles DTO (utile avec multipart + endpoints protégés).
- **Pagination/tri** : pour `GET /articles`, `GET /chapters`, `GET /comments` afin d’éviter de charger des listes complètes.
- **Versioning d’API** : `/api/v1/...` si l’API doit évoluer sans casser le front.

### Qualité / Ops

- **Tests** : augmenter la couverture (auth JWT, règles `@PreAuthorize`, uploads multipart, conversion PDF→HTML, listeners email) + tests d’intégration sur H2/Testcontainers.
