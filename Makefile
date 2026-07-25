dev-env-start:
	podman start springboot-db

dev:
	cd backend && mvn spring-boot:run

dev-client:
	cd frontend && ng serve