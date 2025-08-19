FROM eclipse-temurin:21-jre
LABEL authors="tanvirhassan"
EXPOSE 8082
COPY build/libs/service-registry-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
