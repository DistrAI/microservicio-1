-- Agregar campos de ubicación para usuarios (empresas/PYMES)
ALTER TABLE usuarios ADD COLUMN direccion_empresa VARCHAR(500);
ALTER TABLE usuarios ADD COLUMN latitud_empresa DOUBLE PRECISION;
ALTER TABLE usuarios ADD COLUMN longitud_empresa DOUBLE PRECISION;
ALTER TABLE usuarios ADD COLUMN nombre_empresa VARCHAR(200);

-- Agregar campos de ubicación mejorados para clientes
ALTER TABLE clientes ALTER COLUMN direccion TYPE VARCHAR(500);
ALTER TABLE clientes ADD COLUMN latitud_cliente DOUBLE PRECISION;
ALTER TABLE clientes ADD COLUMN longitud_cliente DOUBLE PRECISION;
ALTER TABLE clientes ADD COLUMN referencia_direccion VARCHAR(300);

-- Crear índices para optimizar consultas geográficas
CREATE INDEX idx_usuarios_ubicacion ON usuarios(latitud_empresa, longitud_empresa);
CREATE INDEX idx_clientes_ubicacion ON clientes(latitud_cliente, longitud_cliente);

-- Comentarios para documentar el propósito de los campos
COMMENT ON COLUMN usuarios.direccion_empresa IS 'Dirección completa de la empresa/PYME del usuario administrador';
COMMENT ON COLUMN usuarios.latitud_empresa IS 'Latitud GPS de la ubicación de la empresa (formato decimal)';
COMMENT ON COLUMN usuarios.longitud_empresa IS 'Longitud GPS de la ubicación de la empresa (formato decimal)';
COMMENT ON COLUMN usuarios.nombre_empresa IS 'Nombre comercial de la empresa/PYME';

COMMENT ON COLUMN clientes.latitud_cliente IS 'Latitud GPS de la dirección del cliente (formato decimal)';
COMMENT ON COLUMN clientes.longitud_cliente IS 'Longitud GPS de la dirección del cliente (formato decimal)';
COMMENT ON COLUMN clientes.referencia_direccion IS 'Referencias adicionales para facilitar la entrega (ej: edificio, apartamento, puntos de referencia)';
