FROM openjdk:17-jdk-slim

LABEL maintainer="Sun Life Financial"
LABEL description="Caching demo app using Redis, Memcached, Hazelcast, and MySQL"

# Create app directory
WORKDIR /app

# Directory for Hazelcast persistence
RUN mkdir -p /app/hazelcast-data

# Copy the jar (from Gradle build output)
COPY build/libs/*.jar app.jar

# Expose Spring Boot default port
EXPOSE 8080

# Optional: Health check for actuator
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# Run the app
ENTRYPOINT ["java", "-jar", "app.jar"]
