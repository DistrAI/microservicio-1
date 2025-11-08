package com.sw.GestorAPI.dto;

import com.sw.GestorAPI.entity.RutaEntrega;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class RutaEntregaPageResponse {
    private List<RutaEntrega> content;
    private int totalElements;
    private int totalPages;
    private int page;
    private int size;
}
