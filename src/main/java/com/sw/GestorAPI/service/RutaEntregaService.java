package com.sw.GestorAPI.service;

import com.sw.GestorAPI.entity.Pedido;
import com.sw.GestorAPI.entity.RutaEntrega;
import com.sw.GestorAPI.enums.EstadoPedido;
import com.sw.GestorAPI.enums.EstadoRuta;
import com.sw.GestorAPI.repository.PedidoRepository;
import com.sw.GestorAPI.repository.RutaEntregaRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RutaEntregaService {

    private final RutaEntregaRepository rutaEntregaRepository;
    private final PedidoRepository pedidoRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public Page<RutaEntrega> listar(Pageable pageable) {
        return rutaEntregaRepository.findAll(pageable);
    }

    public Page<RutaEntrega> listarActivas(Pageable pageable) {
        return rutaEntregaRepository.findByActivoTrue(pageable);
    }

    public Page<RutaEntrega> listarPorRepartidor(Long repartidorId, Pageable pageable) {
        return rutaEntregaRepository.findByRepartidorId(repartidorId, pageable);
    }

    public Page<RutaEntrega> listarPorEstado(EstadoRuta estado, Pageable pageable) {
        return rutaEntregaRepository.findByEstado(estado, pageable);
    }

    public java.util.Optional<RutaEntrega> obtenerPorId(Long id) {
        return rutaEntregaRepository.findById(id);
    }

    public Page<RutaEntrega> listarPorRepartidorYEstado(Long repartidorId, EstadoRuta estado, Pageable pageable) {
        return rutaEntregaRepository.findByRepartidorIdAndEstado(repartidorId, estado, pageable);
    }

    @Transactional
    public RutaEntrega crearRuta(Long repartidorId, LocalDate fechaRuta, Double distanciaTotalKm, Integer tiempoEstimadoMin, List<Long> pedidosIds) {
        if (repartidorId == null) throw new IllegalArgumentException("Repartidor obligatorio");
        if (fechaRuta == null) throw new IllegalArgumentException("Fecha de ruta obligatoria");

        RutaEntrega ruta = RutaEntrega.builder()
                .repartidor(entityManager.getReference(com.sw.GestorAPI.entity.Usuario.class, repartidorId))
                .estado(EstadoRuta.PLANIFICADA)
                .fechaRuta(fechaRuta)
                .distanciaTotalKm(distanciaTotalKm)
                .tiempoEstimadoMin(tiempoEstimadoMin)
                .activo(true)
                .build();

        if (pedidosIds != null && !pedidosIds.isEmpty()) {
            List<Pedido> pedidos = pedidoRepository.findAllById(pedidosIds);
            for (Pedido p : pedidos) {
                if (p.getEstado() == EstadoPedido.CANCELADO || p.getEstado() == EstadoPedido.ENTREGADO) {
                    throw new IllegalArgumentException("El pedido " + p.getId() + " no puede asignarse a una ruta (estado: " + p.getEstado() + ")");
                }
                ruta.addPedido(p);
            }
        }

        return rutaEntregaRepository.save(ruta);
    }

    @Transactional
    public RutaEntrega asignarPedidos(Long rutaId, List<Long> pedidosIds) {
        RutaEntrega ruta = rutaEntregaRepository.findById(rutaId)
                .orElseThrow(() -> new IllegalArgumentException("Ruta no encontrada con ID: " + rutaId));
        if (pedidosIds == null || pedidosIds.isEmpty()) return ruta;
        List<Pedido> pedidos = pedidoRepository.findAllById(pedidosIds);
        for (Pedido p : pedidos) {
            if (p.getEstado() == EstadoPedido.CANCELADO || p.getEstado() == EstadoPedido.ENTREGADO) {
                throw new IllegalArgumentException("El pedido " + p.getId() + " no puede asignarse a una ruta (estado: " + p.getEstado() + ")");
            }
            if (!ruta.getPedidos().contains(p)) ruta.addPedido(p);
        }
        return rutaEntregaRepository.save(ruta);
    }

    @Transactional
    public RutaEntrega removerPedido(Long rutaId, Long pedidoId) {
        RutaEntrega ruta = rutaEntregaRepository.findById(rutaId)
                .orElseThrow(() -> new IllegalArgumentException("Ruta no encontrada con ID: " + rutaId));
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado con ID: " + pedidoId));
        ruta.removePedido(pedido);
        return rutaEntregaRepository.save(ruta);
    }

    @Transactional
    public RutaEntrega actualizarEstado(Long rutaId, EstadoRuta nuevoEstado) {
        RutaEntrega ruta = rutaEntregaRepository.findById(rutaId)
                .orElseThrow(() -> new IllegalArgumentException("Ruta no encontrada con ID: " + rutaId));
        validarTransicionEstado(ruta.getEstado(), nuevoEstado);
        ruta.setEstado(nuevoEstado);
        return rutaEntregaRepository.save(ruta);
    }

    @Transactional
    public RutaEntrega desactivarRuta(Long rutaId) {
        RutaEntrega ruta = rutaEntregaRepository.findById(rutaId)
                .orElseThrow(() -> new IllegalArgumentException("Ruta no encontrada con ID: " + rutaId));
        ruta.setActivo(false);
        return rutaEntregaRepository.save(ruta);
    }

    @Transactional
    public void eliminarRuta(Long rutaId) {
        rutaEntregaRepository.deleteById(rutaId);
    }

    public Page<RutaEntrega> pagina(int page, int size) {
        return listar(PageRequest.of(page, size));
    }

    private void validarTransicionEstado(EstadoRuta actual, EstadoRuta nuevo) {
        switch (actual) {
            case PLANIFICADA -> {
                if (nuevo != EstadoRuta.EN_CURSO && nuevo != EstadoRuta.CANCELADA) {
                    throw new IllegalArgumentException("Desde PLANIFICADA solo a EN_CURSO o CANCELADA");
                }
            }
            case EN_CURSO -> {
                if (nuevo != EstadoRuta.COMPLETADA && nuevo != EstadoRuta.CANCELADA) {
                    throw new IllegalArgumentException("Desde EN_CURSO solo a COMPLETADA o CANCELADA");
                }
            }
            case COMPLETADA, CANCELADA -> throw new IllegalArgumentException("La ruta ya está cerrada");
        }
    }
}
