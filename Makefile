dev-env-start:
	podman start springboot-db

dev:
	cd backend && mvn spring-boot:run

dev-client:
	cd frontend && ng serve


DB_NAME=meal-planner
DB_USER=postgres
DB_HOST=localhost