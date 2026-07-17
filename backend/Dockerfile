# ─── TalentFlow Backend — Render Deploy ──────────────────
# Multi-stage: build com Maven + JDK 21, runtime com JDK 21 slim

FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
# Baixa dependências primeiro (cache layer)
RUN mvn dependency:go-offline -B -q
COPY src ./src
RUN mvn package -DskipTests -B -q

FROM eclipse-temurin:21-jre
WORKDIR /app
# Render injeta $PORT automaticamente
EXPOSE 8080
COPY --from=build /app/target/*.jar app.jar
# Healthcheck opcional
HEALTHCHECK --interval=30s --timeout=3s --start-period=30s \
  CMD java -cp app.jar org.springframework.boot.loader.launch.PropertiesLauncher --server.port=0 2>&1 | grep -q "Started\|UP" || exit 1
ENTRYPOINT ["java", "-jar", "app.jar"]
