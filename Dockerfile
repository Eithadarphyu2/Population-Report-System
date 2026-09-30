# Base runtime image using Java 21 LTS / Java 26 compatible OpenJDK JRE
FROM eclipse-temurin:21-jre-alpine

# Set the working directory inside the container
WORKDIR /app

# Copy any generated JAR file from target folder to app.jar inside container
COPY target/*.jar app.jar

# Entry point command to execute the application JAR
ENTRYPOINT ["java", "-jar", "app.jar"]