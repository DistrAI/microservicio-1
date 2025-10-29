package com.sw2.gestorapi.application.services;

import com.sw2.gestorapi.domain.entities.Producto;
import com.sw2.gestorapi.domain.repositories.ProductoRepository;
import com.sw2.gestorapi.shared.exceptions.ResourceNotFoundException;
import com.sw2.gestorapi.shared.exceptions.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProductoService {

    private final ProductoRepository productoRepository;

    /**
     * Crear un nuevo producto
     */
    public Producto crearProducto(Producto producto) {
        log.info("Creando nuevo producto: {}", producto.getNombre());
        
        if (producto.getSku() != null && 
            productoRepository.existsBySkuAndEmpresaId(producto.getSku(), producto.getEmpresa().getId())) {
            throw new BusinessException("Ya existe un producto con el SKU: " + producto.getSku());
        }
        
        return productoRepository.save(producto);
    }

    /**
     * Obtener producto por ID
     */
    @Transactional(readOnly = true)
    public Producto obtenerProductoPorId(UUID productoId) {
        return productoRepository.findById(productoId)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + productoId));
    }

    /**
     * Obtener productos por empresa
     */
    @Transactional(readOnly = true)
    public List<Producto> obtenerProductosPorEmpresa(UUID empresaId) {
        return productoRepository.findByEmpresaIdAndActiveTrue(empresaId);
    }

    /**
     * Obtener productos con stock bajo
     */
    @Transactional(readOnly = true)
    public List<Producto> obtenerProductosConStockBajo(UUID empresaId) {
        return productoRepository.findProductosConStockBajo(empresaId);
    }

    /**
     * Actualizar stock
     */
    public Producto actualizarStock(UUID productoId, Integer nuevoStock) {
        Producto producto = obtenerProductoPorId(productoId);
        producto.setStockActual(nuevoStock);
        return productoRepository.save(producto);
    }

    /**
     * Reducir stock
     */
    public Producto reducirStock(UUID productoId, Integer cantidad) {
        Producto producto = obtenerProductoPorId(productoId);
        
        if (!producto.tieneStock(cantidad)) {
            throw new BusinessException("Stock insuficiente");
        }
        
        producto.reducirStock(cantidad);
        return productoRepository.save(producto);
    }
}
