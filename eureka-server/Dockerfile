FROM maven:3.8.6-eclipse-temurin-17 AS build
WORKDIR /
COPY pom.xml .
COPY src ./src
RUN --mount=type=cache,target=/root/.m2 mvn -f pom.xml clean package -DskipTests

FROM eclipse-temurin:17-jdk
WORKDIR /
COPY --from=build /target/*.jar eureka-server.jar
EXPOSE 8761
ENTRYPOINT ["java", "-jar", "eureka-server.jar"]