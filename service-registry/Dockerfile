# Use OpenJDK 21 as the base image (official, secure)
FROM eclipse-temurin:21-jre

# Set the working directory
WORKDIR /app

# Copy the JAR into the image
COPY build/libs/service-registry-0.0.1-SNAPSHOT.jar app.jar

# Expose port 8080 so Docker knows this is where the app runs
EXPOSE 8082

# Command to run your app
ENTRYPOINT ["java", "-jar", "app.jar"]
