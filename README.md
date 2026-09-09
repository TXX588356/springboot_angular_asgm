# Meal Catalogue Planner

Meal Catalogue Planner is a full-stack Spring Boot and Angular application for managing a personal food catalogue and planning meals from saved food items. It is built as a practical CRUD-based meal planning system where users can register, log in, maintain food records, and create meal plans with calculated calorie totals.

The project demonstrates how an Angular frontend communicates with a Spring Boot REST API backed by PostgreSQL. It includes form validation, route navigation, authenticated API access, database persistence, derived queries, JPQL queries, and custom API error responses.

Each logged-in user has their own meal plans. Food catalogue records are shared as reusable food options, while meal plans are scoped to the authenticated user so one user cannot view, update, or delete another user's plans.

## Features

- User registration with username, email, and password.
- Email and password login.
- Server-side token sessions for authenticated requests.
- Logout with session invalidation.
- Food catalogue CRUD: create, view, update, and delete food items.
- Food search, category filtering, calorie filtering, and sorting.
- Meal plan CRUD: create, view, update, and delete meal plans.
- User-owned meal plans so each account sees only its own plans.
- Meal plan item selection from existing food records.
- Automatic meal-plan calorie total calculation.
- Angular reactive forms with validation messages.
- Angular route guards for protected pages.
- Spring Boot validation and custom error responses.
- PostgreSQL persistence through Spring Data JPA and Hibernate.

## Tech Stack

- Backend: Java 25, Spring Boot 4.1, Maven
- API: Spring Web, Bean Validation
- Persistence: Spring Data JPA, Hibernate, PostgreSQL
- Frontend: Angular 21, TypeScript, Tailwind CSS
- Database: PostgreSQL

