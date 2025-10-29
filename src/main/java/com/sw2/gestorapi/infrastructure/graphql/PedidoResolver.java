package com.sw2.gestorapi.infrastructure.graphql;

import com.sw2.gestorapi.application.services.PedidoService;
import com.sw2.gestorapi.domain.entities.Pedido;
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
public class PedidoResolver {

    private final PedidoService pedidoService;

    @QueryMapping
    public Pedido pedido(@Argument String id) {
        log.info("Consultando pedido con ID: {}", id);
        return pedidoService.obtenerPedidoPorId(UUID.fromString(id));
    }

    @QueryMapping
    public List<Pedido> pedidos(@Argument String empresaId) {
        log.info("Consultando pedidos de empresa: {}", empresaId);
        return pedidoService.obtenerPedidosPorEmpresa(UUID.fromString(empresaId));
    }

    @QueryMapping
    public List<Pedido> pedidosPendientes(@Argument String empresaId) {
        log.info("Consultando pedidos pendientes de empresa: {}", empresaId);
        return pedidoService.obtenerPedidosPendientes(UUID.fromString(empresaId));
    }

    @MutationMapping
    public Pedido confirmarPedido(@Argument String pedidoId) {
        log.info("Confirmando pedido: {}", pedidoId);
        return pedidoService.confirmarPedido(UUID.fromString(pedidoId));
    }

    @MutationMapping
    public Pedido marcarPedidoEnRuta(@Argument String pedidoId) {
        log.info("Marcando pedido en ruta: {}", pedidoId);
        return pedidoService.marcarEnRuta(UUID.fromString(pedidoId));
    }

    @MutationMapping
    public Pedido marcarPedidoEntregado(@Argument String pedidoId) {
        log.info("Marcando pedido como entregado: {}", pedidoId);
        return pedidoService.marcarEntregado(UUID.fromString(pedidoId));
    }

    @MutationMapping
    public Pedido cancelarPedido(@Argument String pedidoId) {
        log.info("Cancelando pedido: {}", pedidoId);
        return pedidoService.cancelarPedido(UUID.fromString(pedidoId));
    }
}
