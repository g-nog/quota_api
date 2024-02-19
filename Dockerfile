# Start with a base image containing Java runtime
FROM amazoncorretto:21.0.2-alpine3.19 as build

# Set the current working directory inside the image
WORKDIR /app

# Copy gradle.war file to the container
COPY ./gradlew .
COPY ./gradle gradle
COPY ./build.gradle.kts .
COPY ./settings.gradle.kts .

# Grant permissions for the gradlew file to execute
RUN chmod +x ./gradlew

# Copy the rest of the application code
COPY ./src src

# Build the application
RUN ./gradlew build

# Start a new stage from scratch
FROM amazoncorretto:21.0.2-alpine3.19

EXPOSE 8080

# Set the application's jar file
ARG JAR_FILE=/app/build/libs/*.jar

# Copy the application's jar file from the build stage
COPY --from=build ${JAR_FILE} app.jar

# Run the jar file
ENTRYPOINT ["java","-jar","/app.jar"]