package com.sw.GestorAPI.resolver;

import com.sw.GestorAPI.dto.AsignarPedidosRutaInput;
import com.sw.GestorAPI.dto.CrearRutaInput;
import com.sw.GestorAPI.dto.RutaEntregaPageResponse;
import com.sw.GestorAPI.entity.RutaEntrega;
import com.sw.GestorAPI.enums.EstadoRuta;
import com.sw.GestorAPI.service.RutaEntregaService;
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

import java.time.LocalDate;

@Controller
@RequiredArgsConstructor
@Slf4j
public class RutaEntregaResolver {

    private final RutaEntregaService rutaEntregaService;

    // =====================
    // QUERIES (ADMIN, REPARTIDOR)
    // =====================
    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN','REPARTIDOR')")
    public RutaEntregaPageResponse rutas(@Argument Integer page, @Argument Integer size) {
        Pageable pageable = PageRequest.of(page == null ? 0 : page, size == null ? 10 : size);
        Page<RutaEntrega> result = rutaEntregaService.listar(pageable);
        return new RutaEntregaPageResponse(
                result.getContent(),
                (int) result.getTotalElements(),
                result.getTotalPages(),
                result.getNumber(),
                result.getSize()
        );
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN','REPARTIDOR')")
    public RutaEntregaPageResponse rutasActivas(@Argument Integer page, @Argument Integer size) {
        Pageable pageable = PageRequest.of(page == null ? 0 : page, size == null ? 10 : size);
        Page<RutaEntrega> result = rutaEntregaService.listarActivas(pageable);
        return new RutaEntregaPageResponse(
                result.getContent(),
                (int) result.getTotalElements(),
                result.getTotalPages(),
                result.getNumber(),
                result.getSize()
        );
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN','REPARTIDOR')")
    public RutaEntregaPageResponse rutasPorRepartidor(@Argument Long repartidorId, @Argument Integer page, @Argument Integer size) {
        Pageable pageable = PageRequest.of(page == null ? 0 : page, size == null ? 10 : size);
        Page<RutaEntrega> result = rutaEntregaService.listarPorRepartidor(repartidorId, pageable);
        return new RutaEntregaPageResponse(
                result.getContent(),
                (int) result.getTotalElements(),
                result.getTotalPages(),
                result.getNumber(),
                result.getSize()
        );
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN','REPARTIDOR')")
    public RutaEntregaPageResponse rutasPorEstado(@Argument EstadoRuta estado, @Argument Integer page, @Argument Integer size) {
        Pageable pageable = PageRequest.of(page == null ? 0 : page, size == null ? 10 : size);
        Page<RutaEntrega> result = rutaEntregaService.listarPorEstado(estado, pageable);
        return new RutaEntregaPageResponse(
                result.getContent(),
                (int) result.getTotalElements(),
                result.getTotalPages(),
                result.getNumber(),
                result.getSize()
        );
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN','REPARTIDOR')")
    public RutaEntrega ruta(@Argument Long id) {
        return rutaEntregaService.obtenerPorId(id).orElse(null);
    }

    // =====================
    // MUTATIONS
    // =====================
    @MutationMapping
    @PreAuthorize("hasRole('ADMIN')")
    public RutaEntrega crearRuta(@Argument @Valid CrearRutaInput input) {
        return rutaEntregaService.crearRuta(input.getRepartidorId(), input.getFechaRuta(), input.getDistanciaTotalKm(), input.getTiempoEstimadoMin(), input.getPedidosIds());
    }

    @MutationMapping
    @PreAuthorize("hasRole('ADMIN')")
    public RutaEntrega asignarPedidosARuta(@Argument @Valid AsignarPedidosRutaInput input) {
        return rutaEntregaService.asignarPedidos(input.getRutaId(), input.getPedidosIds());
    }

    @MutationMapping
    @PreAuthorize("hasRole('ADMIN')")
    public RutaEntrega removerPedidoDeRuta(@Argument Long rutaId, @Argument Long pedidoId) {
        return rutaEntregaService.removerPedido(rutaId, pedidoId);
    }

    @MutationMapping
    @PreAuthorize("hasAnyRole('ADMIN','REPARTIDOR')")
    public RutaEntrega actualizarEstadoRuta(@Argument Long rutaId, @Argument EstadoRuta estado) {
        return rutaEntregaService.actualizarEstado(rutaId, estado);
    }

    @MutationMapping
    @PreAuthorize("hasRole('ADMIN')")
    public RutaEntrega desactivarRuta(@Argument Long rutaId) {
        return rutaEntregaService.desactivarRuta(rutaId);
    }

    @MutationMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Boolean eliminarRuta(@Argument Long rutaId) {
        rutaEntregaService.eliminarRuta(rutaId);
        return true;
    }
}
