# --- Stage 1: Build the Application ---
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Copy pom.xml and source code
COPY pom.xml .
COPY src ./src

# Build the JAR file skipping tests for speed
RUN mvn clean package -DskipTests

# --- Stage 2: Create the Runtime Image ---
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copy the built JAR from the builder stage
COPY --from=build /app/target/taskflow-0.0.1-SNAPSHOT.jar app.jar

# Expose the application port
EXPOSE 8080

# Configure environment variables defaults
ENV DB_HOST=db
ENV DB_PORT=5432
ENV DB_NAME=taskflow_db
ENV DB_USER=taskflow_user
ENV DB_PASSWORD=taskflow_password

# Run the jar file
ENTRYPOINT ["java", "-jar", "app.jar"]
