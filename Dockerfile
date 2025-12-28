# --- STAGE 1: BUILD THE CODE ---
# Use a Docker image that has Maven pre-installed
FROM maven:3.9.9-eclipse-temurin-21-alpine AS build
WORKDIR /app

# Copy your source code to the container
COPY . .

# Run Maven to compile the code and package it into a Jar
# (We skip tests to speed up deployment)
RUN mvn clean package -DskipTests

# --- STAGE 2: RUN THE APP ---
# Use a lightweight Java Runtime (JRE)
FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app

# Copy the Jar file FROM the "build" stage above
COPY --from=build /app/target/*.jar app.jar

# Run it
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]