#!/bin/bash

echo "🛑 Deteniendo contenedores..."
docker-compose down

echo "🧹 Limpiando contenedores antiguos..."
docker system prune -f

echo "🔨 Reconstruyendo imagen..."
docker-compose build --no-cache

echo "🚀 Iniciando aplicación..."
docker-compose up

