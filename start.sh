#!/bin/bash

# Script de inicio rápido para GestorAPI
# Autor: DistrIA Team

echo "🚀 Iniciando GestorAPI con Docker..."
echo ""

# Verificar si Docker está instalado
if ! command -v docker &> /dev/null; then
    echo "❌ Docker no está instalado. Por favor instala Docker primero."
    exit 1
fi

# Verificar si Docker Compose está instalado
if ! command -v docker-compose &> /dev/null; then
    echo "❌ Docker Compose no está instalado. Por favor instala Docker Compose primero."
    exit 1
fi

# Detener contenedores previos si existen
echo "🛑 Deteniendo contenedores previos (si existen)..."
docker-compose down 2>/dev/null

# Construir y levantar el contenedor
echo "🔨 Construyendo la imagen Docker..."
docker-compose build

echo "🚢 Levantando el contenedor..."
docker-compose up -d

# Esperar a que la aplicación esté lista
echo ""
echo "⏳ Esperando que la aplicación inicie (esto puede tomar 30-60 segundos)..."
sleep 10

# Verificar el estado
for i in {1..12}; do
    if curl -s http://localhost:8081/actuator/health > /dev/null 2>&1; then
        echo ""
        echo "✅ ¡GestorAPI está funcionando correctamente!"
        echo ""
        echo "📊 Endpoints disponibles:"
        echo "   - GraphQL API: http://localhost:8081/graphql"
        echo "   - GraphiQL UI:  http://localhost:8081/graphiql"
        echo "   - Health Check: http://localhost:8081/actuator/health"
        echo ""
        echo "📝 Ver logs: docker-compose logs -f"
        echo "🛑 Detener:  docker-compose down"
        echo ""
        exit 0
    fi
    echo "   Intento $i/12..."
    sleep 5
done

echo ""
echo "⚠️  La aplicación está tardando más de lo esperado."
echo "   Verifica los logs con: docker-compose logs -f"
echo ""
