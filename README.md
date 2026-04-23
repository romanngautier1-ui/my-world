# My World — Fullstack

MyWorld is a fullstack web application (Spring Boot REST API + Angular frontend) built around **books**, **chapters**, and **articles**.

## Features (high level)

- Stateless **JWT auth** + email verification + password reset
- Content management: books/chapters/articles (automatic email notification to users when a new chapter or article is published)
- Engagement: comments + reading history
- Uploads: images + PDFs served via `/api/uploads/*`
- Chapters: plain text **or** uploaded PDF, rendered to HTML via PDF text extraction

## Stack

- Backend: Java 21, Spring Boot 4, Spring Security, JPA/Hibernate, MySQL (local profile), PDFBox, Maven
- Frontend: Angular 21, Bootstrap — API URL: `frontend/src/environments/environment.ts`

<p>
    <img src="https://s3.amazonaws.com/angularminds.com/new-blog-images/angular-wordmark-gradient.png" alt="Angular" height="38" />
    <img src=https://external-content.duckduckgo.com/iu/?u=https%3A%2F%2Ftrellat.es%2Fwp-content%2Fuploads%2Fspring-boot-logo.png&f=1&nofb=1&ipt=5b4200e95a784fc6d78e5dd4dce5232131d482e269b73954252c5f382b263f89 alt="Spring Boot" height="38" />
    <img src="https://external-content.duckduckgo.com/iu/?u=https%3A%2F%2Fmiro.medium.com%2Fv2%2Fresize%3Afit%3A797%2F1*8X26HYxkQ1YPkrW2oliKpw.png&f=1&nofb=1&ipt=3ce24bcc448816ce887cb0ed9a26f87f038cee5e37c97c7895f34defceb4d758" alt="Spring Security" height="38" />
    <img src="https://external-content.duckduckgo.com/iu/?u=https%3A%2F%2Fpngimg.com%2Fd%2Fmysql_PNG23.png&f=1&nofb=1&ipt=e2591dd4c8074fc52876265bd8a59415f70a72ef8ed82599383db9f78524b6ff" alt="MySQL" height="38" />
    <img src="https://external-content.duckduckgo.com/iu/?u=https%3A%2F%2Flogowik.com%2Fcontent%2Fuploads%2Fimages%2Fmaven-apache3537.jpg&f=1&nofb=1&ipt=3e27c589ced53fa913e0841209cee2b72129cf3edf184ba692c28fda5da1fa81" alt="Maven" height="38" />
    <img src="https://external-content.duckduckgo.com/iu/?u=https%3A%2F%2Fbrandlogos.net%2Fwp-content%2Fuploads%2F2021%2F09%2Fbootstrap-logo.png&f=1&nofb=1&ipt=dda5712c2bf0ddcffa3472a6aee27565337534d67217a8ea89ea1fcc953c9628" alt="Bootstrap" height="38" />
</p>

## Quick start

### Backend

```bash
cd backend/myworld
./mvnw spring-boot:run -Dspring-boot.run.profiles=local
```

API (local): `http://localhost:8080`

### Frontend

```bash
cd frontend
npm install
npm start
```

Frontend (local): `http://localhost:4200`

### Run with Docker (quick)

If you prefer to run the full stack with Docker (recommended for integration testing), use the repository root where `docker-compose.yml` lives:

```bash
docker-compose build
docker-compose up -d --build
```

- Frontend (container): `http://localhost:4200` (served by the frontend container)
- Backend (container): `http://localhost:8080`

### Docker Prerequisites (local)

- Ensure you have Docker Desktop (macOS/Windows) or Colima installed and running locally, otherwise, the build and up commands will fail.

## Documentation

- Backend API (routes, auth, uploads, PDF, profiles): [backend/myworld/README.md](backend/myworld/README.md)
- Frontend (Angular scripts): [frontend/README.md](frontend/README.md)

## App screenshots

Screenshots live in [Illustrations app](Illustrations%20app). File names match the feature (not every feature is represented).

<details>
<summary>Show screenshots</summary>

- Home

    ![Home](<Illustrations app/Accueil.png>)

- Register

    ![Register](<Illustrations app/S'enregistrer.png>)

- Login

    ![Login](<Illustrations app/Connexion.png>)

- Forgot password

    ![Forgot password](<Illustrations app/Mot de passe oublié.png>)

- User account

    ![User account](<Illustrations app/Compte utilisateur.png>)

- Books list

    ![Books list](<Illustrations app/Liste des livres.png>)

- Add a book

    ![Add a book](<Illustrations app/Ajouter un livre.png>)

- Chapters list

    ![Chapters list](<Illustrations app/Liste des chapitres.png>)

- Read chapter + download

    ![Read chapter + download](<Illustrations app/Lecture chapitre et téléchargement.png>)

- Comments area

    ![Comments area](<Illustrations app/Espace commentaires.png>)

- Add comment

    ![Add comment](<Illustrations app/Ajouter commentaire.png>)

- View my comment

    ![View my comment](<Illustrations app/Voir son commentaire.png>)

- Articles list

    ![Articles list](<Illustrations app/Liste des articles.png>)

- Read an article (1)

    ![Read an article (1)](<Illustrations app/Lecture d'un article (1).png>)

- Read an article (2)

    ![Read an article (2)](<Illustrations app/Lecture d'un article (2).png>)

- Contact

    ![Contact](<Illustrations app/Contact.png>)

- About

    ![About](<Illustrations app/A propos.png>)

</details>

## Roadmap (work in progress)

- Stripe payments to unlock a chapter or a full book + PDF invoice generation: see “Pistes d’amélioration” in [backend/myworld/README.md](backend/myworld/README.md)

## Repo structure

- `backend/`: Spring Boot backend
- `frontend/`: Angular frontend
- `Illustrations app/`: screenshots
