package com.sw.GestorAPI.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CrearProductoInput {
    @NotBlank
    private String nombre;
    @NotBlank
    private String sku;
    private String descripcion;
    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal precio;
}
