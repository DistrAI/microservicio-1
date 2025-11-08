package com.sw.GestorAPI.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class CrearRutaInput {
    @NotNull(message = "El repartidor es obligatorio")
    private Long repartidorId;

    @NotNull(message = "La fecha de ruta es obligatoria")
    private LocalDate fechaRuta;

    private Double distanciaTotalKm;
    private Integer tiempoEstimadoMin;
    private List<Long> pedidosIds;
}
