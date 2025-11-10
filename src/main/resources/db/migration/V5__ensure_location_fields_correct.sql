-- Asegurar que los campos de ubicación estén correctos
-- Esta migración es idempotente y puede ejecutarse múltiples veces

-- Verificar y corregir tipos de datos para coordenadas GPS
-- Solo ejecutar si las columnas existen

DO $$
BEGIN
    -- Usuarios (empresas)
    IF EXISTS (SELECT 1 FROM information_schema.columns 
               WHERE table_name = 'usuarios' AND column_name = 'latitud_empresa') THEN
        ALTER TABLE usuarios ALTER COLUMN latitud_empresa TYPE DOUBLE PRECISION;
    END IF;
    
    IF EXISTS (SELECT 1 FROM information_schema.columns 
               WHERE table_name = 'usuarios' AND column_name = 'longitud_empresa') THEN
        ALTER TABLE usuarios ALTER COLUMN longitud_empresa TYPE DOUBLE PRECISION;
    END IF;

    -- Clientes
    IF EXISTS (SELECT 1 FROM information_schema.columns 
               WHERE table_name = 'clientes' AND column_name = 'latitud_cliente') THEN
        ALTER TABLE clientes ALTER COLUMN latitud_cliente TYPE DOUBLE PRECISION;
    END IF;
    
    IF EXISTS (SELECT 1 FROM information_schema.columns 
               WHERE table_name = 'clientes' AND column_name = 'longitud_cliente') THEN
        ALTER TABLE clientes ALTER COLUMN longitud_cliente TYPE DOUBLE PRECISION;
    END IF;
END $$;
