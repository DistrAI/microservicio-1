package com.sw.GestorAPI.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class CrearPedidoInput {
    @NotNull(message = "El cliente es obligatorio")
    private Long clienteId;
    
    @NotBlank(message = "La dirección de entrega es obligatoria")
    private String direccionEntrega;
    
    private String observaciones;
    
    @NotEmpty(message = "Debe incluir al menos un item en el pedido")
    @Valid
    private List<ItemPedidoInput> items;
}
