package com.sw2.gestorapi.application.services;

import com.sw2.gestorapi.domain.entities.Pedido;
import com.sw2.gestorapi.domain.entities.PedidoItem;
import com.sw2.gestorapi.domain.repositories.PedidoRepository;
import com.sw2.gestorapi.shared.exceptions.ResourceNotFoundException;
import com.sw2.gestorapi.shared.exceptions.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ProductoService productoService;

    /**
     * Crear un nuevo pedido
     */
    public Pedido crearPedido(Pedido pedido) {
        log.info("Creando nuevo pedido para cliente: {}", pedido.getCliente().getId());
        
        pedido.setFechaPedido(LocalDateTime.now());
        pedido.setEstado(Pedido.EstadoPedido.PENDIENTE);
        
        // Calcular totales
        pedido.calcularTotal();
        
        return pedidoRepository.save(pedido);
    }

    /**
     * Obtener pedido por ID
     */
    @Transactional(readOnly = true)
    public Pedido obtenerPedidoPorId(UUID pedidoId) {
        return pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado con ID: " + pedidoId));
    }

    /**
     * Obtener pedidos por empresa
     */
    @Transactional(readOnly = true)
    public List<Pedido> obtenerPedidosPorEmpresa(UUID empresaId) {
        return pedidoRepository.findByEmpresaId(empresaId);
    }

    /**
     * Obtener pedidos pendientes
     */
    @Transactional(readOnly = true)
    public List<Pedido> obtenerPedidosPendientes(UUID empresaId) {
        return pedidoRepository.findPedidosPendientes(empresaId);
    }

    /**
     * Confirmar pedido
     */
    public Pedido confirmarPedido(UUID pedidoId) {
        log.info("Confirmando pedido: {}", pedidoId);
        
        Pedido pedido = obtenerPedidoPorId(pedidoId);
        
        if (pedido.getEstado() != Pedido.EstadoPedido.PENDIENTE) {
            throw new BusinessException("Solo se pueden confirmar pedidos pendientes");
        }
        
        // Verificar stock disponible
        for (PedidoItem item : pedido.getItems()) {
            if (!productoService.obtenerProductoPorId(item.getProducto().getId())
                    .tieneStock(item.getCantidad())) {
                throw new BusinessException("Stock insuficiente para el producto: " + 
                                          item.getProducto().getNombre());
            }
        }
        
        // Reducir stock
        for (PedidoItem item : pedido.getItems()) {
            productoService.reducirStock(item.getProducto().getId(), item.getCantidad());
        }
        
        pedido.setEstado(Pedido.EstadoPedido.CONFIRMADO);
        return pedidoRepository.save(pedido);
    }

    /**
     * Marcar pedido como en ruta
     */
    public Pedido marcarEnRuta(UUID pedidoId) {
        Pedido pedido = obtenerPedidoPorId(pedidoId);
        pedido.setEstado(Pedido.EstadoPedido.EN_RUTA);
        return pedidoRepository.save(pedido);
    }

    /**
     * Marcar pedido como entregado
     */
    public Pedido marcarEntregado(UUID pedidoId) {
        Pedido pedido = obtenerPedidoPorId(pedidoId);
        pedido.setEstado(Pedido.EstadoPedido.ENTREGADO);
        pedido.setFechaEntregaReal(LocalDateTime.now());
        return pedidoRepository.save(pedido);
    }

    /**
     * Cancelar pedido
     */
    public Pedido cancelarPedido(UUID pedidoId) {
        Pedido pedido = obtenerPedidoPorId(pedidoId);
        
        if (!pedido.puedeSerCancelado()) {
            throw new BusinessException("El pedido no puede ser cancelado en su estado actual");
        }
        
        pedido.setEstado(Pedido.EstadoPedido.CANCELADO);
        return pedidoRepository.save(pedido);
    }
}
