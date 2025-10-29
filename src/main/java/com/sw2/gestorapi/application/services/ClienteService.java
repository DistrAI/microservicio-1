package com.sw2.gestorapi.application.services;

import com.sw2.gestorapi.domain.entities.Cliente;
import com.sw2.gestorapi.domain.repositories.ClienteRepository;
import com.sw2.gestorapi.shared.exceptions.ResourceNotFoundException;
import com.sw2.gestorapi.shared.exceptions.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ClienteService {

    private final ClienteRepository clienteRepository;

    /**
     * Crear un nuevo cliente
     */
    public Cliente crearCliente(Cliente cliente) {
        log.info("Creando nuevo cliente: {} {}", cliente.getNombre(), cliente.getApellido());
        
        // Validar email único por empresa
        if (cliente.getEmail() != null && 
            clienteRepository.existsByEmailAndEmpresaId(cliente.getEmail(), cliente.getEmpresa().getId())) {
            throw new BusinessException("Ya existe un cliente con el email: " + cliente.getEmail());
        }
        
        // Validar documento único por empresa
        if (cliente.getDocumento() != null && 
            clienteRepository.existsByDocumentoAndEmpresaId(cliente.getDocumento(), cliente.getEmpresa().getId())) {
            throw new BusinessException("Ya existe un cliente con el documento: " + cliente.getDocumento());
        }
        
        // Establecer segmento inicial
        if (cliente.getSegmento() == null) {
            cliente.setSegmento(Cliente.SegmentoCliente.NUEVO);
        }
        
        Cliente clienteGuardado = clienteRepository.save(cliente);
        log.info("Cliente creado exitosamente con ID: {}", clienteGuardado.getId());
        
        return clienteGuardado;
    }

    /**
     * Obtener cliente por ID
     */
    @Transactional(readOnly = true)
    public Cliente obtenerClientePorId(UUID clienteId) {
        return clienteRepository.findById(clienteId)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con ID: " + clienteId));
    }

    /**
     * Obtener clientes por empresa
     */
    @Transactional(readOnly = true)
    public List<Cliente> obtenerClientesPorEmpresa(UUID empresaId) {
        return clienteRepository.findByEmpresaIdAndActiveTrue(empresaId);
    }

    /**
     * Obtener clientes VIP
     */
    @Transactional(readOnly = true)
    public List<Cliente> obtenerClientesVIP(UUID empresaId) {
        return clienteRepository.findClientesVIP(empresaId);
    }

    /**
     * Obtener clientes por segmento
     */
    @Transactional(readOnly = true)
    public List<Cliente> obtenerClientesPorSegmento(UUID empresaId, Cliente.SegmentoCliente segmento) {
        return clienteRepository.findByEmpresaIdAndSegmento(empresaId, segmento);
    }

    /**
     * Buscar clientes por nombre o apellido
     */
    @Transactional(readOnly = true)
    public List<Cliente> buscarClientesPorNombre(UUID empresaId, String termino) {
        return clienteRepository.findByEmpresaIdAndNombreOrApellidoContaining(empresaId, termino);
    }

    /**
     * Obtener clientes que cumplen años hoy
     */
    @Transactional(readOnly = true)
    public List<Cliente> obtenerClientesQueCumplenHoy(UUID empresaId) {
        return clienteRepository.findClientesQueCumplenHoy(empresaId, LocalDate.now());
    }

    /**
     * Actualizar cliente
     */
    public Cliente actualizarCliente(UUID clienteId, Cliente clienteActualizado) {
        log.info("Actualizando cliente con ID: {}", clienteId);
        
        Cliente clienteExistente = obtenerClientePorId(clienteId);
        
        // Validar cambio de email
        if (clienteActualizado.getEmail() != null && 
            !clienteActualizado.getEmail().equals(clienteExistente.getEmail())) {
            if (clienteRepository.existsByEmailAndEmpresaId(clienteActualizado.getEmail(), 
                    clienteExistente.getEmpresa().getId())) {
                throw new BusinessException("Ya existe un cliente con el email: " + clienteActualizado.getEmail());
            }
        }
        
        // Actualizar campos
        clienteExistente.setNombre(clienteActualizado.getNombre());
        clienteExistente.setApellido(clienteActualizado.getApellido());
        clienteExistente.setEmail(clienteActualizado.getEmail());
        clienteExistente.setTelefono(clienteActualizado.getTelefono());
        clienteExistente.setFechaNacimiento(clienteActualizado.getFechaNacimiento());
        clienteExistente.setPreferenciasEntrega(clienteActualizado.getPreferenciasEntrega());
        
        return clienteRepository.save(clienteExistente);
    }

    /**
     * Cambiar segmento de cliente
     */
    public Cliente cambiarSegmento(UUID clienteId, Cliente.SegmentoCliente nuevoSegmento) {
        log.info("Cambiando segmento del cliente {} a {}", clienteId, nuevoSegmento);
        
        Cliente cliente = obtenerClientePorId(clienteId);
        cliente.setSegmento(nuevoSegmento);
        
        return clienteRepository.save(cliente);
    }

    /**
     * Segmentación automática de clientes basada en comportamiento
     */
    public void actualizarSegmentacionAutomatica(UUID empresaId) {
        log.info("Ejecutando segmentación automática para empresa: {}", empresaId);
        
        List<Cliente> clientes = obtenerClientesPorEmpresa(empresaId);
        
        for (Cliente cliente : clientes) {
            // Lógica de segmentación (se puede mejorar con datos de pedidos)
            Cliente.SegmentoCliente nuevoSegmento = calcularSegmento(cliente);
            
            if (!cliente.getSegmento().equals(nuevoSegmento)) {
                cliente.setSegmento(nuevoSegmento);
                clienteRepository.save(cliente);
                log.info("Cliente {} actualizado a segmento {}", cliente.getId(), nuevoSegmento);
            }
        }
    }

    /**
     * Desactivar cliente
     */
    public void desactivarCliente(UUID clienteId) {
        log.info("Desactivando cliente con ID: {}", clienteId);
        
        Cliente cliente = obtenerClientePorId(clienteId);
        cliente.setActive(false);
        clienteRepository.save(cliente);
        
        log.info("Cliente desactivado exitosamente");
    }

    /**
     * Obtener estadísticas de clientes
     */
    @Transactional(readOnly = true)
    public ClienteEstadisticas obtenerEstadisticas(UUID empresaId) {
        Long totalClientes = clienteRepository.countByEmpresaId(empresaId);
        Long clientesVIP = clienteRepository.countByEmpresaIdAndSegmento(empresaId, Cliente.SegmentoCliente.VIP);
        Long clientesFrecuentes = clienteRepository.countByEmpresaIdAndSegmento(empresaId, Cliente.SegmentoCliente.FRECUENTE);
        Long clientesNuevos = clienteRepository.countByEmpresaIdAndSegmento(empresaId, Cliente.SegmentoCliente.NUEVO);
        
        return ClienteEstadisticas.builder()
                .totalClientes(totalClientes.intValue())
                .clientesVIP(clientesVIP.intValue())
                .clientesFrecuentes(clientesFrecuentes.intValue())
                .clientesNuevos(clientesNuevos.intValue())
                .build();
    }

    /**
     * Calcular segmento de cliente basado en comportamiento
     */
    private Cliente.SegmentoCliente calcularSegmento(Cliente cliente) {
        // Lógica básica de segmentación
        // En una implementación real, esto se basaría en:
        // - Frecuencia de compras
        // - Monto total gastado
        // - Última fecha de compra
        // - Productos comprados
        
        if (cliente.getPedidos() != null && !cliente.getPedidos().isEmpty()) {
            int numeroPedidos = cliente.getPedidos().size();
            
            if (numeroPedidos >= 10) {
                return Cliente.SegmentoCliente.VIP;
            } else if (numeroPedidos >= 3) {
                return Cliente.SegmentoCliente.FRECUENTE;
            } else {
                return Cliente.SegmentoCliente.NUEVO;
            }
        }
        
        return Cliente.SegmentoCliente.NUEVO;
    }

    /**
     * Clase para estadísticas de clientes
     */
    public static class ClienteEstadisticas {
        private Integer totalClientes;
        private Integer clientesVIP;
        private Integer clientesFrecuentes;
        private Integer clientesNuevos;

        public static ClienteEstadisticasBuilder builder() {
            return new ClienteEstadisticasBuilder();
        }

        public static class ClienteEstadisticasBuilder {
            private Integer totalClientes;
            private Integer clientesVIP;
            private Integer clientesFrecuentes;
            private Integer clientesNuevos;

            public ClienteEstadisticasBuilder totalClientes(Integer totalClientes) {
                this.totalClientes = totalClientes;
                return this;
            }

            public ClienteEstadisticasBuilder clientesVIP(Integer clientesVIP) {
                this.clientesVIP = clientesVIP;
                return this;
            }

            public ClienteEstadisticasBuilder clientesFrecuentes(Integer clientesFrecuentes) {
                this.clientesFrecuentes = clientesFrecuentes;
                return this;
            }

            public ClienteEstadisticasBuilder clientesNuevos(Integer clientesNuevos) {
                this.clientesNuevos = clientesNuevos;
                return this;
            }

            public ClienteEstadisticas build() {
                ClienteEstadisticas estadisticas = new ClienteEstadisticas();
                estadisticas.totalClientes = this.totalClientes;
                estadisticas.clientesVIP = this.clientesVIP;
                estadisticas.clientesFrecuentes = this.clientesFrecuentes;
                estadisticas.clientesNuevos = this.clientesNuevos;
                return estadisticas;
            }
        }
    }
}
