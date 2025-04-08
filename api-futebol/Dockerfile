# Etapa 1 - Build com Maven
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .
RUN mvn package -DskipTests

# Etapa 2 - Runtime com JDK leve
FROM eclipse-temurin:21-jdk-jammy
WORKDIR /work/
COPY --from=build /app/target/quarkus-app/lib/ ./lib/
COPY --from=build /app/target/quarkus-app/*.jar ./
COPY --from=build /app/target/quarkus-app/app/ ./app/
COPY --from=build /app/target/quarkus-app/quarkus/ ./quarkus/
EXPOSE 8080
CMD ["java", "-jar", "quarkus-run.jar"]
