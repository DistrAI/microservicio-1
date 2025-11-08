package com.sw.GestorAPI.dto;

import com.sw.GestorAPI.entity.Cliente;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ClientePageResponse {
    private List<Cliente> content;
    private int totalElements;
    private int totalPages;
    private int page;
    private int size;
}
