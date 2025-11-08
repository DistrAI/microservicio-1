package com.sw.GestorAPI.resolver;

import com.sw.GestorAPI.dto.ActualizarClienteInput;
import com.sw.GestorAPI.dto.ClientePageResponse;
import com.sw.GestorAPI.dto.CrearClienteInput;
import com.sw.GestorAPI.entity.Cliente;
import com.sw.GestorAPI.service.ClienteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ClienteResolver {

    private final ClienteService clienteService;

    // =====================
    // QUERIES
    // =====================
    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN','REPARTIDOR')")
    public ClientePageResponse clientes(@Argument Integer page, @Argument Integer size) {
        Pageable pageable = PageRequest.of(page == null ? 0 : page, size == null ? 10 : size);
        Page<Cliente> result = clienteService.listar(pageable);
        return new ClientePageResponse(
                result.getContent(),
                (int) result.getTotalElements(),
                result.getTotalPages(),
                result.getNumber(),
                result.getSize()
        );
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN','REPARTIDOR')")
    public ClientePageResponse clientesActivos(@Argument Integer page, @Argument Integer size) {
        Pageable pageable = PageRequest.of(page == null ? 0 : page, size == null ? 10 : size);
        Page<Cliente> result = clienteService.listarActivos(pageable);
        return new ClientePageResponse(
                result.getContent(),
                (int) result.getTotalElements(),
                result.getTotalPages(),
                result.getNumber(),
                result.getSize()
        );
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN','REPARTIDOR')")
    public Cliente cliente(@Argument Long id) {
        return clienteService.obtenerPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado con ID: " + id));
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN','REPARTIDOR')")
    public Cliente clientePorEmail(@Argument String email) {
        return clienteService.obtenerPorEmail(email)
                .orElse(null);
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN','REPARTIDOR')")
    public ClientePageResponse buscarClientesPorNombre(@Argument String nombre,
                                                       @Argument Integer page,
                                                       @Argument Integer size) {
        Pageable pageable = PageRequest.of(page == null ? 0 : page, size == null ? 10 : size);
        Page<Cliente> result = clienteService.buscarPorNombre(nombre, pageable);
        return new ClientePageResponse(
                result.getContent(),
                (int) result.getTotalElements(),
                result.getTotalPages(),
                result.getNumber(),
                result.getSize()
        );
    }

    // =====================
    // MUTATIONS
    // =====================
    @MutationMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Cliente crearCliente(@Argument CrearClienteInput input) {
        Cliente c = Cliente.builder()
                .nombre(input.getNombre())
                .email(input.getEmail())
                .telefono(input.getTelefono())
                .direccion(input.getDireccion())
                .activo(true)
                .build();
        return clienteService.crearCliente(c);
    }

    @MutationMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Cliente actualizarCliente(@Argument Long id, @Argument ActualizarClienteInput input) {
        Cliente datos = Cliente.builder()
                .nombre(input.getNombre())
                .email(input.getEmail())
                .telefono(input.getTelefono())
                .direccion(input.getDireccion())
                .activo(input.getActivo())
                .build();
        return clienteService.actualizarCliente(id, datos);
    }

    @MutationMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Cliente desactivarCliente(@Argument Long id) {
        return clienteService.desactivarCliente(id);
    }

    @MutationMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Cliente activarCliente(@Argument Long id) {
        return clienteService.activarCliente(id);
    }

    @MutationMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Boolean eliminarCliente(@Argument Long id) {
        clienteService.eliminarCliente(id);
        return true;
    }
}
