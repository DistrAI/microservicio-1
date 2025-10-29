package com.sw2.gestorapi.application.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.UUID;

@Data
public class CrearProductoInput {
    private String nombre;
    private String descripcion;
    private String sku;
    private BigDecimal precio;
    private BigDecimal costo;
    private Integer stockActual;
    private Integer stockMinimo;
    private String unidadMedida;
    private String categoria;
    private BigDecimal pesoKg;
    private String imagenUrl;
    private Boolean requiereRefrigeracion = false;
    private Boolean esFragil = false;
    private UUID empresaId;
}
