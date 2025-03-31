FROM openjdk:21-jdk

COPY target/*.jar app.jar
COPY .env ./.env

ARG BUILD_PROFILE=prod
ENV SPRING_PROFILES_ACTIVE=$BUILD_PROFILE

ENTRYPOINT ["java", "-jar", "/app.jar"]

EXPOSE 8080