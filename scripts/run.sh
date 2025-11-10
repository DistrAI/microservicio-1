#!/bin/bash
#
# Script de ayuda para ejecutar el poblado de base de datos
# Instala dependencias automáticamente si no existen
#

set -e

echo "🚀 DistrIA - Script de Poblado de Base de Datos"
echo "=============================================="
echo ""

# Verificar que Python está instalado
if ! command -v python3 &> /dev/null; then
    echo "❌ Error: Python 3 no está instalado"
    echo "Por favor instala Python 3.8 o superior"
    exit 1
fi

echo "✅ Python 3 detectado: $(python3 --version)"
echo ""

# Verificar/instalar dependencias
echo "📦 Verificando dependencias..."
if ! python3 -c "import psycopg2" 2>/dev/null; then
    echo "⚙️  Instalando dependencias..."
    pip3 install -r requirements.txt
    echo ""
else
    echo "✅ Dependencias ya instaladas"
    echo ""
fi

# Ejecutar el script
echo "🎯 Ejecutando poblado de base de datos..."
echo ""
python3 seed_database.py

echo ""
echo "✅ ¡Proceso completado!"
