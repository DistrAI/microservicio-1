package com.sw.GestorAPI.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class AsignarPedidosRutaInput {
    @NotNull(message = "La ruta es obligatoria")
    private Long rutaId;

    @NotEmpty(message = "Debe indicar al menos un pedido")
    private List<Long> pedidosIds;
}
