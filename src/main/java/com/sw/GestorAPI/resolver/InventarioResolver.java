package com.sw.GestorAPI.resolver;

import com.sw.GestorAPI.dto.AjustarInventarioInput;
import com.sw.GestorAPI.dto.CrearInventarioInput;
import com.sw.GestorAPI.dto.InventarioPageResponse;
import com.sw.GestorAPI.dto.MovimientoPageResponse;
import com.sw.GestorAPI.entity.Inventario;
import com.sw.GestorAPI.entity.MovimientoInventario;
import com.sw.GestorAPI.service.InventarioService;
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

@Controller
@RequiredArgsConstructor
@Slf4j
public class InventarioResolver {

    private final InventarioService inventarioService;

    // =====================
    // QUERIES (ADMIN, REPARTIDOR)
    // =====================
    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN','REPARTIDOR')")
    public InventarioPageResponse inventarios(@Argument Integer page, @Argument Integer size) {
        Pageable pageable = PageRequest.of(page == null ? 0 : page, size == null ? 10 : size);
        Page<Inventario> result = inventarioService.listar(pageable);
        return new InventarioPageResponse(
                result.getContent(),
                (int) result.getTotalElements(),
                result.getTotalPages(),
                result.getNumber(),
                result.getSize()
        );
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN','REPARTIDOR')")
    public InventarioPageResponse inventariosActivos(@Argument Integer page, @Argument Integer size) {
        Pageable pageable = PageRequest.of(page == null ? 0 : page, size == null ? 10 : size);
        Page<Inventario> result = inventarioService.listarActivos(pageable);
        return new InventarioPageResponse(
                result.getContent(),
                (int) result.getTotalElements(),
                result.getTotalPages(),
                result.getNumber(),
                result.getSize()
        );
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN','REPARTIDOR')")
    public Inventario inventarioPorProducto(@Argument Long productoId) {
        return inventarioService.obtenerPorProductoId(productoId)
                .orElse(null);
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN','REPARTIDOR')")
    public InventarioPageResponse inventariosStockBajo(@Argument Integer page, @Argument Integer size) {
        Pageable pageable = PageRequest.of(page == null ? 0 : page, size == null ? 10 : size);
        Page<Inventario> result = inventarioService.listarStockBajo(pageable);
        return new InventarioPageResponse(
                result.getContent(),
                (int) result.getTotalElements(),
                result.getTotalPages(),
                result.getNumber(),
                result.getSize()
        );
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN','REPARTIDOR')")
    public InventarioPageResponse buscarInventariosPorNombre(@Argument String nombre, @Argument Integer page, @Argument Integer size) {
        Pageable pageable = PageRequest.of(page == null ? 0 : page, size == null ? 10 : size);
        Page<Inventario> result = inventarioService.buscarPorNombreProducto(nombre, pageable);
        return new InventarioPageResponse(
                result.getContent(),
                (int) result.getTotalElements(),
                result.getTotalPages(),
                result.getNumber(),
                result.getSize()
        );
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN','REPARTIDOR')")
    public MovimientoPageResponse movimientosPorProducto(@Argument Long productoId, @Argument Integer page, @Argument Integer size) {
        Pageable pageable = PageRequest.of(page == null ? 0 : page, size == null ? 10 : size);
        Page<MovimientoInventario> result = inventarioService.listarMovimientosPorProducto(productoId, pageable);
        return new MovimientoPageResponse(
                result.getContent(),
                (int) result.getTotalElements(),
                result.getTotalPages(),
                result.getNumber(),
                result.getSize()
        );
    }

    // =====================
    // MUTATIONS (SOLO ADMIN)
    // =====================
    @MutationMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Inventario crearInventario(@Argument @Valid CrearInventarioInput input) {
        return inventarioService.crearInventario(
            input.getProductoId(), 
            input.getCantidadInicial(), 
            input.getUbicacion(), 
            input.getStockMinimo()
        );
    }

    @MutationMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Inventario ajustarInventario(@Argument @Valid AjustarInventarioInput input) {
        return inventarioService.ajustarStock(input.getProductoId(), input.getCantidad(), input.getMotivo());
    }

    @MutationMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Inventario desactivarInventario(@Argument Long productoId) {
        return inventarioService.desactivarInventario(productoId);
    }
}
