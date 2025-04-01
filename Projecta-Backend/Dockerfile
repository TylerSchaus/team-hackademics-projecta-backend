# ---------- Stage 1: Build the app ----------
    FROM maven:3.9.4-eclipse-temurin-21 AS build
    WORKDIR /build
    COPY pom.xml .
    COPY src ./src
    RUN mvn clean package -DskipTests
    
    # ---------- Stage 2: Run the app ----------
    FROM openjdk:21-jdk-slim
    WORKDIR /app
    COPY --from=build /build/target/*.jar app.jar
    EXPOSE 8080
    ENTRYPOINT ["java", "-jar", "app.jar"]
    