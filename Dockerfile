# 1. Use OpenJDK as base image
FROM openjdk:21-jdk-slim

# 2. Add a label (optional)
LABEL maintainer="your-email@example.com"

# 3. Set working directory
WORKDIR /app

# 4. Copy the built jar into container
COPY target/accounting-1.0.0.jar app.jar

# 5. Expose port (same as your Spring Boot runs, usually 8080)
EXPOSE 8080

# 6. Run the app
ENTRYPOINT ["java", "-jar", "app.jar"]
