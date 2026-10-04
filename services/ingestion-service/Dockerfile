FROM maven:3.9.11-eclipse-temurin-17 AS build
WORKDIR /workspace
COPY . .
RUN mvn -B -pl services/ingestion-service -am package -Dmaven.test.skip=true
FROM eclipse-temurin:17-jre
RUN apt-get update && apt-get install -y --no-install-recommends curl && rm -rf /var/lib/apt/lists/*
RUN useradd --system --uid 10001 eventlens
WORKDIR /app
COPY --from=build /workspace/services/ingestion-service/target/ingestion-service-*.jar app.jar
USER eventlens
EXPOSE 8081
ENTRYPOINT ["java","-XX:MaxRAMPercentage=75","-jar","app.jar"]

