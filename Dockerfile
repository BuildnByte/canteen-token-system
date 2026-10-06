FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copy the built jar file into the container
COPY target/canteen-token-system-0.0.1-SNAPSHOT.jar app.jar

# Expose the port the app runs on
EXPOSE 8082

# Command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