![Java](https://img.shields.io/badge/Java-25-orange?logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.1-6DB33F?logo=springboot&logoColor=white)
![Angular](https://img.shields.io/badge/Angular-21-DD0031?logo=angular&logoColor=white)
![TypeScript](https://img.shields.io/badge/TypeScript-3178C6?logo=typescript&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-336791?logo=postgresql&logoColor=white)
![Tailwind CSS](https://img.shields.io/badge/Tailwind%20CSS-06B6D4?logo=tailwindcss&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-C71A36?logo=apachemaven&logoColor=white)

## Project Structure

```text
.
├── backend/                 # Spring Boot API
│   ├── src/main/java/       # Controllers, services, entities, repositories, DTOs
│   ├── src/main/resources/  # application.properties and seed data
│   └── pom.xml
├── frontend/                # Angular client
│   ├── src/app/             # Angular routes, components, services, models
│   ├── proxy.conf.json      # Proxies /api to the Spring Boot backend
│   └── package.json
└── Makefile                 # Common development commands
```

## Prerequisites

- Java 25
- Maven, or use the included `backend/mvnw`
- Node.js and npm
- Angular CLI, or run Angular scripts through npm
- PostgreSQL running locally, or Docker/Podman to run PostgreSQL from an image
- Optional: Podman, if you use the existing `make dev-env-start` workflow

## Database Configuration

The backend is configured in `backend/src/main/resources/application.properties`:

```properties
server.port=8085
spring.datasource.url=${DB_URL:jdbc:postgresql://localhost:5434/meal_planner}
spring.datasource.username=${DB_USER:postgres}
spring.datasource.password=${DB_PASSWORD:postgres}
spring.jpa.hibernate.ddl-auto=update
```

The default local database values are also shown in `.env.example`:

```env
DB_URL=jdbc:postgresql://localhost:5434/meal_planner
DB_USER=postgres
DB_PASSWORD=postgres
```

### Create the Local Database Container

If you use Podman, create the PostgreSQL container once with the `postgres:17` image:

```bash
podman run -d \
  --name springboot-db \
  -p 5434:5432 \
  -e POSTGRES_DB=meal_planner \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=postgres \
  -v springboot-db-data:/var/lib/postgresql/data \
  postgres:17
```

If you use Docker instead, run the same container with Docker:

```bash
docker run -d \
  --name springboot-db \
  -p 5434:5432 \
  -e POSTGRES_DB=meal_planner \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=postgres \
  -v springboot-db-data:/var/lib/postgresql/data \
  postgres:17
```

This creates a PostgreSQL database named `meal_planner` and exposes it at `localhost:5434`. The `springboot-db-data` volume keeps the database data, so restarting the container does not reset the database.

If you already have a Podman container named `springboot-db`, start it with:

```bash
make dev-env-start
```

This command only starts the existing Podman container. It does not create a fresh database each time.

## Running Locally

Start the backend:

```bash
make dev
```

Or run it directly:

```bash
cd backend
./mvnw spring-boot:run
```

The backend runs at:

```text
http://localhost:8085
```

Install frontend dependencies:

```bash
cd frontend
npm install
```

Start the Angular client:

```bash
npm start
```

Or from the repository root:

```bash
make dev-client
```

The Angular dev server proxies `/api` requests to `http://localhost:8085`.

## Testing

Run backend tests:

```bash
cd backend
./mvnw test
```

Run frontend tests:

```bash
cd frontend
npm test
```

Build the frontend:

```bash
cd frontend
npm run build
```

## API Overview

Base backend URL:

```text
http://localhost:8085
```

### Authentication

| Method | Endpoint | Description |
| --- | --- | --- |
| `POST` | `/api/auth/register` | Register a new user account with username, email, and password. |
| `POST` | `/api/auth/login` | Login with email and password. Returns the authenticated user and a session token. |
| `GET` | `/api/auth/me` | Get the currently authenticated user from the session token. |
| `POST` | `/api/auth/logout` | Logout and invalidate the current session token. |

Example registration request:

```json
{
  "username": "student1",
  "email": "student1@example.com",
  "password": "password123"
}
```

Example login request:

```json
{
  "email": "student1@example.com",
  "password": "password123"
}
```

Protected API requests should include the session token returned by login:

```text
Authorization: Bearer <sessionToken>
```

### Food Items

| Method | Endpoint | Description |
| --- | --- | --- |
| `GET` | `/api/foods` | List foods. Supports `name`, `category`, `maxCalories`, `sortBy`, and `direction` query parameters. |
| `GET` | `/api/foods/{id}` | Get one food item by ID. |
| `POST` | `/api/foods` | Create a food item. |
| `PUT` | `/api/foods/{id}` | Update a food item. |
| `DELETE` | `/api/foods/{id}` | Delete a food item. |
| `GET` | `/api/foods/search?name={name}` | Search foods by name. |
| `GET` | `/api/foods/category/{category}` | List foods by category. |
| `GET` | `/api/foods/filter` | Filter foods by optional `category` and `maxCalories`. |

Example food item request:

```json
{
  "name": "Chicken Rice",
  "category": "Main",
  "categoryCodes": ["main"],
  "calories": 620,
  "protein": 28.5,
  "carbohydrates": 72.0,
  "fat": 21.0,
  "servingSize": "1 plate",
  "price": 5.50
}
```

### Meal Plans

| Method | Endpoint | Description |
| --- | --- | --- |
| `GET` | `/api/meal-plans` | List meal plans. Supports `mealDate` and `mealType` query parameters. |
| `GET` | `/api/meal-plans/{id}` | Get one meal plan by ID. |
| `POST` | `/api/meal-plans` | Create a meal plan. |
| `PUT` | `/api/meal-plans/{id}` | Update a meal plan. |
| `DELETE` | `/api/meal-plans/{id}` | Delete a meal plan. |

Valid meal types:

```text
BREAKFAST, LUNCH, DINNER, SNACK
```

Example meal plan request:

```json
{
  "name": "Weekday Lunch",
  "mealDate": "2026-08-10",
  "mealType": "LUNCH",
  "notes": "Simple balanced meal",
  "items": [
    {
      "foodItemId": 1,
      "quantity": 1.0
    }
  ]
}
```

## Seed Data

Initial food catalogue data is stored in:

```text
backend/src/main/resources/data/food-items.json
```

The backend includes a `DataSeeder` component that loads food data into the database during application startup.
