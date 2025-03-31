FROM maven:3.8.6-eclipse-temurin-17 AS maven
WORKDIR /
COPY pom.xml .
COPY src ./src
RUN --mount=type=cache,target=/root/.m2 mvn -f pom.xml clean package -DskipTests

FROM eclipse-temurin:17-jre AS builder
WORKDIR /app
COPY --from=maven /target/*.jar eureka-server.jar
RUN java -Djarmode=layertools -jar eureka-server.jar extract

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=builder /app/dependencies/ ./
COPY --from=builder /app/spring-boot-loader/ ./
COPY --from=builder /app/snapshot-dependencies/ ./
COPY --from=builder /app/application/ ./
EXPOSE 8761
ENTRYPOINT ["java","-XX:+UseParallelGC","-XX:MaxRAMPercentage=75","org.springframework.boot.loader.launch.JarLauncher"]