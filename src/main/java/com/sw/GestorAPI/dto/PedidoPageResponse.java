package com.sw.GestorAPI.dto;

import com.sw.GestorAPI.entity.Pedido;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class PedidoPageResponse {
    private List<Pedido> content;
    private int totalElements;
    private int totalPages;
    private int page;
    private int size;
}
