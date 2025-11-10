-- Corregir tipos de datos para coordenadas GPS
-- Cambiar de DECIMAL a DOUBLE PRECISION para mejor compatibilidad con PostgreSQL

-- Usuarios (empresas)
ALTER TABLE usuarios ALTER COLUMN latitud_empresa TYPE DOUBLE PRECISION;
ALTER TABLE usuarios ALTER COLUMN longitud_empresa TYPE DOUBLE PRECISION;

-- Clientes
ALTER TABLE clientes ALTER COLUMN latitud_cliente TYPE DOUBLE PRECISION;
ALTER TABLE clientes ALTER COLUMN longitud_cliente TYPE DOUBLE PRECISION;
