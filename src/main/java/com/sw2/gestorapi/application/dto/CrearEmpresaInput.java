package com.sw2.gestorapi.application.dto;

import com.sw2.gestorapi.domain.entities.Empresa;
import lombok.Data;

@Data
public class CrearEmpresaInput {
    private String nombre;
    private String rucNit;
    private String email;
    private String telefono;
    private String direccion;
    private String ciudad;
    private String pais;
    private Empresa.PlanSuscripcion planSuscripcion;
}
