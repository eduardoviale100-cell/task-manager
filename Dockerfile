# ============================================
# Stage 1: Build
# ============================================
FROM eclipse-temurin:17-jdk-alpine AS build

WORKDIR /app

# Copiar Maven wrapper y pom.xml primero para cachear dependencias
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

# Asegurar permisos de ejecución para el wrapper
RUN chmod +x mvnw

# Descargar dependencias (capa cacheada si pom.xml no cambia)
RUN ./mvnw dependency:go-offline -B

# Copiar código fuente
COPY src/ src/

# Compilar el JAR sin ejecutar tests
RUN ./mvnw clean package -DskipTests -B

# ============================================
# Stage 2: Runtime
# ============================================
FROM eclipse-temurin:17-jre-alpine AS runtime

WORKDIR /app

# Crear usuario no-root por seguridad
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# Copiar el JAR compilado desde la etapa de build
COPY --from=build /app/target/*.jar app.jar

# Cambiar al usuario no-root
USER appuser

# Puerto expuesto (debe coincidir con server.port en application.yaml)
EXPOSE 8080

# Health check básico
HEALTHCHECK --interval=30s --timeout=10s --retries=3 --start-period=40s \
    CMD wget --quiet --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# Ejecutar con perfil MySQL por defecto en Docker
# Sobreescribir con: docker run -e SPRING_PROFILES_ACTIVE=h2 ...
ENTRYPOINT ["java", \
    "-XX:+UseContainerSupport", \
    "-XX:MaxRAMPercentage=75.0", \
    "-Djava.security.egd=file:/dev/./urandom", \
    "-jar", "app.jar"]
