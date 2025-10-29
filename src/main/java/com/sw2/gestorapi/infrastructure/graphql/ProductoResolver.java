package com.sw2.gestorapi.infrastructure.graphql;

import com.sw2.gestorapi.application.dto.CrearProductoInput;
import com.sw2.gestorapi.application.services.ProductoService;
import com.sw2.gestorapi.application.services.EmpresaService;
import com.sw2.gestorapi.domain.entities.Producto;
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
public class ProductoResolver {

    private final ProductoService productoService;
    private final EmpresaService empresaService;

    @QueryMapping
    public Producto producto(@Argument String id) {
        log.info("Consultando producto con ID: {}", id);
        return productoService.obtenerProductoPorId(UUID.fromString(id));
    }

    @QueryMapping
    public List<Producto> productos(@Argument String empresaId) {
        log.info("Consultando productos de empresa: {}", empresaId);
        return productoService.obtenerProductosPorEmpresa(UUID.fromString(empresaId));
    }

    @QueryMapping
    public List<Producto> productosConStockBajo(@Argument String empresaId) {
        log.info("Consultando productos con stock bajo de empresa: {}", empresaId);
        return productoService.obtenerProductosConStockBajo(UUID.fromString(empresaId));
    }

    @MutationMapping
    public Producto crearProducto(@Argument CrearProductoInput input) {
        log.info("Creando nuevo producto: {}", input.getNombre());
        
        Producto producto = Producto.builder()
                .nombre(input.getNombre())
                .descripcion(input.getDescripcion())
                .sku(input.getSku())
                .precio(input.getPrecio())
                .costo(input.getCosto())
                .stockActual(input.getStockActual())
                .stockMinimo(input.getStockMinimo())
                .unidadMedida(input.getUnidadMedida())
                .categoria(input.getCategoria())
                .pesoKg(input.getPesoKg())
                .imagenUrl(input.getImagenUrl())
                .requiereRefrigeracion(input.getRequiereRefrigeracion())
                .esFragil(input.getEsFragil())
                .empresa(empresaService.obtenerEmpresaPorId(input.getEmpresaId()))
                .build();
        
        return productoService.crearProducto(producto);
    }

    @MutationMapping
    public Producto actualizarStock(@Argument String productoId, @Argument Integer nuevoStock) {
        log.info("Actualizando stock del producto {} a {}", productoId, nuevoStock);
        return productoService.actualizarStock(UUID.fromString(productoId), nuevoStock);
    }
}
