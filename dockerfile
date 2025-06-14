
FROM openjdk:17-slim
WORKDIR /app
COPY target/BlogService-0.0.1-SNAPSHOT.jar /app/blog-service.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/blog-service.jar"]
