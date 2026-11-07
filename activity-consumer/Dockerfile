# Stage 1: Build Spring Boot application
FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn -B clean package -DskipTests

# Stage 2: Run application
FROM eclipse-temurin:21-jre

WORKDIR /app

RUN groupadd --system appgroup && \
    useradd --system --uid 10001 --gid appgroup appuser

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8092

USER appuser

ENTRYPOINT ["java", "-jar", "app.jar"]
