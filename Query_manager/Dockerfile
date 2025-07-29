# Use official JDK image as base
FROM eclipse-temurin:17-jdk

# Set environment variable to reduce Spring log noise in Docker
ENV JAVA_OPTS=""

# Set working directory
WORKDIR /app

# Copy built JAR from target folder to /app
COPY target/attendance-downloader-0.0.1-SNAPSHOT.jar app.jar

# Expose port (default Spring Boot port)
EXPOSE 8080

# Run the JAR file
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
