# Dockerfile para GestorAPI - DistrIA
# Usa una imagen base de OpenJDK 17 con Alpine para menor tamaño
FROM openjdk:17-jdk-alpine

# Información del mantenedor
LABEL maintainer="DistrIA Team"
LABEL description="GestorAPI - Microservicio central de DistrIA"

# Instalar curl para health checks
RUN apk add --no-cache curl

# Crear directorio de trabajo
WORKDIR /app

# Copiar el archivo pom.xml y el wrapper de Maven
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .

# Dar permisos de ejecución al wrapper de Maven
RUN chmod +x mvnw

# Descargar dependencias (esto se cachea si no cambia el pom.xml)
RUN ./mvnw dependency:go-offline -B

# Copiar el código fuente
COPY src src

# Construir la aplicación
RUN ./mvnw clean package -DskipTests

# Crear un usuario no-root para ejecutar la aplicación
RUN addgroup -g 1001 -S appgroup && \
    adduser -u 1001 -S appuser -G appgroup

# Cambiar la propiedad del directorio de trabajo al usuario de la aplicación
RUN chown -R appuser:appgroup /app

# Cambiar al usuario no-root
USER appuser

# Exponer el puerto 8080
EXPOSE 8080

# Variables de entorno por defecto
ENV SPRING_PROFILES_ACTIVE=docker
ENV JAVA_OPTS="-Xmx512m -Xms256m"

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# Comando para ejecutar la aplicación
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar target/GestorAPI-*.jar"]
