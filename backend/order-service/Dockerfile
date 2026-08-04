FROM gradle:8.10.2-jdk21 AS builder

WORKDIR /app

COPY . .

RUN ./gradlew :order-service:build -x test

FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=builder /app/order-service/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]