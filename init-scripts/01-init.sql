-- ===================================================================
-- SCRIPT DE INICIALIZACIÓN SIMPLIFICADO - DistrIA Database
-- ===================================================================

-- Crear extensiones básicas necesarias
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- Configurar timezone para Bolivia
SET timezone = 'America/La_Paz';

-- ===================================================================
-- FUNCIONES AUXILIARES
-- ===================================================================

-- Función para generar códigos únicos
CREATE OR REPLACE FUNCTION generate_unique_code(prefix TEXT, length INTEGER DEFAULT 8)
RETURNS TEXT AS $$
BEGIN
    RETURN UPPER(prefix || '-' || SUBSTRING(MD5(RANDOM()::TEXT) FROM 1 FOR length));
END;
$$ LANGUAGE plpgsql;

-- Función para auditoría automática
CREATE OR REPLACE FUNCTION audit_trigger_function()
RETURNS TRIGGER AS $$
BEGIN
    IF TG_OP = 'INSERT' THEN
        INSERT INTO audit.audit_log (table_name, operation, new_data, changed_by, changed_at)
        VALUES (TG_TABLE_NAME, TG_OP, row_to_json(NEW), current_user, NOW());
        RETURN NEW;
    ELSIF TG_OP = 'UPDATE' THEN
        INSERT INTO audit.audit_log (table_name, operation, old_data, new_data, changed_by, changed_at)
        VALUES (TG_TABLE_NAME, TG_OP, row_to_json(OLD), row_to_json(NEW), current_user, NOW());
        RETURN NEW;
    ELSIF TG_OP = 'DELETE' THEN
        INSERT INTO audit.audit_log (table_name, operation, old_data, changed_by, changed_at)
        VALUES (TG_TABLE_NAME, TG_OP, row_to_json(OLD), current_user, NOW());
        RETURN OLD;
    END IF;
    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

-- ===================================================================
-- TABLA DE AUDITORÍA
-- ===================================================================
CREATE TABLE IF NOT EXISTS audit.audit_log (
    id SERIAL PRIMARY KEY,
    table_name TEXT NOT NULL,
    operation TEXT NOT NULL,
    old_data JSONB,
    new_data JSONB,
    changed_by TEXT NOT NULL,
    changed_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

-- Índices para la tabla de auditoría
CREATE INDEX IF NOT EXISTS idx_audit_log_table_name ON audit.audit_log(table_name);
CREATE INDEX IF NOT EXISTS idx_audit_log_changed_at ON audit.audit_log(changed_at);
CREATE INDEX IF NOT EXISTS idx_audit_log_operation ON audit.audit_log(operation);

-- ===================================================================
-- CONFIGURACIONES DE SEGURIDAD
-- ===================================================================

-- Crear rol de solo lectura para reportes
DO $$
BEGIN
    IF NOT EXISTS (SELECT FROM pg_catalog.pg_roles WHERE rolname = 'distraia_readonly') THEN
        CREATE ROLE distraia_readonly;
    END IF;
END
$$;

-- Permisos para el rol de solo lectura
GRANT CONNECT ON DATABASE distriadb TO distraia_readonly;
GRANT USAGE ON SCHEMA public TO distraia_readonly;
GRANT USAGE ON SCHEMA reporting TO distraia_readonly;
GRANT SELECT ON ALL TABLES IN SCHEMA public TO distraia_readonly;
GRANT SELECT ON ALL TABLES IN SCHEMA reporting TO distraia_readonly;

-- ===================================================================
-- DATOS INICIALES PARA DESARROLLO
-- ===================================================================

-- Insertar empresa de prueba (solo en desarrollo)
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM empresas WHERE ruc_nit = '1234567890') THEN
        INSERT INTO empresas (id, nombre, ruc_nit, email, telefono, plan_suscripcion, limite_productos, limite_clientes, limite_usuarios, active, created_at, updated_at, version)
        VALUES (
            gen_random_uuid(),
            'Empresa Demo DistrIA',
            '1234567890',
            'demo@distraia.com',
            '+591 70000000',
            'PROFESIONAL',
            1000,
            5000,
            10,
            true,
            NOW(),
            NOW(),
            0
        );
    END IF;
EXCEPTION
    WHEN undefined_table THEN
        -- La tabla aún no existe, se creará con Hibernate
        NULL;
END
$$;

-- Mensaje de confirmación
SELECT 'DistrIA Database initialized successfully!' as status,
       version() as postgresql_version,
       current_timestamp as initialized_at;
