# Meal Catalogue Planner

Meal Catalogue Planner is a Spring Boot and Angular application for managing food items and building meal plans from them. The backend exposes REST APIs for food catalogue CRUD, filtering, searching, and meal plan management. The frontend consumes those APIs through an Angular development proxy.

## Tech Stack

- Backend: Java 25, Spring Boot 4.1, Maven
- API: Spring Web, Bean Validation
- Persistence: Spring Data JPA, Hibernate, PostgreSQL
- Frontend: Angular 21, TypeScript, Tailwind CSS
- Database: PostgreSQL

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
- PostgreSQL running locally
- Optional: Podman, if you use the existing `make dev-env-start` workflow

## Database Configuration

The backend is configured in `backend/src/main/resources/application.properties`:

```properties
server.port=8085
spring.datasource.url=jdbc:postgresql://localhost:5434/meal_planner
spring.datasource.username=postgres
spring.datasource.password=postgres
spring.jpa.hibernate.ddl-auto=update
```

Create a PostgreSQL database named `meal_planner` and make sure it is reachable on port `5434` with the configured username and password.

If you already have a Podman container named `springboot-db`, start it with:

```bash
make dev-env-start
```

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
