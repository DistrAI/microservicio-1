package com.sw.GestorAPI.resolver;

import com.sw.GestorAPI.dto.CrearPedidoInput;
import com.sw.GestorAPI.dto.ItemPedidoInput;
import com.sw.GestorAPI.dto.PedidoPageResponse;
import com.sw.GestorAPI.entity.Pedido;
import com.sw.GestorAPI.enums.EstadoPedido;
import com.sw.GestorAPI.service.PedidoService;
import jakarta.validation.Valid;
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

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@Slf4j
public class PedidoResolver {

    private final PedidoService pedidoService;

    // =====================
    // QUERIES (ADMIN, REPARTIDOR)
    // =====================
    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN','REPARTIDOR')")
    public PedidoPageResponse pedidos(@Argument Integer page, @Argument Integer size) {
        Pageable pageable = PageRequest.of(page == null ? 0 : page, size == null ? 10 : size);
        Page<Pedido> result = pedidoService.listar(pageable);
        return new PedidoPageResponse(
                result.getContent(),
                (int) result.getTotalElements(),
                result.getTotalPages(),
                result.getNumber(),
                result.getSize()
        );
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN','REPARTIDOR')")
    public PedidoPageResponse pedidosActivos(@Argument Integer page, @Argument Integer size) {
        Pageable pageable = PageRequest.of(page == null ? 0 : page, size == null ? 10 : size);
        Page<Pedido> result = pedidoService.listarActivos(pageable);
        return new PedidoPageResponse(
                result.getContent(),
                (int) result.getTotalElements(),
                result.getTotalPages(),
                result.getNumber(),
                result.getSize()
        );
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN','REPARTIDOR')")
    public PedidoPageResponse pedidosPorEstado(@Argument EstadoPedido estado, @Argument Integer page, @Argument Integer size) {
        Pageable pageable = PageRequest.of(page == null ? 0 : page, size == null ? 10 : size);
        Page<Pedido> result = pedidoService.listarPorEstado(estado, pageable);
        return new PedidoPageResponse(
                result.getContent(),
                (int) result.getTotalElements(),
                result.getTotalPages(),
                result.getNumber(),
                result.getSize()
        );
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN','REPARTIDOR')")
    public PedidoPageResponse pedidosPorCliente(@Argument Long clienteId, @Argument Integer page, @Argument Integer size) {
        Pageable pageable = PageRequest.of(page == null ? 0 : page, size == null ? 10 : size);
        Page<Pedido> result = pedidoService.listarPorCliente(clienteId, pageable);
        return new PedidoPageResponse(
                result.getContent(),
                (int) result.getTotalElements(),
                result.getTotalPages(),
                result.getNumber(),
                result.getSize()
        );
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN','REPARTIDOR')")
    public Pedido pedido(@Argument Long id) {
        return pedidoService.obtenerPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado con ID: " + id));
    }

    // =====================
    // MUTATIONS
    // =====================
    @MutationMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Pedido crearPedido(@Argument @Valid CrearPedidoInput input) {
        List<PedidoService.ItemPedidoData> items = input.getItems().stream()
                .map(this::mapItem)
                .collect(Collectors.toList());
        return pedidoService.crearPedido(input.getClienteId(), input.getDireccionEntrega(), input.getObservaciones(), items);
    }

    @MutationMapping
    @PreAuthorize("hasAnyRole('ADMIN','REPARTIDOR')")
    public Pedido actualizarEstadoPedido(@Argument Long id, @Argument EstadoPedido estado) {
        return pedidoService.actualizarEstado(id, estado);
    }

    @MutationMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Pedido cancelarPedido(@Argument Long id, @Argument String motivo) {
        return pedidoService.cancelarPedido(id, motivo);
    }

    @MutationMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Pedido desactivarPedido(@Argument Long id) {
        return pedidoService.desactivarPedido(id);
    }

    @MutationMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Boolean eliminarPedido(@Argument Long id) {
        pedidoService.eliminarPedido(id);
        return true;
    }

    private PedidoService.ItemPedidoData mapItem(ItemPedidoInput i) {
        PedidoService.ItemPedidoData d = new PedidoService.ItemPedidoData();
        d.setProductoId(i.getProductoId());
        d.setCantidad(i.getCantidad());
        d.setPrecioUnitario(i.getPrecioUnitario());
        return d;
    }
}
