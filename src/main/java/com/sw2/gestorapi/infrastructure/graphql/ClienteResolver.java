package com.sw2.gestorapi.infrastructure.graphql;

import com.sw2.gestorapi.application.services.ClienteService;
import com.sw2.gestorapi.application.services.EmpresaService;
import com.sw2.gestorapi.domain.entities.Cliente;
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
public class ClienteResolver {

    private final ClienteService clienteService;
    private final EmpresaService empresaService;

    @QueryMapping
    public Cliente cliente(@Argument String id) {
        log.info("Consultando cliente con ID: {}", id);
        return clienteService.obtenerClientePorId(UUID.fromString(id));
    }

    @QueryMapping
    public List<Cliente> clientes(@Argument String empresaId) {
        log.info("Consultando clientes de empresa: {}", empresaId);
        return clienteService.obtenerClientesPorEmpresa(UUID.fromString(empresaId));
    }

    @QueryMapping
    public List<Cliente> clientesVIP(@Argument String empresaId) {
        log.info("Consultando clientes VIP de empresa: {}", empresaId);
        return clienteService.obtenerClientesVIP(UUID.fromString(empresaId));
    }

    @QueryMapping
    public List<Cliente> clientesPorSegmento(@Argument String empresaId, @Argument String segmento) {
        log.info("Consultando clientes por segmento {} de empresa: {}", segmento, empresaId);
        Cliente.SegmentoCliente segmentoEnum = Cliente.SegmentoCliente.valueOf(segmento.toUpperCase());
        return clienteService.obtenerClientesPorSegmento(UUID.fromString(empresaId), segmentoEnum);
    }

    @QueryMapping
    public List<Cliente> buscarClientes(@Argument String empresaId, @Argument String termino) {
        log.info("Buscando clientes con término '{}' en empresa: {}", termino, empresaId);
        return clienteService.buscarClientesPorNombre(UUID.fromString(empresaId), termino);
    }

    @QueryMapping
    public List<Cliente> clientesQueCumplenHoy(@Argument String empresaId) {
        log.info("Consultando clientes que cumplen años hoy en empresa: {}", empresaId);
        return clienteService.obtenerClientesQueCumplenHoy(UUID.fromString(empresaId));
    }

    @MutationMapping
    public Cliente crearCliente(@Argument CrearClienteInput input) {
        log.info("Creando nuevo cliente: {} {}", input.getNombre(), input.getApellido());
        
        Cliente cliente = Cliente.builder()
                .nombre(input.getNombre())
                .apellido(input.getApellido())
                .email(input.getEmail())
                .telefono(input.getTelefono())
                .documento(input.getDocumento())
                .tipoDocumento(input.getTipoDocumento())
                .fechaNacimiento(input.getFechaNacimiento())
                .preferenciasEntrega(input.getPreferenciasEntrega())
                .empresa(empresaService.obtenerEmpresaPorId(input.getEmpresaId()))
                .build();
        
        return clienteService.crearCliente(cliente);
    }

    @MutationMapping
    public Cliente actualizarCliente(@Argument String clienteId, @Argument ActualizarClienteInput input) {
        log.info("Actualizando cliente: {}", clienteId);
        
        Cliente clienteActualizado = Cliente.builder()
                .nombre(input.getNombre())
                .apellido(input.getApellido())
                .email(input.getEmail())
                .telefono(input.getTelefono())
                .fechaNacimiento(input.getFechaNacimiento())
                .preferenciasEntrega(input.getPreferenciasEntrega())
                .build();
        
        return clienteService.actualizarCliente(UUID.fromString(clienteId), clienteActualizado);
    }

    @MutationMapping
    public Cliente cambiarSegmentoCliente(@Argument String clienteId, @Argument String nuevoSegmento) {
        log.info("Cambiando segmento del cliente {} a {}", clienteId, nuevoSegmento);
        Cliente.SegmentoCliente segmentoEnum = Cliente.SegmentoCliente.valueOf(nuevoSegmento.toUpperCase());
        return clienteService.cambiarSegmento(UUID.fromString(clienteId), segmentoEnum);
    }

    @MutationMapping
    public Boolean desactivarCliente(@Argument String clienteId) {
        log.info("Desactivando cliente: {}", clienteId);
        clienteService.desactivarCliente(UUID.fromString(clienteId));
        return true;
    }

    @QueryMapping
    public ClienteEstadisticas estadisticasClientes(@Argument String empresaId) {
        log.info("Consultando estadísticas de clientes para empresa: {}", empresaId);
        ClienteService.ClienteEstadisticas stats = clienteService.obtenerEstadisticas(UUID.fromString(empresaId));
        return new ClienteEstadisticas(stats);
    }

    // DTOs internos
    public static class CrearClienteInput {
        private String nombre;
        private String apellido;
        private String email;
        private String telefono;
        private String documento;
        private Cliente.TipoDocumento tipoDocumento;
        private java.time.LocalDate fechaNacimiento;
        private String preferenciasEntrega;
        private UUID empresaId;

        // Getters
        public String getNombre() { return nombre; }
        public String getApellido() { return apellido; }
        public String getEmail() { return email; }
        public String getTelefono() { return telefono; }
        public String getDocumento() { return documento; }
        public Cliente.TipoDocumento getTipoDocumento() { return tipoDocumento; }
        public java.time.LocalDate getFechaNacimiento() { return fechaNacimiento; }
        public String getPreferenciasEntrega() { return preferenciasEntrega; }
        public UUID getEmpresaId() { return empresaId; }
    }

    public static class ActualizarClienteInput {
        private String nombre;
        private String apellido;
        private String email;
        private String telefono;
        private java.time.LocalDate fechaNacimiento;
        private String preferenciasEntrega;

        // Getters
        public String getNombre() { return nombre; }
        public String getApellido() { return apellido; }
        public String getEmail() { return email; }
        public String getTelefono() { return telefono; }
        public java.time.LocalDate getFechaNacimiento() { return fechaNacimiento; }
        public String getPreferenciasEntrega() { return preferenciasEntrega; }
    }

    public static class ClienteEstadisticas {
        private Integer totalClientes;
        private Integer clientesVIP;
        private Integer clientesFrecuentes;
        private Integer clientesNuevos;

        public ClienteEstadisticas(ClienteService.ClienteEstadisticas stats) {
            // Constructor que convierte desde el DTO del servicio
        }

        // Getters
        public Integer getTotalClientes() { return totalClientes; }
        public Integer getClientesVIP() { return clientesVIP; }
        public Integer getClientesFrecuentes() { return clientesFrecuentes; }
        public Integer getClientesNuevos() { return clientesNuevos; }
    }
}
