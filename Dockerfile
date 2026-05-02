# Stage 1: Build the application
FROM maven:3.8.8-eclipse-temurin-17 AS builder
WORKDIR /build

# Copy only the pom.xml first to take advantage of Docker caching
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy the rest of the source code and build the JAR
COPY src ./src
RUN cp src/main/resources/application.properties.example src/main/resources/application.properties
RUN mvn clean package -DskipTests

# Stage 2: Create the runtime image
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copy the generated JAR from the builder stage
COPY --from=builder /build/target/*.jar app.jar

# Expose the Render default port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]