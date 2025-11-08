package com.sw.GestorAPI.dto;

import com.sw.GestorAPI.entity.Producto;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ProductoPageResponse {
    private List<Producto> content;
    private int totalElements;
    private int totalPages;
    private int page;
    private int size;
}
