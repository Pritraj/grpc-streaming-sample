FROM gradle:8.5.0-jdk21 AS build
WORKDIR /home/gradle/project

# Copy proto definitions (needed for generation)
COPY --chown=gradle:gradle proto/ proto/

# Copy server code
COPY --chown=gradle:gradle server/ server/

# Switch to server directory to run gradle
WORKDIR /home/gradle/project/server
RUN gradle build --no-daemon

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /home/gradle/project/server/build/libs/grpc-job-server-1.0-SNAPSHOT.jar app.jar
EXPOSE 9090
ENTRYPOINT ["java", "-jar", "app.jar"]
