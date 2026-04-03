
# MyWorld — Backend

Spring Boot backend for the **MyWorld** project.

## Project overview

MyWorld is a REST API that powers a reading/writing application with:

- **Library**: managing **books** and **chapters**.
- **Editorial content**: managing **articles** (with an image).
- **Interaction**: **comments** on chapters and **reading history** (read chapters).
- **User accounts**: registration, **email verification**, **JWT** login, password reset.
- **Email notifications**: automatic emails when a new article/chapter is added (active users).
- **Uploads**: file storage (images + PDFs) on disk, served via `GET /api/uploads/{filename}`.
- **PDF service**: a chapter can be stored either as **raw text** or as a **URL to an uploaded PDF**; the backend can **extract text from the PDF** and render it as HTML.

## Stack

- Java **21**
- Spring Boot **4.0.3**
- Maven (wrapper: `./mvnw`)
- Persistence: Spring Data JPA
- Mapping: MapStruct + Lombok

## Prerequisites

- JDK 21 installed
- (Optional, `local` profile) local MySQL

## Run the project

### 1) Start without profile (config via environment variables)

The file `src/main/resources/application.properties` contains only **shared** configuration (no secrets).
To start without a profile, you must provide configuration through environment variables.

- Example (DB + JWT):

```bash
export SPRING_DATASOURCE_URL='jdbc:mysql://localhost:3306/myworld'
export SPRING_DATASOURCE_USERNAME='myworld'
export SPRING_DATASOURCE_PASSWORD='myworld'
export JWT_SECRET='change-me-please-change-me-please-32bytes+'
./mvnw spring-boot:run
```

### 2) `local` profile (MySQL + seed)

The `local` profile is meant to start with MySQL and initialize the database with an SQL script.

- Configuration: `src/main/resources/application-local.properties`
  - Contains `spring.datasource.*` + JPA config + enables seed `classpath:db/mysql/data.sql`

