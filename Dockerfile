# Java 26 runtime image
FROM eclipse-temurin:26-jre

# Set the working directory
WORKDIR /app

# Copy the generated JAR into the container
COPY target/*.jar app.jar

# Start the application
ENTRYPOINT ["java", "-jar", "app.jar"]