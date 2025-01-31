# Dockerfile for Ride Booking Service Microservice
FROM openjdk:17
VOLUME /tmp
ARG JAR_FILE=target/ride-booking-service.jar
COPY ${JAR_FILE} ride-booking-service.jar
ENTRYPOINT ["java", "-jar", "/ride-booking-service.jar", "--spring.profiles.active=cloud"]
