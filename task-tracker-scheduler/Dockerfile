FROM maven:3.8.6-eclipse-temurin-17 AS build
WORKDIR /
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jdk
WORKDIR /
COPY --from=build /target/*.jar task-tracker-scheduler.jar
ENTRYPOINT ["java", "-jar", "task-tracker-scheduler.jar"]