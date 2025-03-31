FROM maven:3.9.5-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -Dspring.profiles.active=prod -Dmaven.test.skip=true

FROM openjdk:21-jdk
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
#COPY .env.prod /app/.env

ARG BUILD_PROFILE=prod
ENV SPRING_PROFILES_ACTIVE=$BUILD_PROFILE

ENTRYPOINT ["java", "-jar", "app.jar"]

EXPOSE 8080