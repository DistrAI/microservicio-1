package com.sw.GestorAPI.repository;

import com.sw.GestorAPI.entity.MovimientoInventario;
import com.sw.GestorAPI.enums.TipoMovimiento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MovimientoInventarioRepository extends JpaRepository<MovimientoInventario, Long> {

    Page<MovimientoInventario> findByProductoId(Long productoId, Pageable pageable);

    Page<MovimientoInventario> findByTipo(TipoMovimiento tipo, Pageable pageable);

    Page<MovimientoInventario> findByPedidoId(Long pedidoId, Pageable pageable);

    @Query("SELECT m FROM MovimientoInventario m WHERE m.fechaMovimiento BETWEEN :fechaInicio AND :fechaFin ORDER BY m.fechaMovimiento DESC")
    Page<MovimientoInventario> findByFechaMovimientoBetween(@Param("fechaInicio") LocalDateTime fechaInicio, 
                                                           @Param("fechaFin") LocalDateTime fechaFin, 
                                                           Pageable pageable);

    List<MovimientoInventario> findByPedidoIdOrderByFechaMovimientoDesc(Long pedidoId);
}
