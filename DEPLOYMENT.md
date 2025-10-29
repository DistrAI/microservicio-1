# 🚀 Guía de Despliegue - DistrIA GestorAPI

## 📋 Requisitos Previos

### Sistema Operativo
- **Linux**: Ubuntu 20.04+ / CentOS 8+ / RHEL 8+
- **Windows**: Windows Server 2019+
- **macOS**: macOS 11+

### Software Requerido
- **Docker**: 20.10+
- **Docker Compose**: 2.0+
- **Java**: 17+ (para desarrollo local)
- **PostgreSQL**: 15+ (si no usas Docker)

## 🔧 Configuración de Producción

### 1. Preparar el Entorno

```bash
# Crear directorios necesarios
sudo mkdir -p /opt/distraia/{data,logs,config}
sudo mkdir -p /opt/distraia/data/{postgres,redis}
sudo mkdir -p /opt/distraia/logs/{app,nginx}

# Configurar permisos
sudo chown -R $USER:$USER /opt/distraia
chmod -R 755 /opt/distraia
```

### 2. Configurar Variables de Entorno

```bash
# Copiar archivo de ejemplo
cp .env.prod.example .env.prod

# Editar variables (¡IMPORTANTE!)
nano .env.prod
```

**Variables críticas a cambiar:**
```bash
POSTGRES_PASSWORD=TU_PASSWORD_SUPER_SEGURO
REDIS_PASSWORD=TU_REDIS_PASSWORD_SEGURO
JWT_SECRET=TU_JWT_SECRET_MINIMO_32_CARACTERES
CORS_ALLOWED_ORIGINS=https://tu-dominio.com
```

### 3. Configurar SSL/TLS (Recomendado)

```bash
# Generar certificado SSL (Let's Encrypt recomendado)
sudo certbot certonly --standalone -d api.distraia.com

# Configurar en .env.prod
SSL_ENABLED=true
SSL_KEY_STORE=/path/to/keystore.p12
SSL_KEY_STORE_PASSWORD=tu_keystore_password
```

## 🐳 Despliegue con Docker

### Desarrollo Local
```bash
# Levantar servicios de desarrollo
docker-compose up -d

# Ver logs
docker-compose logs -f gestor-api

# Acceder a GraphiQL
# http://localhost:8080/graphiql
```

### Producción
```bash
# Levantar servicios de producción
docker-compose -f docker-compose.prod.yml --env-file .env.prod up -d

# Verificar estado de servicios
docker-compose -f docker-compose.prod.yml ps

# Ver logs de producción
docker-compose -f docker-compose.prod.yml logs -f gestor-api
```

## 📊 Monitoreo y Salud

### Health Checks
```bash
# Verificar salud de la aplicación
curl http://localhost:8081/actuator/health

# Verificar métricas
curl http://localhost:8081/actuator/metrics

# Verificar base de datos
docker exec distrIA-postgres-prod pg_isready -U distriauser
```

### Prometheus Metrics
- **URL**: `http://localhost:9090`
- **Métricas de la app**: `http://localhost:8081/actuator/prometheus`

## 🔒 Seguridad en Producción

### 1. Base de Datos
```sql
-- Crear usuario de solo lectura para reportes
CREATE USER distraia_readonly WITH PASSWORD 'readonly_password';
GRANT CONNECT ON DATABASE distriadb_prod TO distraia_readonly;
GRANT USAGE ON SCHEMA public TO distraia_readonly;
GRANT SELECT ON ALL TABLES IN SCHEMA public TO distraia_readonly;
```

### 2. Firewall
```bash
# Permitir solo puertos necesarios
sudo ufw allow 80/tcp
sudo ufw allow 443/tcp
sudo ufw allow 22/tcp
sudo ufw enable
```

### 3. Backup Automático
```bash
# Crear script de backup
cat > /opt/distraia/backup.sh << 'EOF'
#!/bin/bash
BACKUP_DIR="/opt/distraia/backups"
DATE=$(date +%Y%m%d_%H%M%S)
docker exec distrIA-postgres-prod pg_dump -U distriauser distriadb_prod > $BACKUP_DIR/backup_$DATE.sql
find $BACKUP_DIR -name "backup_*.sql" -mtime +30 -delete
EOF

chmod +x /opt/distraia/backup.sh

# Agregar a crontab
echo "0 2 * * * /opt/distraia/backup.sh" | crontab -
```

## 🚀 Comandos de Despliegue

### Actualización de la Aplicación
```bash
# 1. Hacer backup
docker exec distrIA-postgres-prod pg_dump -U distriauser distriadb_prod > backup_pre_update.sql

# 2. Construir nueva imagen
docker-compose -f docker-compose.prod.yml build gestor-api

# 3. Actualizar servicios (zero-downtime)
docker-compose -f docker-compose.prod.yml up -d --no-deps gestor-api

# 4. Verificar despliegue
curl http://localhost:8081/actuator/health
```

### Rollback
```bash
# Volver a la versión anterior
docker-compose -f docker-compose.prod.yml down
docker-compose -f docker-compose.prod.yml up -d
```

## 📈 Optimizaciones de Performance

### 1. PostgreSQL
```sql
-- Configuraciones recomendadas para producción
ALTER SYSTEM SET shared_buffers = '256MB';
ALTER SYSTEM SET effective_cache_size = '1GB';
ALTER SYSTEM SET maintenance_work_mem = '64MB';
ALTER SYSTEM SET checkpoint_completion_target = 0.9;
ALTER SYSTEM SET wal_buffers = '16MB';
ALTER SYSTEM SET default_statistics_target = 100;
SELECT pg_reload_conf();
```

### 2. JVM
```bash
# Variables de entorno JVM optimizadas (ya incluidas en docker-compose.prod.yml)
JAVA_OPTS="-Xms512m -Xmx1g -XX:+UseG1GC -XX:+UseStringDeduplication"
```

## 🔍 Troubleshooting

### Problemas Comunes

#### 1. Error de Conexión a Base de Datos
```bash
# Verificar conectividad
docker exec distrIA-gestor-api-prod ping postgres-db

# Verificar logs de PostgreSQL
docker logs distrIA-postgres-prod
```

#### 2. Error de Memoria
```bash
# Verificar uso de memoria
docker stats

# Ajustar límites en docker-compose.prod.yml
deploy:
  resources:
    limits:
      memory: 2G
```

#### 3. Problemas de SSL
```bash
# Verificar certificado
openssl x509 -in /path/to/cert.pem -text -noout

# Verificar configuración SSL
curl -I https://api.distraia.com/actuator/health
```

## 📞 Soporte

### Logs Importantes
```bash
# Logs de aplicación
tail -f /opt/distraia/logs/app/gestorapi.log

# Logs de base de datos
docker logs distrIA-postgres-prod

# Logs de sistema
journalctl -u docker -f
```

### Contacto
- **Email**: support@distraia.com
- **Documentación**: https://docs.distraia.com
- **GitHub Issues**: https://github.com/DistrAI/microservicio-1/issues

## 🔄 Actualizaciones

Para mantener el sistema actualizado:

1. **Revisar releases**: https://github.com/DistrAI/microservicio-1/releases
2. **Leer changelog**: Siempre revisar cambios antes de actualizar
3. **Hacer backup**: Nunca actualizar sin backup
4. **Probar en staging**: Validar en entorno de pruebas primero
5. **Monitorear**: Vigilar métricas después del despliegue

---

**¡Importante!** Este es un sistema de producción. Siempre sigue las mejores prácticas de seguridad y mantén backups actualizados.
