# Stage 1: Build the application (using Java 21)
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
# Copy pom and source
COPY pom.xml .
COPY src ./src
# Build the jar inside Docker (skipping local Java version issues)
RUN mvn clean package -DskipTests

# Stage 2: Run the application
FROM eclipse-temurin:21-jre
WORKDIR /app
# Copy the built jar from the first stage
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]