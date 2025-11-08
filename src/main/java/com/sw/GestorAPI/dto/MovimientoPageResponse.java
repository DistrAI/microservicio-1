package com.sw.GestorAPI.dto;

import com.sw.GestorAPI.entity.MovimientoInventario;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class MovimientoPageResponse {
    private List<MovimientoInventario> content;
    private int totalElements;
    private int totalPages;
    private int page;
    private int size;
}