- Run with `local` profile:

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=local
```

You can override any property via env (e.g. `SPRING_DATASOURCE_URL`, `JWT_SECRET`, `APP_FRONTEND_BASE_URL`).

### 3) `prod` profile

The `prod` profile expects all configuration (DB/JWT/CORS/URLs) to be provided via environment variables or a secrets manager.

```bash
java -jar target/myworld-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```

Or (example) using the JAR with the `local` profile:

```bash
java -jar target/myworld-0.0.1-SNAPSHOT.jar --spring.profiles.active=local
```

## Tests

Tests use an in-memory H2 database via `src/test/resources/application.properties`.

- Run tests:

```bash
./mvnw test
```

## API routes

The routes below are derived from the Spring controllers (`src/main/java/com/app/myworld/controller`).
By default, Spring Security runs in **stateless** mode (JWT):

- **Public**: auth endpoints (`/api/auth/**`), `GET /api/uploads/**`, `GET /api/books` / `GET /api/books/{id}`, `GET /api/articles` / `GET /api/articles/{id}` / `GET /api/articles/{id}/neighbors`, mail endpoints (`/api/mails/**`).
- **Authenticated**: everything else.
- **ADMIN**: some routes are restricted via `@PreAuthorize("hasRole('ADMIN')")`.

### Auth (JWT) & account management

- `POST /api/auth/register` (public)
  - JSON body (`RegisterRequest`): `username`, `email`, `password`
  - Effect: creates the user (role `USER`, `isActive=false`), then triggers a verification email.
- `GET /api/auth/verify-email?token=...` (public)
  - Effect: activates the account (`isActive=true`).
- `POST /api/auth/login` (public)
  - JSON body (`LoginRequest`): `username`, `password`
  - Response (`AuthResponse`): `token`, `username`, `email`, `role`
- `POST /api/auth/reset-password` (public)
  - JSON body: **string** containing the email (e.g. `"user@example.com"`)
  - Effect: if the user exists, generates a reset token (30 min) and sends a link to the frontend.
- `POST /api/auth/reset-password-link` (public)
  - JSON body (`ResetPasswordRequest`): `token`, `newPassword`

Token usage:

- Header: `Authorization: Bearer <JWT>`
- The JWT includes `userId` and `tokenVersion`. The backend invalidates a token if `tokenVersion` no longer matches the value stored in the database.

User routes:

- `GET /api/users/me` (authenticated)
- `PATCH /api/users/me/change-password` (authenticated)
- `PATCH /api/users/me/update-current-user` (authenticated)
  - Effect: updates the user and returns a **new JWT** (rotation via `tokenVersion`).
- `DELETE /api/users/me` (ADMIN)

### Uploads (images + PDF)

The backend does not provide a “generic upload endpoint”. Uploads are handled through some `multipart/form-data` endpoints (create/update), and files are served through:

- `GET /api/uploads/{filename}` (public)

Storage & limits:

- Directory: `app.upload.dir` (default `${user.home}/myworld/uploads`)
- Max size: `10MB` per file (`spring.servlet.multipart.max-file-size`) and `12MB` per request.
- Supported extensions:
  - images: `jpg`, `jpeg`, `png`, `gif`, `webp`
  - PDF: `pdf`

Use cases:

- Articles/books: `imageFile` (upload) **or** `urlImage` (external URL)
- Chapters: `pdfFile` (upload) **or** `content` (raw text)

### PDF service (chapters)

A chapter can store its content either as a URL to an uploaded PDF (`.../api/uploads/<uuid>.pdf`) or as raw text.

- `GET /api/chapters/{id}/html` (authenticated)
  - Returns “safe” HTML in `<p>`:
    - if `content` points to an uploaded PDF, the backend extracts text using **PDFBox** and then formats it into paragraphs
    - otherwise, it formats the raw text into paragraphs
- `GET /api/chapters/{id}/pdf` (ADMIN)
  - Downloads the PDF linked to the chapter if the content is a PDF upload.

### Articles

- `GET /api/articles`
- `GET /api/articles/{id}`
- `GET /api/articles/{id}/neighbors`
- `POST /api/articles` (ADMIN) (multipart) (automatic email sent to notify users about a new article)
- `PATCH /api/articles/{id}` (ADMIN) (multipart)
- `DELETE /api/articles/{id}` (ADMIN)

### Books

- `GET /api/books`
- `GET /api/books/{id}`
- `POST /api/books` (ADMIN) (multipart)
- `PATCH /api/books/{id}` (ADMIN) (multipart)
- `DELETE /api/books/{id}` (ADMIN)

### Chapters

- `GET /api/chapters`
- `GET /api/chapters/{id}`
- `GET /api/chapters/{id}/html`
- `GET /api/chapters/{id}/pdf` (ADMIN)
- `GET /api/chapters/{id}/neighbors`
- `POST /api/chapters/{id}/likes/increment`
- `POST /api/chapters/{id}/likes/decrement`
- `POST /api/chapters` (ADMIN) (multipart) (automatic email sent to notify users about a new chapter)
- `PATCH /api/chapters/{id}` (ADMIN) (multipart)
- `DELETE /api/chapters/{id}` (ADMIN)

### Chapter reads (read chapters history)

- `GET /api/chapter-reads`
- `GET /api/chapter-reads/exists?userId=...&chapterId=...`
- `POST /api/chapter-reads`
- `DELETE /api/chapter-reads/{id}`

### Comments

- `GET /api/comments`
- `GET /api/comments/{id}`
- `GET /api/comments/exists?userId=...&chapterId=...`
- `POST /api/comments` (authenticated)
- `PATCH /api/comments/{id}` (authenticated)
- `DELETE /api/comments/{id}` (authenticated)

### Users

- `GET /api/users/me` (authenticated)
- `PATCH /api/users/me/change-password` (authenticated)
- `PATCH /api/users/me/update-current-user` (authenticated)
- `DELETE /api/users/me` (ADMIN)

### Mails

Endpoints currently **public** (see `SecurityConfig`):

- `POST /api/mails/send`
- `POST /api/mails/sendWithAttachment`
- `POST /api/mails/receive`

### Uploads

- `GET /api/uploads/{filename}` (public) — serves an image/PDF already stored on disk.

## Project structure

- `src/main/java/com/app/myworld/`
  - `controller/`: REST controllers (Auth, Article, Book, Chapter, ChapterRead, Comment, Email, Upload, User)
  - `service/`: business logic
  - `repository/`: Spring Data repositories
  - `model/`: JPA entities
  - `dto/`: DTOs (requests/responses)
  - `mapper/`: MapStruct mappers
  - `security/`: security (Spring Security)
  - `config/`: application configuration

- `src/main/resources/`
  - `application.properties`: default configuration
  - `application-local.properties`: `local` profile (MySQL + seed)
  - `db/mysql/data.sql`: seed data

## Notes

- MapStruct mapper implementations are generated in `target/generated-sources/annotations`.
- If your IDE reports Maven/Java config out of sync with `pom.xml`, reloading the Java/Maven workspace usually fixes it.

## Improvement ideas / feature backlog

These ideas are intentionally incremental and reuse patterns already present (services, DTOs, events, security).

### Security / Auth

- **Logout endpoint**: increment `User.tokenVersion` to invalidate the current JWT server-side (the mechanism already exists via `generateNewToken`).
- **Refresh tokens** (optional): short-lived access token + refresh token (rotation + revocation) instead of a long-lived JWT.
- **Secure /api/mails/**: mail endpoints are currently public (see `SecurityConfig`) → add auth + rate limiting + anti-abuse measures.
- **Consistent input validation**: standardize errors (single JSON format) via `@ControllerAdvice` (there is already a `GlobalExceptionHandler`).

### Uploads / Files

- **Orphaned file cleanup**: when deleting an article/book/chapter, delete its associated uploaded file (if the content points to `/api/uploads/...`).
- **Caching**: more aggressive `Cache-Control` for images (if filenames are immutable UUIDs), while keeping `no-cache` if desired for PDFs.

### PDF / Chapters

- **Richer preview**: store an “extracted text” (or HTML) version in the DB at create/update time, to avoid reprocessing the PDF on every `GET /html`.

### Payments (Stripe) + PDF invoice (work-in-progress)

Planned goal (not finalized yet): allow a user to **pay** to **read chapters** or **unlock a full book**, then generate a **PDF invoice**.

Backend implementation ideas:

- **Access model (entitlements)**:
  - `BOOK_UNLOCK` (unlocks a whole book) and/or `CHAPTER_UNLOCK` (unlocks a chapter).
  - Store a “proof of access” in the DB (e.g. `entitlements` table linked to `userId`, `bookId/chapterId`, `grantedAt`, `sourcePaymentId`).
- **Stripe Checkout (recommended to start)**:
  - Backend endpoint to create a Checkout session (e.g. `POST /api/billing/checkout` with `{ type, bookId?, chapterId? }`).
  - Frontend redirects to Stripe, then returns on success/cancel.
  - **Stripe webhooks** (e.g. `POST /api/billing/webhooks/stripe`) to validate payment server-side and grant access.
- **Security & robustness**:
  - Verify webhook signature (`Stripe-Signature`) + webhook secret.
  - Idempotency (a Stripe event can be received multiple times) by storing `stripeEventId`/`paymentIntentId`.
  - Handle statuses (failed payment, refund, chargeback) → revoke access if needed.
- **Gating reading routes**:
  - To add at the service layer (e.g. `ChapterService.get(...)` / `getHtml(...)`): verify the user has a valid entitlement before returning content.
  - Optional “preview”: expose a public excerpt (if desired) while protecting the rest.
- **PDF invoice**:
  - Generate an invoice when the webhook is confirmed (or on-demand) and store it (DB + file in `app.upload.dir`).
  - Reuse **PDFBox** (already present) to produce a simple PDF (invoice number, date, customer, items, total, currency).
  - Retrieval endpoint (e.g. `GET /api/invoices/{id}/pdf` authenticated) with access control (only the current user's invoices).

Configuration to plan for (env): `STRIPE_SECRET_KEY`, `STRIPE_WEBHOOK_SECRET`, `APP_FRONTEND_BASE_URL` (success/cancel URLs), and optionally `APP_PUBLIC_BASE_URL` if the backend sits behind a reverse proxy.

### API / DX

- **OpenAPI/Swagger**: expose routes documentation + DTO models (useful with multipart + protected endpoints).
- **Pagination/sorting**: for `GET /articles`, `GET /chapters`, `GET /comments` to avoid loading full lists.
- **API versioning**: `/api/v1/...` if the API needs to evolve without breaking the frontend.

### Quality / Ops

- **Tests**: increase coverage (JWT auth, `@PreAuthorize` rules, multipart uploads, PDF→HTML conversion, email listeners) + integration tests with H2/Testcontainers.
