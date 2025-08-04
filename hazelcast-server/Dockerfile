# Use an official OpenJDK runtime as a parent image
FROM openjdk:21-jdk-slim

# Set working directory inside container
WORKDIR /app

# Copy your fat jar to the container
COPY build/libs/hazelcast-server-1.0.0-all.jar hazelcast-server.jar

# Copy your Hazelcast XML config (update filename if needed)
COPY src/main/resources/hazelcast-server.xml hazelcast-server.xml

# Expose the default Hazelcast port and management center port (if used)
EXPOSE 5701 8080

# Run the Hazelcast server with the config
ENTRYPOINT ["java", "-Dhazelcast.config=hazelcast-server.xml", "-jar", "hazelcast-server.jar"]
