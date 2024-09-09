FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app
COPY ../pom.xml .
COPY ../.mvn .mvn
COPY ../mv* ./
RUN mvn dependency:go-offline
COPY ../src ./src
RUN mvn clean package -DskipTests
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app/target/*.jar /app/app.jar
CMD ["java", "-jar", "/app/app.jar"]
