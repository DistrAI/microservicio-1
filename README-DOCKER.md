# 🐳 GestorAPI - Guía de Docker

## 📋 Requisitos Previos
- Docker instalado
- Docker Compose instalado
- Conexión a internet (para conectar a Supabase)

## 🚀 Comandos para Levantar la Aplicación

### Opción 1: Con Docker Compose (Recomendado)
```bash
# Construir y levantar el contenedor
docker-compose up --build

# En modo detached (segundo plano)
docker-compose up -d --build

# Ver logs
docker-compose logs -f gestor-api

# Detener
docker-compose down
```

### Opción 2: Solo con Docker
```bash
# Construir la imagen
docker build -t gestor-api:latest .

# Ejecutar el contenedor
docker run -d \
  -p 8081:8081 \
  --name gestor-api \
  --network host \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://dpg-d49a9fk9c44c73bilt60-a.oregon-postgres.render.com:5432/gestorapi_ij3r \
  -e SPRING_DATASOURCE_USERNAME=admin \
  -e SPRING_DATASOURCE_PASSWORD=GimjVfMKs8ca2LSryY24otdIMJWl38W2 \
  -e SERVER_PORT=8081 \
  gestor-api:latest

# Ver logs
docker logs -f gestor-api

# Detener y eliminar
docker stop gestor-api && docker rm gestor-api
```

## 🔍 Verificar que Funciona

### Health Check
```bash
curl http://localhost:8081/actuator/health
```

### GraphiQL Interface
Abre en tu navegador:
```
http://localhost:8081/graphiql
```

### GraphQL Endpoint
```
http://localhost:8081/graphql
```

## 🛠️ Comandos Útiles

### Ver contenedores corriendo
```bash
docker ps
```

### Entrar al contenedor
```bash
docker exec -it gestor-api sh
```

### Ver logs en tiempo real
```bash
docker-compose logs -f
```

### Reconstruir sin caché
```bash
docker-compose build --no-cache
docker-compose up
```

### Limpiar todo
```bash
# Detener y eliminar contenedores
docker-compose down

# Eliminar imágenes
docker rmi gestor-api

# Limpiar volúmenes (cuidado!)
docker-compose down -v
```

## 🌐 Base de Datos

La aplicación está configurada para conectarse automáticamente a Render PostgreSQL:
- **Host:** dpg-d49a9fk9c44c73bilt60-a.oregon-postgres.render.com
- **Puerto:** 5432
- **Database:** gestorapi_ij3r
- **User:** admin

Las credenciales están configuradas en `docker-compose.yml` y `application.properties`.

## 📊 Endpoints Disponibles

| Endpoint | Descripción |
|----------|-------------|
| `http://localhost:8081/graphql` | API GraphQL |
| `http://localhost:8081/graphiql` | Interfaz GraphiQL |
| `http://localhost:8081/actuator/health` | Health Check |
| `http://localhost:8081/actuator/info` | Info de la app |

## 🔧 Variables de Entorno

Puedes modificar las variables en `docker-compose.yml`:

```yaml
environment:
  SPRING_DATASOURCE_URL: jdbc:postgresql://...
  SPRING_DATASOURCE_USERNAME: postgres
  SPRING_DATASOURCE_PASSWORD: tu_password
  SPRING_JPA_HIBERNATE_DDL_AUTO: update
  JAVA_OPTS: "-Xms256m -Xmx512m"
```

## 🐛 Troubleshooting

### El contenedor no inicia
```bash
# Ver logs detallados
docker-compose logs gestor-api

# Verificar que el puerto 8081 no esté ocupado
sudo lsof -i :8081
```

### No conecta a la base de datos
- Verifica que tienes conexión a internet
- Verifica que las credenciales de Render sean correctas
- Asegúrate de que la base de datos en Render esté activa
- Revisa los logs: `docker-compose logs -f`

### Rebuild completo
```bash
docker-compose down
docker system prune -a
docker-compose up --build
```
