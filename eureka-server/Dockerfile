# Dockerfile for Dispatch Service Microservice
FROM openjdk:17
VOLUME /tmp
ARG JAR_FILE=target/eureka-server.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java", "-jar", "/eureka-server.jar"]
