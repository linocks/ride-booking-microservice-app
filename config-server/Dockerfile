# Dockerfile for Dispatch Service Microservice
FROM openjdk:17
VOLUME /tmp
ARG JAR_FILE=target/config-server.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java", "-jar", "/config-server.jar"]
