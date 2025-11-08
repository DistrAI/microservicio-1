package com.sw.GestorAPI.repository;

import com.sw.GestorAPI.entity.Pedido;
import com.sw.GestorAPI.enums.EstadoPedido;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    /**
     * Busca todos los pedidos activos paginados
     */
    Page<Pedido> findByActivoTrue(Pageable pageable);

    /**
     * Busca pedidos por estado paginado
     */
    Page<Pedido> findByEstado(EstadoPedido estado, Pageable pageable);

    /**
     * Busca pedidos por cliente paginado
     */
    Page<Pedido> findByClienteId(Long clienteId, Pageable pageable);

    /**
     * Busca pedidos por cliente y estado paginado
     */
    Page<Pedido> findByClienteIdAndEstado(Long clienteId, EstadoPedido estado, Pageable pageable);

    /**
     * Busca pedidos por rango de fechas
     */
    @Query("SELECT p FROM Pedido p WHERE p.fechaPedido BETWEEN :fechaInicio AND :fechaFin ORDER BY p.fechaPedido DESC")
    Page<Pedido> findByFechaPedidoBetween(@Param("fechaInicio") LocalDateTime fechaInicio, 
                                         @Param("fechaFin") LocalDateTime fechaFin, 
                                         Pageable pageable);

    /**
     * Busca pedidos pendientes o en proceso (para repartidores)
     */
    @Query("SELECT p FROM Pedido p WHERE p.estado IN ('PENDIENTE', 'EN_PROCESO', 'EN_CAMINO') AND p.activo = true")
    Page<Pedido> findPedidosEnProceso(Pageable pageable);

    /**
     * Cuenta pedidos por estado
     */
    long countByEstado(EstadoPedido estado);

    /**
     * Busca pedidos de un cliente específico ordenados por fecha
     */
    @Query("SELECT p FROM Pedido p WHERE p.cliente.id = :clienteId AND p.activo = true ORDER BY p.fechaPedido DESC")
    List<Pedido> findByClienteIdOrderByFechaPedidoDesc(@Param("clienteId") Long clienteId);
}
