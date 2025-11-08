package com.sw.GestorAPI.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ItemPedidoInput {
    @NotNull(message = "El producto es obligatorio")
    private Long productoId;
    
    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser mayor a 0")
    private Integer cantidad;
    
    @DecimalMin(value = "0.0", inclusive = false, message = "El precio unitario debe ser mayor a 0")
    private BigDecimal precioUnitario; // Opcional, si no se proporciona usa el precio del producto
}
