-- Add columns for previous and new quantity in movimientos_inventario
ALTER TABLE movimientos_inventario
    ADD COLUMN IF NOT EXISTS cantidad_anterior INT,
    ADD COLUMN IF NOT EXISTS cantidad_nueva INT;

-- Backfill strategy: if both are NULL, try to infer a reasonable default
-- We default cantidad_anterior to 0 when unknown, and cantidad_nueva to cantidad_anterior +/- cantidad
-- based on tipo (ENTRADA -> +, SALIDA -> -). For AJUSTE, sum cantidad tal cual.
-- Note: Adjust as needed if you keep historical accuracy elsewhere.

-- Initialize NULLs to 0 to avoid issues
UPDATE movimientos_inventario
SET cantidad_anterior = COALESCE(cantidad_anterior, 0)
WHERE cantidad_anterior IS NULL;

-- For ENTRADA: nueva = anterior + cantidad (when nueva is NULL)
UPDATE movimientos_inventario
SET cantidad_nueva = cantidad_anterior + cantidad
WHERE cantidad_nueva IS NULL AND tipo = 'ENTRADA';

-- For SALIDA: nueva = anterior - cantidad (when nueva is NULL)
UPDATE movimientos_inventario
SET cantidad_nueva = cantidad_anterior - cantidad
WHERE cantidad_nueva IS NULL AND tipo = 'SALIDA';

-- For AJUSTE: nueva = anterior + cantidad (could be negative or positive)
UPDATE movimientos_inventario
SET cantidad_nueva = cantidad_anterior + cantidad
WHERE cantidad_nueva IS NULL AND tipo = 'AJUSTE';

-- Enforce NOT NULL after backfill
ALTER TABLE movimientos_inventario
    ALTER COLUMN cantidad_anterior SET NOT NULL,
    ALTER COLUMN cantidad_nueva SET NOT NULL;
