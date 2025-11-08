package com.sw.GestorAPI.dto;

import com.sw.GestorAPI.entity.Inventario;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class InventarioPageResponse {
    private List<Inventario> content;
    private int totalElements;
    private int totalPages;
    private int page;
    private int size;
}
