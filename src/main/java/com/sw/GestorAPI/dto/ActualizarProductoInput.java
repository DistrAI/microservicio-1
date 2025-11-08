package com.sw.GestorAPI.dto;

import jakarta.validation.constraints.DecimalMin;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ActualizarProductoInput {
    private String nombre;
    private String sku;
    private String descripcion;
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal precio;
    private Boolean activo;
}
