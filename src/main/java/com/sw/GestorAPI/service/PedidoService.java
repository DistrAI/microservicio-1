package com.sw.GestorAPI.service;

import com.sw.GestorAPI.entity.Cliente;
import com.sw.GestorAPI.entity.ItemPedido;
import com.sw.GestorAPI.entity.Pedido;
import com.sw.GestorAPI.entity.Producto;
import com.sw.GestorAPI.enums.EstadoPedido;
import com.sw.GestorAPI.repository.PedidoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ClienteService clienteService;
    private final ProductoService productoService;
    private final InventarioService inventarioService;

    public Page<Pedido> listar(@NonNull Pageable pageable) {
        return pedidoRepository.findAll(pageable);
    }

    public Page<Pedido> listarActivos(@NonNull Pageable pageable) {
        return pedidoRepository.findByActivoTrue(pageable);
    }

    public Page<Pedido> listarPorEstado(@NonNull EstadoPedido estado, @NonNull Pageable pageable) {
        return pedidoRepository.findByEstado(estado, pageable);
    }

    public Page<Pedido> listarPorCliente(@NonNull Long clienteId, @NonNull Pageable pageable) {
        return pedidoRepository.findByClienteId(clienteId, pageable);
    }

    public Page<Pedido> listarEnProceso(@NonNull Pageable pageable) {
        return pedidoRepository.findPedidosEnProceso(pageable);
    }

    public Optional<Pedido> obtenerPorId(@NonNull Long id) {
        return pedidoRepository.findById(id);
    }

    @Transactional
    public Pedido crearPedido(@NonNull Long clienteId, @NonNull String direccionEntrega, String observaciones, @NonNull List<ItemPedidoData> items) {
        // Validar cliente
        Cliente cliente = clienteService.obtenerPorId(clienteId)
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado con ID: " + clienteId));

        if (!cliente.getActivo()) {
            throw new IllegalArgumentException("No se puede crear pedido para cliente inactivo");
        }

        // Crear pedido
        Pedido pedido = Pedido.builder()
                .cliente(cliente)
                .estado(EstadoPedido.PENDIENTE)
                .direccionEntrega(direccionEntrega)
                .observaciones(observaciones)
                .total(BigDecimal.ZERO)
                .activo(true)
                .build();

        // Agregar items
        for (ItemPedidoData itemData : items) {
            Producto producto = productoService.obtenerPorId(itemData.getProductoId())
                    .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado con ID: " + itemData.getProductoId()));

            if (!producto.getActivo()) {
                throw new IllegalArgumentException("No se puede agregar producto inactivo: " + producto.getNombre());
            }

            ItemPedido item = ItemPedido.builder()
                    .producto(producto)
                    .cantidad(itemData.getCantidad())
                    .precioUnitario(producto.getPrecio()) // Siempre usar el precio actual del producto
                    .build();

            item.calcularSubtotal();
            pedido.addItem(item);

            // Descontar stock del inventario
            try {
                inventarioService.descontarStock(producto.getId(), itemData.getCantidad(), 
                    "Venta - Pedido", null); // Se actualizará con el ID del pedido después de guardar
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Error en producto " + producto.getNombre() + ": " + e.getMessage());
            }
        }

        Pedido pedidoGuardado = pedidoRepository.save(pedido);

        // Actualizar los movimientos con el ID del pedido
        // Nota: En una implementación más robusta, se podría hacer esto de forma más elegante
        return pedidoGuardado;
    }

    @Transactional
    public Pedido actualizarEstado(@NonNull Long id, @NonNull EstadoPedido nuevoEstado) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado con ID: " + id));

        // Validar transiciones de estado
        validarTransicionEstado(pedido.getEstado(), nuevoEstado);

        pedido.setEstado(nuevoEstado);

        // Si se marca como entregado, establecer fecha de entrega
        if (nuevoEstado == EstadoPedido.ENTREGADO && pedido.getFechaEntrega() == null) {
            pedido.setFechaEntrega(LocalDateTime.now());
        }

        return pedidoRepository.save(pedido);
    }

    @Transactional
    public Pedido cancelarPedido(@NonNull Long id, String motivo) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado con ID: " + id));

        if (pedido.getEstado() == EstadoPedido.ENTREGADO) {
            throw new IllegalArgumentException("No se puede cancelar un pedido ya entregado");
        }

        if (pedido.getEstado() == EstadoPedido.CANCELADO) {
            throw new IllegalArgumentException("El pedido ya está cancelado");
        }

        pedido.setEstado(EstadoPedido.CANCELADO);
        if (motivo != null) {
            pedido.setObservaciones(pedido.getObservaciones() != null ? 
                pedido.getObservaciones() + " | CANCELADO: " + motivo : "CANCELADO: " + motivo);
        }

        // Revertir stock al inventario
        for (ItemPedido item : pedido.getItems()) {
            try {
                inventarioService.revertirStock(item.getProducto().getId(), item.getCantidad(), 
                    "Cancelación de pedido - " + motivo, pedido.getId());
            } catch (Exception e) {
                // Log error pero no fallar la cancelación
                System.err.println("Error al revertir stock para producto " + item.getProducto().getNombre() + ": " + e.getMessage());
            }
        }

        return pedidoRepository.save(pedido);
    }

    @Transactional
    public Pedido desactivarPedido(@NonNull Long id) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado con ID: " + id));
        pedido.setActivo(false);
        return pedidoRepository.save(pedido);
    }

    @Transactional
    public void eliminarPedido(@NonNull Long id) {
        if (!pedidoRepository.existsById(id)) {
            throw new IllegalArgumentException("Pedido no encontrado con ID: " + id);
        }
        pedidoRepository.deleteById(id);
    }

    public long contarPorEstado(@NonNull EstadoPedido estado) {
        return pedidoRepository.countByEstado(estado);
    }

    public Page<Pedido> pagina(int page, int size) {
        return listar(PageRequest.of(page, size));
    }

    private void validarTransicionEstado(EstadoPedido estadoActual, EstadoPedido nuevoEstado) {
        // Reglas de transición de estados
        switch (estadoActual) {
            case PENDIENTE:
                if (nuevoEstado != EstadoPedido.EN_PROCESO && nuevoEstado != EstadoPedido.CANCELADO) {
                    throw new IllegalArgumentException("Desde PENDIENTE solo se puede pasar a EN_PROCESO o CANCELADO");
                }
                break;
            case EN_PROCESO:
                if (nuevoEstado != EstadoPedido.EN_CAMINO && nuevoEstado != EstadoPedido.CANCELADO) {
                    throw new IllegalArgumentException("Desde EN_PROCESO solo se puede pasar a EN_CAMINO o CANCELADO");
                }
                break;
            case EN_CAMINO:
                if (nuevoEstado != EstadoPedido.ENTREGADO && nuevoEstado != EstadoPedido.CANCELADO) {
                    throw new IllegalArgumentException("Desde EN_CAMINO solo se puede pasar a ENTREGADO o CANCELADO");
                }
                break;
            case ENTREGADO:
                throw new IllegalArgumentException("No se puede cambiar el estado de un pedido ENTREGADO");
            case CANCELADO:
                throw new IllegalArgumentException("No se puede cambiar el estado de un pedido CANCELADO");
        }
    }

    // Clase interna para datos de item de pedido
    public static class ItemPedidoData {
        private Long productoId;
        private Integer cantidad;
        private BigDecimal precioUnitario; // OPCIONAL: Si es null, usa automáticamente el precio actual del producto

        public ItemPedidoData() {}

        public ItemPedidoData(Long productoId, Integer cantidad) {
            this.productoId = productoId;
            this.cantidad = cantidad;
        }

        public ItemPedidoData(Long productoId, Integer cantidad, BigDecimal precioUnitario) {
            this.productoId = productoId;
            this.cantidad = cantidad;
            this.precioUnitario = precioUnitario;
        }

        // Getters y setters
        public Long getProductoId() { return productoId; }
        public void setProductoId(Long productoId) { this.productoId = productoId; }
        public Integer getCantidad() { return cantidad; }
        public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
        public BigDecimal getPrecioUnitario() { return precioUnitario; }
        public void setPrecioUnitario(BigDecimal precioUnitario) { this.precioUnitario = precioUnitario; }
    }
}
