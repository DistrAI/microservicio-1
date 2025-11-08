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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InventarioService {

    private final InventarioRepository inventarioRepository;
    private final MovimientoInventarioRepository movimientoRepository;
    private final ProductoService productoService;

    public Page<Inventario> listar(Pageable pageable) {
        return inventarioRepository.findAll(pageable);
    }

    public Page<Inventario> listarActivos(Pageable pageable) {
        return inventarioRepository.findByActivoTrue(pageable);
    }

    public Page<Inventario> buscarPorNombreProducto(String nombre, Pageable pageable) {
        return inventarioRepository.findByProductoNombreContainingIgnoreCase(nombre, pageable);
    }

    public Page<Inventario> listarStockBajo(Pageable pageable) {
        return inventarioRepository.findInventariosConStockBajo(pageable);
    }

    public Optional<Inventario> obtenerPorProductoId(Long productoId) {
        return inventarioRepository.findByProductoId(productoId);
    }

    public Page<MovimientoInventario> listarMovimientosPorProducto(Long productoId, Pageable pageable) {
        return movimientoRepository.findByProductoId(productoId, pageable);
    }

    @Transactional
    public Inventario crearInventario(Long productoId, Integer cantidadInicial, String ubicacion, Integer stockMinimo) {
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
            registrarMovimiento(producto, TipoMovimiento.ENTRADA, cantidadInicial, "Inventario inicial", null);
        }

        return inventario;
    }

    @Transactional
    public Inventario ajustarStock(Long productoId, Integer delta, String motivo) {
        Inventario inventario = obtenerOCrearInventario(productoId);
        
        int nuevaCantidad = inventario.getCantidad() + delta;
        if (nuevaCantidad < 0) {
            throw new IllegalArgumentException("Stock insuficiente. Stock actual: " + inventario.getCantidad() + ", requerido: " + Math.abs(delta));
        }

        inventario.setCantidad(nuevaCantidad);
        inventario = inventarioRepository.save(inventario);

        // Registrar movimiento
        TipoMovimiento tipo = delta > 0 ? TipoMovimiento.ENTRADA : TipoMovimiento.SALIDA;
        registrarMovimiento(inventario.getProducto(), tipo, Math.abs(delta), motivo, null);

        return inventario;
    }

    @Transactional
    public void descontarStock(Long productoId, Integer cantidad, String motivo, Long pedidoId) {
        Inventario inventario = obtenerOCrearInventario(productoId);
        
        if (!inventario.tieneStockSuficiente(cantidad)) {
            throw new IllegalArgumentException("Stock insuficiente para producto: " + inventario.getProducto().getNombre() + 
                ". Stock actual: " + inventario.getCantidad() + ", requerido: " + cantidad);
        }

        inventario.setCantidad(inventario.getCantidad() - cantidad);
        inventarioRepository.save(inventario);

        // Registrar movimiento de salida
        registrarMovimiento(inventario.getProducto(), TipoMovimiento.SALIDA, cantidad, motivo, pedidoId);
    }

    @Transactional
    public void revertirStock(Long productoId, Integer cantidad, String motivo, Long pedidoId) {
        Inventario inventario = obtenerOCrearInventario(productoId);
        
        inventario.setCantidad(inventario.getCantidad() + cantidad);
        inventarioRepository.save(inventario);

        // Registrar movimiento de entrada (reversión)
        registrarMovimiento(inventario.getProducto(), TipoMovimiento.ENTRADA, cantidad, motivo, pedidoId);
    }

    @Transactional
    public Inventario actualizarInventario(Long productoId, String ubicacion, Integer stockMinimo) {
        Inventario inventario = obtenerOCrearInventario(productoId);
        
        if (ubicacion != null) inventario.setUbicacion(ubicacion);
        if (stockMinimo != null) inventario.setStockMinimo(stockMinimo);
        
        return inventarioRepository.save(inventario);
    }

    @Transactional
    public Inventario desactivarInventario(Long productoId) {
        Inventario inventario = obtenerOCrearInventario(productoId);
        inventario.setActivo(false);
        return inventarioRepository.save(inventario);
    }

    private Inventario obtenerOCrearInventario(Long productoId) {
        return inventarioRepository.findByProductoId(productoId)
                .orElseGet(() -> {
                    // Crear inventario automáticamente con cantidad 0 si no existe
                    Producto producto = productoService.obtenerPorId(productoId)
                            .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado con ID: " + productoId));
                    
                    return inventarioRepository.save(Inventario.builder()
                            .producto(producto)
                            .cantidad(0)
                            .ubicacion("Sin ubicación")
                            .stockMinimo(0)
                            .activo(true)
                            .build());
                });
    }

    private void registrarMovimiento(Producto producto, TipoMovimiento tipo, Integer cantidad, String motivo, Long pedidoId) {
        MovimientoInventario movimiento = MovimientoInventario.builder()
                .producto(producto)
                .tipo(tipo)
                .cantidad(cantidad)
                .motivo(motivo)
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
