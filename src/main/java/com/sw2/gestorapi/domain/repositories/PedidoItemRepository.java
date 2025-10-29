package com.sw2.gestorapi.domain.repositories;

import com.sw2.gestorapi.domain.entities.PedidoItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PedidoItemRepository extends JpaRepository<PedidoItem, UUID> {

    /**
     * Busca items por pedido
     */
    List<PedidoItem> findByPedidoIdAndActiveTrue(UUID pedidoId);

    /**
     * Busca items por producto
     */
    List<PedidoItem> findByProductoIdAndActiveTrue(UUID productoId);

    /**
     * Suma cantidad total de un producto vendido
     */
    @Query("SELECT COALESCE(SUM(pi.cantidad), 0) FROM PedidoItem pi WHERE pi.producto.id = :productoId AND " +
           "pi.pedido.estado = 'ENTREGADO' AND pi.active = true")
    Long sumCantidadVendidaByProducto(@Param("productoId") UUID productoId);
}
