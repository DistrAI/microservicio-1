-- Script de inicialización para DistrIA Database
-- Este script se ejecuta automáticamente cuando se crea el contenedor de PostgreSQL

-- Crear extensiones necesarias
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- Crear esquemas adicionales si son necesarios
-- CREATE SCHEMA IF NOT EXISTS analytics;
-- CREATE SCHEMA IF NOT EXISTS reporting;

-- Configurar timezone
SET timezone = 'America/La_Paz';

-- Mensaje de confirmación
SELECT 'DistrIA Database initialized successfully!' as status;
