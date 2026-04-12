# MusicReview Application

MusicReview is a Spring Boot web application for browsing artists and albums, writing album reviews, and managing user profiles.
It includes a server-rendered UI (Thymeleaf) and basic REST endpoints for albums, artists, and reviews.

## Features

- User registration and login
- Role-based access control (`USER` and `ADMIN`)
- Artist and album browsing with detail pages
- Review creation, editing, and deletion
- Ownership checks: users can manage only their own reviews (admins can manage all)
- User profile page with bio, favorite genre, and profile image upload
- Genre-based grouping in artist/album lists
- Internationalization support (`messages.properties`, `messages_en.properties`, `messages_fi.properties`)
- REST API endpoints under `/api/*`

## Tech Stack

- Java 17
- Spring Boot 4.0.5
- Spring MVC + Thymeleaf
- Spring Security
- Spring Data JPA
- H2 (local default)
- PostgreSQL (Rahti profile)
- Maven
- Docker (multi-stage build)

## Project Structure

- `src/main/java/com/example/musicreview/controller` - MVC controllers
- `src/main/java/com/example/musicreview/controller/api` - REST controllers
- `src/main/java/com/example/musicreview/service` - business logic
- `src/main/java/com/example/musicreview/repository` - JPA repositories
- `src/main/java/com/example/musicreview/model` - JPA entities
- `src/main/resources/templates` - Thymeleaf templates
- `src/main/resources/static` - static assets
- `src/main/resources/application.properties` - local config (H2)
- `src/main/resources/application-rahti.properties` - Rahti/PostgreSQL config

## Security and Roles

### Public routes

- `/login`
- `/register`
- Static resources (`/css/**`, `/images/**`, `/uploads/**`)
- Read-only pages like albums/artists/reviews listing and details

### Authenticated user routes

- Create/edit/delete own reviews
- Edit own profile (`/users/{id}/edit`)

### Admin-only routes

- Create/edit/delete artists
- Create/edit/delete albums

### Default admin bootstrap

On startup, the app ensures an `admin` user exists.
The user is created in code with a pre-hashed password in `UserService.ensureAdminUserExists()`.
If needed, change credentials directly in the database or update the initialization logic.

## Local Development

### Prerequisites

- Java 17
- Maven (or use `mvnw`/`mvnw.cmd`)

### Run the app

Windows (PowerShell):

```powershell
.\mvnw.cmd spring-boot:run
```

macOS/Linux:

```bash
./mvnw spring-boot:run
```

Default URL:

- `http://localhost:8080`

The default local profile uses file-based H2:

- `jdbc:h2:file:${H2_DB_PATH:~/musicreviewdb};AUTO_SERVER=TRUE`

## Testing and Build

Run tests:

```bash
./mvnw test
```

Build jar:

```bash
./mvnw clean package
```

## Configuration

`application.properties` imports optional `.env` values:

- `spring.config.import=optional:file:.env[.properties]`

Useful environment variables:

- `H2_DB_PATH` - local H2 database file path
- `APP_UPLOAD_DIR` - directory for uploaded profile images (default `./uploads` locally)
- `SPRING_PROFILES_ACTIVE` - active profile (Docker image defaults to `rahti`)

### Rahti / PostgreSQL profile

When profile `rahti` is active, PostgreSQL settings are resolved from environment variables.
The app supports these patterns:

- `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD`
- or `DB_JDBC_URL` + `DB_USERNAME` + `DB_PASSWORD`
- or host-based variables: `DB_HOST`, `DB_PORT`, `DB_NAME` (with `DB_USERNAME` / `DB_PASSWORD`)

## Docker

Build image:

```bash
docker build -t musicreview .
```

Run container:

```bash
docker run --rm -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=rahti \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://<host>:5432/<db> \
  -e SPRING_DATASOURCE_USERNAME=<user> \
  -e SPRING_DATASOURCE_PASSWORD=<password> \
  musicreview
```

## REST API

- `GET /api/albums` - list albums
- `GET /api/albums/{id}` - album details
- `GET /api/artists` - list artists
- `GET /api/artists/{id}` - artist details
- `GET /api/reviews` - list reviews

## Notes

- Uploaded profile images are served from `/uploads/**` and stored on disk (`APP_UPLOAD_DIR`).
- Supported upload extensions: `jpg`, `jpeg`, `png`, `gif`, `webp`.
- Localized messages are available in English and Finnish.
