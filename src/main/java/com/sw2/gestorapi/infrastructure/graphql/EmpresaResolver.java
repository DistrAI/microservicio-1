package com.sw2.gestorapi.infrastructure.graphql;

import com.sw2.gestorapi.application.dto.CrearEmpresaInput;
import com.sw2.gestorapi.application.services.EmpresaService;
import com.sw2.gestorapi.domain.entities.Empresa;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
@Slf4j
public class EmpresaResolver {

    private final EmpresaService empresaService;

    @QueryMapping
    public Empresa empresa(@Argument String id) {
        log.info("Consultando empresa con ID: {}", id);
        return empresaService.obtenerEmpresaPorId(UUID.fromString(id));
    }

    @QueryMapping
    public List<Empresa> empresas() {
        log.info("Consultando todas las empresas");
        return empresaService.obtenerTodasLasEmpresas();
    }

    @MutationMapping
    public Empresa crearEmpresa(@Argument CrearEmpresaInput input) {
        log.info("Creando nueva empresa: {}", input.getNombre());
        
        Empresa empresa = Empresa.builder()
                .nombre(input.getNombre())
                .rucNit(input.getRucNit())
                .email(input.getEmail())
                .telefono(input.getTelefono())
                .direccion(input.getDireccion())
                .ciudad(input.getCiudad())
                .pais(input.getPais())
                .planSuscripcion(input.getPlanSuscripcion())
                .build();
        
        return empresaService.crearEmpresa(empresa);
    }
}
