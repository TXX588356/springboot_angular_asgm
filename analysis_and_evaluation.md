# 1. Introduction
The Meal Catalogue Planner developed for this assignment contains several implementation concepts that are relevant to my final year project, an AI-powered meal recommendation system. Although the assignment uses Angular and Spring Boot while the FYP uses React and Go, the underlying software design concepts are transferable. In particular, this assignment provided practical experience in structuring REST APIs, separating frontend and backend responsibilities, handling user input, managing food-related data, and connecting user interfaces to backend business logic. 

The assignment therefore serves as a smaller implementation of several technical problems that also appear in the FYP. The technologies themselves may change, but decisions concerning API design, component separation, validation, data flow, and layered architecture can be applied to the FYP.

# 2. Relevance of the Spring Boot Backend
## 2.1 REST API Design and Business Logic
The backend uses resource-oriented endpoint names such as `/api/foods` and `/api/meal-plans`. CRUD operations are represented using HTTP methods such as GET, POST, PUT, and DELETE instead of creating action-oriented URLs such as `/createFood` or `/deleteFood`. 
For example, `GET /api/foods/{id}` retrieves a particular food, while `PUT /api/foods/{id}` updates the food. 
This makes the API predictable and allows the URL to represent the business resource while the HTTP method represents the required operation. This design follows RESTful principles, which improves consistency and makes the API easier to understand and maintain. It also allows clients to interact with different resources in a uniform way without needing to learn custom endpoint patterns for each operation. In addition, it improves scalability and extensibility, since new features can often be added by introducing new resources or HTTP methods without changing the overall API structure.

Optional query parameters are also used for operations such as food searching and filtering. `/api/foods` accepts `name`, `category`, `maxCalories`, `sortBy` and `direction`. This is suitable because these values refine the collection being requested rather than representing separate resources. The backend service can combine these parameters through a JPA `Specification`, allowing a user to search, filter, and sort at the same time. 
This approach is directly relevant to my FYP. The recommendation and meal-logging interfaces also require retrieval of meals according to properties such as meal category, nutrition, and price. Similar query parameters can therefore be used when implementing meal filtering. The approach could later be extended with FYP-specific parameters such as dietary preference or budget constraints without redesigning the basic endpoint structure.

## 2.2 Layered Architecture, DTOs and Validation
The Spring Boot implementation separates controllers, services, and repositories. Controllers handle HTTP requests, services contain application logic, and repositories handle database operations. For example, `FoodItemController` delegates processing to `FoodItemService`, while `FoodItemService` uses `FoodItemRepository` for persistence and queries. 
This is highly suitable for the FYP. An Echo handler can perform the same responsibility as a Spring controller, while Go service and repository layers can perform the same roles as the Spring service and JPA repository. This separation is particularly important in the FYP because recommendation logic, database operations, and external AI/API calls should not be mixed directly into the HTTP handlers.

Request DTOs provide another useful design. `FoodRequest` validates fields such as name, category, and nutritional values before the data enters the service layer, while `MealPlanRequest` requires a date, meal type, and at least one food item. This demonstrates that backend validation remains necessary even when the frontend already validates input, because API requests can be sent without using the frontend. 

The meal-plan service also calculates total calories from its individual food items rather than trusting a total supplied by the client. This is suitable for the FYP because nutrition calculations and other important business rules should be performed or validated by the backend so that results remain consistent.

# 3. Relevance of the Angular Frontend
## 3.1 Components and Routing
The Angular application separates major functions into routes such as `/foods`, `/foods/new`, `/foods/:id`, `/meal-plans`, and `/meal-plans/:id`. A nested `/foods/:id/nutrition` route is also provided for detailed nutritional information. Authentication guards protect dashboard, food, and meal-plan routes, while login and registration routes remain publicly accessible.

This structure maps well to the FYP frontend. For example, food detail routes can correspond to meal or recommendation detail pages, while protected routes can be used for meal logs, recommendations, profile settings, and budget information. Although React Router will be used instead of Angular Router, the same route-oriented organisation can be retained. 

The food catalogue also demonstrates component reuse. `FoodList` renders multiple `FoodCard` components using `@for`. Each card receives a `FoodItem` through `@Input`, while deletion is communicated back to the parent using `@Output`. This separates the responsibility of displaying an individual food from managing the whole collection. A similar structure can be applied in React by passing recommendation or meal objects to reusable card components through props and handling actions through callback functions.

## 3.2 Bindings, Directives and Reactive Forms
Two-way `[(ngModel)]` binding is used for catalogue filters such as search text, category, maximum calories and sorting. This is appropriate because these controls represent simple temporary UI state. Event binding then calls `applyQuery()` when the user chooses to apply the filters. Angular's `@if` and `@for` directives are used to display loading, error, empty and populated states.

More structured data-entry pages use reactive forms instead. The food form defines validators for required fields, lengths, and numeric ranges and groups protein, carbohydrates, and fat into a nested nutrition group. The template displays validation messages and prevents submission when the form is invalid. This design is appropriate for the FYP's forms because data such as meal details, preferences and budget values also require predictable validation.

## 3.3 HTTP Communication and End-User Interaction
HTTP requests are placed inside `FoodService`, `MealPlanService` and `AuthService` rather than being implemented separately in every component. Components therefore concentrate on presentation state, navigation and user interaction while services manage communication with `/api` endpoints.

This separation is suitable for the FYP because many different pages may need to access the same backend resources. Centralising API calls reduces duplication and makes endpoint changes easier to maintain.

From the user's perspective, these technical decisions create a continuous workflow: the user navigates to an appropriate page, enters or filters information, the component validates the input, a service sends the HTTP request, the backend performs the business logic and database operation, and the updated result is finally displayed to the user. The assignment demonstrates the complete frontend-to-backend interaction that will also be required when users request AI meal recommendations, inspect meal information or record their meals in the FYP project.

# 4. Overall Evaluation
Overall, the Angular and Spring Boot implementation is relevant to the final FYP primarily because it provides reusable **design patterns rather than reusable framework-specific code**. RESTful resource design, layered backend architecture, request validation, user-specific data access, reusable frontend components, route-based navigation and centralised HTTP services can all be reproduced using React, Go and Echo.

Overall, this project provides a useful smaller-scale implementation of several architectural and interaction patterns that can support the more complex AI recommendation, nutrition, meal-logging and personalisation requirements of the final FYP.
