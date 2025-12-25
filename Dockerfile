FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app

# Specifying the exact filename avoids copying the broken 'plain' jar
COPY target/the-vault-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]