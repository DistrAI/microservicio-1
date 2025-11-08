package com.sw.GestorAPI.service;

import com.sw.GestorAPI.entity.Producto;
import com.sw.GestorAPI.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductoService {

    private final ProductoRepository productoRepository;

    public Page<Producto> listar(Pageable pageable) {
        return productoRepository.findAll(pageable);
    }

    public Page<Producto> listarActivos(Pageable pageable) {
        return productoRepository.findByActivoTrue(pageable);
    }

    public Page<Producto> buscarPorNombre(String nombre, Pageable pageable) {
        return productoRepository.findByNombreContainingIgnoreCase(nombre, pageable);
    }

    public Optional<Producto> obtenerPorId(Long id) {
        return productoRepository.findById(id);
    }

    public Optional<Producto> obtenerPorSku(String sku) {
        return productoRepository.findBySku(sku);
    }

    @Transactional
    public Producto crearProducto(Producto producto) {
        return productoRepository.save(producto);
    }

    @Transactional
    public Producto actualizarProducto(Long id, Producto datos) {
        Producto existente = productoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado con ID: " + id));

        if (datos.getNombre() != null) existente.setNombre(datos.getNombre());
        if (datos.getSku() != null) existente.setSku(datos.getSku());
        if (datos.getDescripcion() != null) existente.setDescripcion(datos.getDescripcion());
        if (datos.getPrecio() != null) existente.setPrecio(datos.getPrecio());
        if (datos.getActivo() != null) existente.setActivo(datos.getActivo());

        return productoRepository.save(existente);
    }

    @Transactional
    public Producto desactivarProducto(Long id) {
        Producto existente = productoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado con ID: " + id));
        existente.setActivo(false);
        return productoRepository.save(existente);
    }

    @Transactional
    public Producto activarProducto(Long id) {
        Producto existente = productoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado con ID: " + id));
        existente.setActivo(true);
        return productoRepository.save(existente);
    }

    @Transactional
    public void eliminarProducto(Long id) {
        if (!productoRepository.existsById(id)) {
            throw new IllegalArgumentException("Producto no encontrado con ID: " + id);
        }
        productoRepository.deleteById(id);
    }

    public Page<Producto> pagina(int page, int size) {
        return listar(PageRequest.of(page, size));
    }
}
