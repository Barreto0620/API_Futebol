FROM eclipse-temurin:21-jdk-jammy as build

WORKDIR /app
COPY . .

RUN ./mvnw package -DskipTests

FROM eclipse-temurin:21-jdk-jammy
WORKDIR /work/

COPY --from=build /app/target/quarkus-app/lib/ ./lib/
COPY --from=build /app/target/quarkus-app/*.jar ./
COPY --from=build /app/target/quarkus-app/app/ ./app/
COPY --from=build /app/target/quarkus-app/quarkus/ ./quarkus/

EXPOSE 8080
CMD ["java", "-jar", "app.jar"]
