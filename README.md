# My World

Ce projet est une application web complète composée d'un backend (API Java Spring Boot) et d'un frontend (application Angular). Elle permet de gérer et visualiser des contenus variés, avec une interface utilisateur moderne et sécurisée.

## Présentation du projet

- **Backend** : API REST développée avec Spring Boot, gérant la logique métier, la sécurité, la persistance des données et l'exposition des endpoints.
- **Frontend** : Application Angular offrant une interface utilisateur réactive, connectée à l'API pour l'affichage et la gestion des données.

## Technologies utilisées

### Backend
- **Java 17+**
- **Spring Boot** (framework principal)
- **Maven** (gestionnaire de dépendances)
- **Base de données** : MySQL (modifiable selon configuration)
- **Spring Security** (authentification/autorisation)
- **JPA/Hibernate** (ORM)

### Frontend
- **Angular** (framework principal)
- **TypeScript**
- **RxJS** (programmation réactive)
- **Angular CLI** (outils de build et de développement)
- **CSS3** / **HTML5**

## Installation et lancement

### Prérequis
- **Node.js** (v16+ recommandé)
- **npm** ou **yarn**
- **Java 17+**
- **Maven**
- **MySQL** (ou autre base compatible)

### 1. Cloner le dépôt
```bash
git clone <url-du-repo>
cd my-world
```

### 2. Configuration de la base de données
- Modifier les fichiers `application.properties` dans `backend/myworld/src/main/resources/` selon vos paramètres MySQL (utilisateur, mot de passe, nom de la base) et selon vos paramètres d'email (email et mot de passe).

### 3. Lancer le backend
```bash
cd backend/myworld
mvn clean install
./mvnw spring-boot:run -Dspring-boot.run.profiles=local (démarrage sur la configuration local)
```
L'API sera disponible sur `http://localhost:8080` par défaut.

### 4. Lancer le frontend
```bash
cd frontend
npm install
ng serve
```
L'application sera accessible sur `http://localhost:4200`.

## Structure du projet

- `backend/` : code source du backend Spring Boot
- `frontend/` : code source du frontend Angular

## Scripts utiles

- **Backend**
    - `mvn clean install` : build du backend
    - `mvn spring-boot:run` : lancement du serveur API
- **Frontend**
    - `npm install` : installation des dépendances
    - `ng serve` : lancement du serveur de développement

## Contribution

Les contributions sont les bienvenues !

1. Forkez le projet
2. Créez une branche (`git checkout -b feature/ma-feature`)
3. Commitez vos modifications (`git commit -am 'Ajout d'une feature'`)
4. Poussez la branche (`git push origin feature/ma-feature`)
5. Ouvrez une Pull Request

## Licence

Ce projet est sous licence MIT.
