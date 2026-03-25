# ── Etapa 1: Build ──────────────────────────────────────────
FROM gradle:8-jdk17-alpine AS builder
WORKDIR /app

# Copiar archivos de dependencias primero (aprovecha caché de Docker)
COPY build.gradle settings.gradle ./
COPY gradle gradle

# Descargar dependencias
RUN gradle dependencies --no-daemon || true

# Copiar el resto del código y compilar
COPY src src
RUN gradle bootJar --no-daemon

# ── Etapa 2: Runtime ─────────────────────────────────────────
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copiar solo el JAR generado
COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]