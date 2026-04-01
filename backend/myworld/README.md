# MyWorld — Backend

Backend Spring Boot du projet **MyWorld**.

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
  - Contient `spring.datasource.*` + config JPA + seed `classpath:db/mysql/seed.sql`

- Run avec profil `local`:

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=local
```

Tu peux surcharger n'importe quel paramètre via env (ex: `SPRING_DATASOURCE_URL`, `JWT_SECRET`, `APP_FRONTEND_BASE_URL`).

### 3) Désactiver la sécurité (profil `no-security`)

Utile pour tester les routes API sans authentification.

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=no-security
```

Tu peux aussi combiner des profils (ex: MySQL + no auth) :

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=local,no-security
```

### 4) Profil `prod`

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
Selon la configuration Spring Security globale, certaines routes peuvent être protégées en plus des `@PreAuthorize`.

### Articles

- `GET /api/articles`
- `GET /api/articles/{id}`
- `POST /api/articles` (ADMIN)
- `PATCH /api/articles/{id}` (ADMIN)
- `DELETE /api/articles/{id}` (ADMIN)

### Livres

- `GET /api/books`
- `GET /api/books/{id}`
- `POST /api/books` (ADMIN)
- `PATCH /api/books/{id}` (ADMIN)
- `DELETE /api/books/{id}` (ADMIN)

### Chapitres

- `GET /api/chapters`
- `GET /api/chapters/{id}`
- `POST /api/chapters` (ADMIN)
- `PATCH /api/chapters/{id}` (ADMIN)
- `DELETE /api/chapters/{id}` (ADMIN)

### Lecture de chapitres

- `GET /api/chapter-reads`
- `POST /api/chapter-reads`
- `DELETE /api/chapter-reads/{id}`

### Commentaires

- `GET /api/comments`
- `GET /api/comments/{id}`
- `POST /api/comments` (authentifié)
- `PATCH /api/comments/{id}` (authentifié)
- `DELETE /api/comments/{id}` (authentifié)

### Utilisateurs

- `GET /api/users/{id}`
- `POST /api/users`
- `PATCH /api/users/{id}`
- `DELETE /api/users/{id}` (ADMIN)

### Messages

- `MessageController` est présent mais ne définit pas encore de routes.

## Structure du projet

- `src/main/java/com/app/myworld/`
  - `controller/` : contrôleurs REST (Article, Book, Chapter, ChapterRead, Comment, Message, User)
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
  - `db/mysql/seed.sql` : données de seed

## Notes

- Les implémentations de mappers MapStruct sont générées dans `target/generated-sources/annotations`.
- Si ton IDE indique que la configuration Maven/Java n’est pas à jour avec le `pom.xml`, un rechargement du workspace Java/Maven règle généralement le problème.
