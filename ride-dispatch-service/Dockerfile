# Dockerfile for Dispatch Service Microservice
FROM openjdk:17
VOLUME /tmp
ARG JAR_FILE=target/ride-dispatch-service.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java", "-jar", "/ride-dispatch-service.jar", "-Dspring.profiles.active=cloud"]
