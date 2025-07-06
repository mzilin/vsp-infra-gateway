# Use a slim, secure Java 21 base image
FROM eclipse-temurin:21-jdk-alpine

# Use non-root user for security
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# Set working directory
WORKDIR /app

# Copy built jar
COPY build/libs/vsp-infra-gateway-*.jar app.jar

# Change ownership of the JAR
RUN chown appuser:appgroup app.jar

# Switch to non-root user
USER appuser

# Expose the port
EXPOSE 8080

# Use exec form of ENTRYPOINT for signal handling
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]