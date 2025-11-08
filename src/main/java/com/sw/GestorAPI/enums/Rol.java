package com.sw.GestorAPI.enums;

/**
 * Enum que define los roles de usuario en el sistema DistrIA
 * 
 * ADMIN: Acceso total al sistema (Gerente, Operador, Administrador)
 *        - Gestión completa de productos, clientes, pedidos
 *        - Creación y asignación de rutas
 *        - Reportes y análisis
 *        - Gestión de usuarios
 * 
 * REPARTIDOR: Acceso limitado solo a entregas
 *             - Ver solo sus rutas asignadas
 *             - Actualizar estado de pedidos (En camino -> Entregado)
 *             - Subir foto de entrega
 *             - Sin acceso a inventario ni gestión
 */
public enum Rol {
    ADMIN,
    REPARTIDOR
}
