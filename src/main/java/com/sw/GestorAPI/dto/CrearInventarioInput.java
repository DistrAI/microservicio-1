package com.sw.GestorAPI.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CrearInventarioInput {

    @NotNull(message = "El ID del producto es obligatorio")
    private Long productoId;

    @NotNull(message = "La cantidad inicial es obligatoria")
    @Min(value = 0, message = "La cantidad inicial debe ser mayor o igual a 0")
    private Integer cantidadInicial;

    @NotNull(message = "La ubicación es obligatoria")
    @Size(min = 1, max = 100, message = "La ubicación debe tener entre 1 y 100 caracteres")
    private String ubicacion;

    @NotNull(message = "El stock mínimo es obligatorio")
    @Min(value = 0, message = "El stock mínimo debe ser mayor o igual a 0")
    private Integer stockMinimo;

    // Constructors
    public CrearInventarioInput() {
    }

    public CrearInventarioInput(Long productoId, Integer cantidadInicial, String ubicacion, Integer stockMinimo) {
        this.productoId = productoId;
        this.cantidadInicial = cantidadInicial;
        this.ubicacion = ubicacion;
        this.stockMinimo = stockMinimo;
    }

    // Getters and Setters
    public Long getProductoId() {
        return productoId;
    }

    public void setProductoId(Long productoId) {
        this.productoId = productoId;
    }

    public Integer getCantidadInicial() {
        return cantidadInicial;
    }

    public void setCantidadInicial(Integer cantidadInicial) {
        this.cantidadInicial = cantidadInicial;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public Integer getStockMinimo() {
        return stockMinimo;
    }

    public void setStockMinimo(Integer stockMinimo) {
        this.stockMinimo = stockMinimo;
    }
}
