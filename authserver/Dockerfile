FROM eclipse-temurin:21-jre
LABEL authors="tanvirhassan"
COPY build/libs/authserver-0.1-SNAPSHOT.jar auth.jar
EXPOSE 8090
ENTRYPOINT ["java", "-jar", "/auth.jar"]
