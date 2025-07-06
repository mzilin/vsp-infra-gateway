# Use a slim, secure Java 21 base image
FROM eclipse-temurin:21-jdk-alpine

# Create a non-root user and group
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# Set working directory
WORKDIR /app

# Copy the built JAR file
COPY build/libs/vsp-infra-gateway-*.jar app.jar

# Ensure correct ownership
RUN chown appuser:appgroup app.jar

# Switch to the non-root user
USER appuser

# Expose the application port
EXPOSE 8080

# Start the application
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]