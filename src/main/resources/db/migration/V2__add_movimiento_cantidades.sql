-- Agregar columnas de cantidad anterior y nueva a movimientos_inventario
ALTER TABLE movimientos_inventario ADD COLUMN cantidad_anterior integer;
ALTER TABLE movimientos_inventario ADD COLUMN cantidad_nueva integer;

-- Backfill seguro: establecer valores por defecto para registros existentes
UPDATE movimientos_inventario SET cantidad_anterior = 0 WHERE cantidad_anterior IS NULL;
UPDATE movimientos_inventario SET cantidad_nueva = 0 WHERE cantidad_nueva IS NULL;

-- Hacer las columnas NOT NULL
ALTER TABLE movimientos_inventario ALTER COLUMN cantidad_anterior SET NOT NULL;
ALTER TABLE movimientos_inventario ALTER COLUMN cantidad_nueva SET NOT NULL;
