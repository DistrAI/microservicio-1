package com.sw.GestorAPI.service;

import com.sw.GestorAPI.entity.Inventario;
import com.sw.GestorAPI.entity.MovimientoInventario;
import com.sw.GestorAPI.entity.Producto;
import com.sw.GestorAPI.enums.TipoMovimiento;
import com.sw.GestorAPI.repository.InventarioRepository;
import com.sw.GestorAPI.repository.MovimientoInventarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InventarioService {

    private final InventarioRepository inventarioRepository;
    private final MovimientoInventarioRepository movimientoRepository;
    private final ProductoService productoService;

    public Page<Inventario> listar(@NonNull Pageable pageable) {
        return inventarioRepository.findAll(pageable);
    }

    public Page<Inventario> listarActivos(@NonNull Pageable pageable) {
        return inventarioRepository.findByActivoTrue(pageable);
    }

    public Page<Inventario> buscarPorNombreProducto(@NonNull String nombre, @NonNull Pageable pageable) {
        return inventarioRepository.findByProductoNombreContainingIgnoreCase(nombre, pageable);
    }

    public Page<Inventario> listarStockBajo(@NonNull Pageable pageable) {
        return inventarioRepository.findInventariosConStockBajo(pageable);
    }

    public Optional<Inventario> obtenerPorProductoId(@NonNull Long productoId) {
        return inventarioRepository.findByProductoId(productoId);
    }

    public Page<MovimientoInventario> listarMovimientosPorProducto(@NonNull Long productoId,
            @NonNull Pageable pageable) {
        return movimientoRepository.findByProductoId(productoId, pageable);
    }

    @Transactional
    public Inventario crearInventario(@NonNull Long productoId, @NonNull Integer cantidadInicial, String ubicacion,
            @NonNull Integer stockMinimo) {
        Producto producto = productoService.obtenerPorId(productoId)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado con ID: " + productoId));

        if (inventarioRepository.existsByProductoId(productoId)) {
            throw new IllegalArgumentException("Ya existe inventario para el producto: " + producto.getNombre());
        }

        Inventario inventario = Inventario.builder()
                .producto(producto)
                .cantidad(cantidadInicial)
                .ubicacion(ubicacion)
                .stockMinimo(stockMinimo)
                .activo(true)
                .build();

        inventario = inventarioRepository.save(inventario);

        // Registrar movimiento inicial si hay cantidad
        if (cantidadInicial > 0) {
            int cantidadAnterior = 0;
            int cantidadNueva = cantidadInicial;
            registrarMovimiento(producto, TipoMovimiento.ENTRADA, cantidadInicial, "Inventario inicial", null,
                    cantidadAnterior, cantidadNueva);
        }

        return inventario;
    }

    @Transactional
    public Inventario ajustarStock(@NonNull Long productoId, @NonNull Integer delta, @NonNull String motivo) {
        Inventario inventario = obtenerOCrearInventario(productoId);

        int cantidadAnterior = inventario.getCantidad();
        int nuevaCantidad = cantidadAnterior + delta;
        if (nuevaCantidad < 0) {
            throw new IllegalArgumentException("Stock insuficiente. Stock actual: " + inventario.getCantidad()
                    + ", requerido: " + Math.abs(delta));
        }

        inventario.setCantidad(nuevaCantidad);
        inventario = inventarioRepository.save(inventario);

        // Registrar movimiento
        TipoMovimiento tipo = delta > 0 ? TipoMovimiento.ENTRADA : TipoMovimiento.SALIDA;
        registrarMovimiento(inventario.getProducto(), tipo, Math.abs(delta), motivo, null, cantidadAnterior,
                nuevaCantidad);

        return inventario;
    }

    @Transactional
    public void descontarStock(@NonNull Long productoId, @NonNull Integer cantidad, @NonNull String motivo,
            Long pedidoId) {
        Inventario inventario = obtenerOCrearInventario(productoId);

        if (!inventario.tieneStockSuficiente(cantidad)) {
            throw new IllegalArgumentException(
                    "Stock insuficiente para producto: " + inventario.getProducto().getNombre() +
                            ". Stock actual: " + inventario.getCantidad() + ", requerido: " + cantidad);
        }
        int cantidadAnterior = inventario.getCantidad();
        inventario.setCantidad(cantidadAnterior - cantidad);
        inventarioRepository.save(inventario);

        // Registrar movimiento de salida
        registrarMovimiento(inventario.getProducto(), TipoMovimiento.SALIDA, cantidad, motivo, pedidoId,
                cantidadAnterior, inventario.getCantidad());
    }

    @Transactional
    public void revertirStock(@NonNull Long productoId, @NonNull Integer cantidad, @NonNull String motivo,
            Long pedidoId) {
        Inventario inventario = obtenerOCrearInventario(productoId);

        int cantidadAnterior = inventario.getCantidad();
        inventario.setCantidad(cantidadAnterior + cantidad);
        inventarioRepository.save(inventario);

        // Registrar movimiento de entrada (reversión)
        registrarMovimiento(inventario.getProducto(), TipoMovimiento.ENTRADA, cantidad, motivo, pedidoId,
                cantidadAnterior, inventario.getCantidad());
    }

    @Transactional
    public Inventario actualizarInventario(@NonNull Long productoId, String ubicacion, Integer stockMinimo) {
        Inventario inventario = obtenerOCrearInventario(productoId);

        if (ubicacion != null)
            inventario.setUbicacion(ubicacion);
        if (stockMinimo != null)
            inventario.setStockMinimo(stockMinimo);

        return inventarioRepository.save(inventario);
    }

    @Transactional
    public Inventario desactivarInventario(@NonNull Long productoId) {
        Inventario inventario = obtenerOCrearInventario(productoId);
        inventario.setActivo(false);
        return inventarioRepository.save(inventario);
    }

    private Inventario obtenerOCrearInventario(@NonNull Long productoId) {
        return inventarioRepository.findByProductoId(productoId)
                .orElseGet(() -> {
                    // Crear inventario automáticamente con cantidad 0 si no existe
                    Producto producto = productoService.obtenerPorId(productoId)
                            .orElseThrow(
                                    () -> new IllegalArgumentException("Producto no encontrado con ID: " + productoId));

                    return inventarioRepository.save(Inventario.builder()
                            .producto(producto)
                            .cantidad(0)
                            .ubicacion("Sin ubicación")
                            .stockMinimo(0)
                            .activo(true)
                            .build());
                });
    }

    private void registrarMovimiento(@NonNull Producto producto, @NonNull TipoMovimiento tipo, @NonNull Integer cantidad, @NonNull String motivo, Long pedidoId, @NonNull Integer cantidadAnterior, @NonNull Integer cantidadNueva) {

        MovimientoInventario movimiento = MovimientoInventario.builder()
            
            
                .producto(producto)
                .tipo(tipo)
                .cantidad(cantidad)
                .motivo(motivo)
                .cantidadAnterior(cantidadAnterior)
                .cantidadNueva(cantidadNueva)
                .build();

        if (pedidoId != null) {
            // Aquí podrías cargar el pedido si necesitas la relación completa
            // Por ahora solo guardamos el ID en el motivo para simplicidad
            movimiento.setMotivo(motivo + " (Pedido ID: " + pedidoId + ")");
        }

        movimientoRepository.save(movimiento);
    }

    public Page<Inventario> pagina(int page, int size) {
        return listar(PageRequest.of(page, size));
    }
}
