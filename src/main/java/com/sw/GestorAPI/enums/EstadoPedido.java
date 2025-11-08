package com.sw.GestorAPI.enums;

/**
 * Estados posibles de un pedido en el sistema DistrIA
 */
public enum EstadoPedido {
    PENDIENTE,      // Pedido creado, esperando procesamiento
    EN_PROCESO,     // Pedido siendo preparado
    EN_CAMINO,      // Pedido en ruta de entrega
    ENTREGADO,      // Pedido entregado exitosamente
    CANCELADO       // Pedido cancelado
}
