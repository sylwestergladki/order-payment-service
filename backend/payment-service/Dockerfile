FROM gradle:8.10.2-jdk21 AS builder

WORKDIR /app

COPY . .

RUN ./gradlew :payment-service:build -x test

FROM eclipse-temurin:21-jre

WORKDIR /app

RUN useradd --system --create-home appuser

COPY --from=builder /app/payment-service/build/libs/*.jar app.jar

RUN chown appuser:appuser app.jar

USER appuser

EXPOSE 8082

ENTRYPOINT ["java", "-jar", "app.jar"]