# Java 21 runtime image
FROM eclipse-temurin:21-jre

# Set the working directory
WORKDIR /app

# Copy the generated JAR into the container
COPY target/*-jar-with-dependencies.jar app.jar

# Start the application
ENTRYPOINT ["java", "-jar", "app.jar"]