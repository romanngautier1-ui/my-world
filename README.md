# My World — Fullstack

MyWorld est une application web fullstack (API Spring Boot + front Angular) autour de **livres**, **chapitres** et **articles**.

## Fonctionnalités (résumé)

- Auth **JWT stateless** + vérification email + reset password
- Gestion de contenu: livres/chapitres/articles (avec envoie de mail automatique aux utilisateurs lors de l'ajout d'un nouveau chapitre ou d'un nouvel article)
- Interaction: commentaires + historique de lecture
- Uploads: images + PDFs servis via `/api/uploads/*`
- Chapitres: texte brut **ou** PDF uploadé, rendu HTML via extraction PDF

## Stack

- Backend: Java 21, Spring Boot 4, Spring Security, JPA/Hibernate, MySQL (profil `local`), PDFBox
- Frontend: Angular 21 — API URL: `frontend/src/environments/environment.ts`

## Démarrage rapide

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

Front (local): `http://localhost:4200`

## Documentation

- API / backend (routes, auth, uploads, PDF, profils): [backend/myworld/README.md](backend/myworld/README.md)
- Frontend (Angular scripts): [frontend/README.md](frontend/README.md)

## Illustrations app

Captures d’écran: [Illustrations site](Illustrations%20site). Le nom du fichier correspond à la fonctionnalité (toutes les fonctionnalités ne sont pas représentées).

<details>
<summary>Voir les captures</summary>

- A propos

    ![A propos](<Illustrations app/A propos.png>)

- Accueil

    ![Accueil](<Illustrations app/Accueil.png>)

- Ajouter commentaire

    ![Ajouter commentaire](<Illustrations app/Ajouter commentaire.png>)

- Ajouter un livre

    ![Ajouter un livre](<Illustrations app/Ajouter un livre.png>)

- Compte utilisateur

    ![Compte utilisateur](<Illustrations app/Compte utilisateur.png>)

- Connexion

    ![Connexion](<Illustrations app/Connexion.png>)

- Contact

    ![Contact](<Illustrations app/Contact.png>)

- Editer un livre

    ![Editer un livre](<Illustrations app/Editer un  livre.png>)

- Espace commentaires

    ![Espace commentaires](<Illustrations app/Espace commentaires.png>)

- Lecture chapitre et téléchargement

    ![Lecture chapitre et téléchargement](<Illustrations app/Lecture chapitre et téléchargement.png>)

- Lecture d'un article (1)

    ![Lecture d'un article (1)](<Illustrations app/Lecture d'un article (1).png>)

- Lecture d'un article (2)

    ![Lecture d'un article (2)](<Illustrations app/Lecture d'un article (2).png>)

- Liste des articles

    ![Liste des articles](<Illustrations app/Liste des articles.png>)

- Liste des chapitres

    ![Liste des chapitres](<Illustrations app/Liste des chapitres.png>)

- Liste des livres

    ![Liste des livres](<Illustrations app/Liste des livres.png>)

- Mot de passe oublié

    ![Mot de passe oublié](<Illustrations app/Mot de passe oublié.png>)

- S'enregistrer

    ![S'enregistrer](<Illustrations app/S'enregistrer.png>)

- Voir son commentaire

    ![Voir son commentaire](<Illustrations site/Voir son commentaire.png>)
</details>

## Roadmap (en réflexion)

- Paiement Stripe pour débloquer un chapitre ou un livre + facture PDF: voir la section “Pistes d’amélioration” dans [backend/myworld/README.md](backend/myworld/README.md)

## Structure du repo

- `backend/` : backend Spring Boot
- `frontend/` : frontend Angular
- `Illustrations app/` : captures d’écran
