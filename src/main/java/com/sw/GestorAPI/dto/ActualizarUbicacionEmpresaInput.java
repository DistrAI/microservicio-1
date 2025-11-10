package com.sw.GestorAPI.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para actualizar la ubicación de la empresa de un usuario
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActualizarUbicacionEmpresaInput {

    @NotBlank(message = "La dirección de la empresa es obligatoria")
    private String direccionEmpresa;

    @NotNull(message = "La latitud es obligatoria")
    @DecimalMin(value = "-90.0", message = "La latitud debe estar entre -90 y 90")
    @DecimalMax(value = "90.0", message = "La latitud debe estar entre -90 y 90")
    private Double latitudEmpresa;

    @NotNull(message = "La longitud es obligatoria")
    @DecimalMin(value = "-180.0", message = "La longitud debe estar entre -180 y 180")
    @DecimalMax(value = "180.0", message = "La longitud debe estar entre -180 y 180")
    private Double longitudEmpresa;

    @NotBlank(message = "El nombre de la empresa es obligatorio")
    private String nombreEmpresa;
}
