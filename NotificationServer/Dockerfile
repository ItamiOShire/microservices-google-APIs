FROM openjdk:21-ea-1-jdk-slim
LABEL authors="adam"

WORKDIR /app
COPY target/NotificationServer-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]