package com.sw2.gestorapi.domain.repositories;

import com.sw2.gestorapi.domain.entities.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, UUID> {

    /**
     * Busca un pedido por número de pedido
     */
    Optional<Pedido> findByNumeroPedido(String numeroPedido);

    /**
     * Busca pedidos por cliente
     */
    List<Pedido> findByClienteIdAndActiveTrue(UUID clienteId);

    /**
     * Busca pedidos por empresa (a través del cliente)
     */
    @Query("SELECT p FROM Pedido p WHERE p.cliente.empresa.id = :empresaId AND p.active = true")
    List<Pedido> findByEmpresaId(@Param("empresaId") UUID empresaId);

    /**
     * Busca pedidos por estado
     */
    @Query("SELECT p FROM Pedido p WHERE p.cliente.empresa.id = :empresaId AND " +
           "p.estado = :estado AND p.active = true")
    List<Pedido> findByEmpresaIdAndEstado(@Param("empresaId") UUID empresaId, 
                                          @Param("estado") Pedido.EstadoPedido estado);

    /**
     * Busca pedidos pendientes
     */
    @Query("SELECT p FROM Pedido p WHERE p.cliente.empresa.id = :empresaId AND " +
           "p.estado = 'PENDIENTE' AND p.active = true")
    List<Pedido> findPedidosPendientes(@Param("empresaId") UUID empresaId);

    /**
     * Busca pedidos en ruta
     */
    @Query("SELECT p FROM Pedido p WHERE p.cliente.empresa.id = :empresaId AND " +
           "p.estado = 'EN_RUTA' AND p.active = true")
    List<Pedido> findPedidosEnRuta(@Param("empresaId") UUID empresaId);

    /**
     * Busca pedidos entregados
     */
    @Query("SELECT p FROM Pedido p WHERE p.cliente.empresa.id = :empresaId AND " +
           "p.estado = 'ENTREGADO' AND p.active = true")
    List<Pedido> findPedidosEntregados(@Param("empresaId") UUID empresaId);

    /**
     * Busca pedidos por rango de fechas
     */
    @Query("SELECT p FROM Pedido p WHERE p.cliente.empresa.id = :empresaId AND " +
           "p.fechaPedido BETWEEN :fechaInicio AND :fechaFin AND p.active = true")
    List<Pedido> findByEmpresaIdAndFechaPedidoBetween(@Param("empresaId") UUID empresaId,
                                                      @Param("fechaInicio") LocalDateTime fechaInicio,
                                                      @Param("fechaFin") LocalDateTime fechaFin);

    /**
     * Busca pedidos por prioridad
     */
    @Query("SELECT p FROM Pedido p WHERE p.cliente.empresa.id = :empresaId AND " +
           "p.prioridad = :prioridad AND p.active = true ORDER BY p.fechaPedido ASC")
    List<Pedido> findByEmpresaIdAndPrioridad(@Param("empresaId") UUID empresaId,
                                             @Param("prioridad") Pedido.PrioridadPedido prioridad);

    /**
     * Busca pedidos urgentes
     */
    @Query("SELECT p FROM Pedido p WHERE p.cliente.empresa.id = :empresaId AND " +
           "p.prioridad = 'URGENTE' AND p.estado NOT IN ('ENTREGADO', 'CANCELADO') AND p.active = true " +
           "ORDER BY p.fechaPedido ASC")
    List<Pedido> findPedidosUrgentes(@Param("empresaId") UUID empresaId);

    /**
     * Busca pedidos por rango de totales
     */
    @Query("SELECT p FROM Pedido p WHERE p.cliente.empresa.id = :empresaId AND " +
           "p.total BETWEEN :totalMin AND :totalMax AND p.active = true")
    List<Pedido> findByEmpresaIdAndTotalBetween(@Param("empresaId") UUID empresaId,
                                                @Param("totalMin") BigDecimal totalMin,
                                                @Param("totalMax") BigDecimal totalMax);

    /**
     * Busca pedidos por método de pago
     */
    @Query("SELECT p FROM Pedido p WHERE p.cliente.empresa.id = :empresaId AND " +
           "p.metodoPago = :metodoPago AND p.active = true")
    List<Pedido> findByEmpresaIdAndMetodoPago(@Param("empresaId") UUID empresaId,
                                              @Param("metodoPago") Pedido.MetodoPago metodoPago);

    /**
     * Busca pedidos de hoy
     */
    @Query("SELECT p FROM Pedido p WHERE p.cliente.empresa.id = :empresaId AND " +
           "CAST(p.fechaPedido as date) = CAST(CURRENT_TIMESTAMP as date) AND p.active = true")
    List<Pedido> findPedidosDeHoy(@Param("empresaId") UUID empresaId);

    /**
     * Cuenta pedidos por estado
     */
    @Query("SELECT COUNT(p) FROM Pedido p WHERE p.cliente.empresa.id = :empresaId AND " +
           "p.estado = :estado AND p.active = true")
    Long countByEmpresaIdAndEstado(@Param("empresaId") UUID empresaId, 
                                   @Param("estado") Pedido.EstadoPedido estado);

    /**
     * Suma total de ventas por empresa
     */
    @Query("SELECT COALESCE(SUM(p.total), 0) FROM Pedido p WHERE p.cliente.empresa.id = :empresaId AND " +
           "p.estado = 'ENTREGADO' AND p.active = true")
    BigDecimal sumTotalVentasByEmpresaId(@Param("empresaId") UUID empresaId);

    /**
     * Suma total de ventas por empresa en un rango de fechas
     */
    @Query("SELECT COALESCE(SUM(p.total), 0) FROM Pedido p WHERE p.cliente.empresa.id = :empresaId AND " +
           "p.estado = 'ENTREGADO' AND p.fechaEntregaReal BETWEEN :fechaInicio AND :fechaFin AND p.active = true")
    BigDecimal sumTotalVentasByEmpresaIdAndFechaEntrega(@Param("empresaId") UUID empresaId,
                                                        @Param("fechaInicio") LocalDateTime fechaInicio,
                                                        @Param("fechaFin") LocalDateTime fechaFin);

    /**
     * Verifica si existe un pedido con el número dado
     */
    boolean existsByNumeroPedido(String numeroPedido);
}
