package com.sw.GestorAPI.repository;

import com.sw.GestorAPI.entity.Inventario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InventarioRepository extends JpaRepository<Inventario, Long> {

    Optional<Inventario> findByProductoId(Long productoId);

    Page<Inventario> findByActivoTrue(Pageable pageable);

    @Query("SELECT i FROM Inventario i WHERE i.producto.nombre LIKE %:nombre% AND i.activo = true")
    Page<Inventario> findByProductoNombreContainingIgnoreCase(@Param("nombre") String nombre, Pageable pageable);

    @Query("SELECT i FROM Inventario i WHERE i.cantidad <= i.stockMinimo AND i.activo = true")
    Page<Inventario> findInventariosConStockBajo(Pageable pageable);

    boolean existsByProductoId(Long productoId);
}
