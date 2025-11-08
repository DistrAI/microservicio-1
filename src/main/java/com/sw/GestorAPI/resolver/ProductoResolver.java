package com.sw.GestorAPI.resolver;

import com.sw.GestorAPI.dto.ActualizarProductoInput;
import com.sw.GestorAPI.dto.CrearProductoInput;
import com.sw.GestorAPI.dto.ProductoPageResponse;
import com.sw.GestorAPI.entity.Producto;
import com.sw.GestorAPI.service.ProductoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import org.springframework.security.access.prepost.PreAuthorize;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ProductoResolver {

    private final ProductoService productoService;

    // =====================
    // QUERIES
    // =====================
    @QueryMapping
    public ProductoPageResponse productos(@Argument Integer page, @Argument Integer size) {
        Pageable pageable = PageRequest.of(page == null ? 0 : page, size == null ? 10 : size);
        Page<Producto> result = productoService.listar(pageable);
        return new ProductoPageResponse(
                result.getContent(),
                (int) result.getTotalElements(),
                result.getTotalPages(),
                result.getNumber(),
                result.getSize()
        );
    }

    @QueryMapping
    public ProductoPageResponse productosActivos(@Argument Integer page, @Argument Integer size) {
        Pageable pageable = PageRequest.of(page == null ? 0 : page, size == null ? 10 : size);
        Page<Producto> result = productoService.listarActivos(pageable);
        return new ProductoPageResponse(
                result.getContent(),
                (int) result.getTotalElements(),
                result.getTotalPages(),
                result.getNumber(),
                result.getSize()
        );
    }

    @QueryMapping
    public Producto producto(@Argument Long id) {
        return productoService.obtenerPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado con ID: " + id));
    }

    @QueryMapping
    public Producto productoPorSku(@Argument String sku) {
        return productoService.obtenerPorSku(sku)
                .orElse(null);
    }

    @QueryMapping
    public ProductoPageResponse buscarProductosPorNombre(@Argument String nombre,
                                                         @Argument Integer page,
                                                         @Argument Integer size) {
        Pageable pageable = PageRequest.of(page == null ? 0 : page, size == null ? 10 : size);
        Page<Producto> result = productoService.buscarPorNombre(nombre, pageable);
        return new ProductoPageResponse(
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
    public Producto crearProducto(@Argument CrearProductoInput input) {
        Producto p = Producto.builder()
                .nombre(input.getNombre())
                .sku(input.getSku())
                .descripcion(input.getDescripcion())
                .precio(input.getPrecio())
                .activo(true)
                .build();
        return productoService.crearProducto(p);
    }

    @MutationMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Producto actualizarProducto(@Argument Long id, @Argument ActualizarProductoInput input) {
        Producto datos = Producto.builder()
                .nombre(input.getNombre())
                .sku(input.getSku())
                .descripcion(input.getDescripcion())
                .precio(input.getPrecio())
                .activo(input.getActivo())
                .build();
        return productoService.actualizarProducto(id, datos);
    }

    @MutationMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Producto desactivarProducto(@Argument Long id) {
        return productoService.desactivarProducto(id);
    }

    @MutationMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Producto activarProducto(@Argument Long id) {
        return productoService.activarProducto(id);
    }

    @MutationMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Boolean eliminarProducto(@Argument Long id) {
        productoService.eliminarProducto(id);
        return true;
    }
}