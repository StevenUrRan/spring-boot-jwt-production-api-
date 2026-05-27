FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app

# Copy Maven wrapper and pom
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Download dependencies (offline)
RUN ./mvnw -B dependency:go-offline

# Copy source code and build
COPY src src
RUN ./mvnw -B package -DskipTests

# Runtime image
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Crear usuario y grupo de ejecución no-root para seguridad
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# Copiar el jar del stage anterior
COPY --from=build --chown=appuser:appgroup /app/target/*.jar app.jar

# Cambiar al usuario no-root
USER appuser

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
