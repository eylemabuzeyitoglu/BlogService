
FROM openjdk:17-slim
WORKDIR /app
COPY target/BlogService-0.0.1-SNAPSHOT.jar /app/BlogService-0.0.1-SNAPSHOT.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/BlogService-0.0.1-SNAPSHOT.jar"]
