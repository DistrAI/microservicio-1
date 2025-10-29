package com.sw2.gestorapi.domain.repositories;

import com.sw2.gestorapi.domain.entities.RutaEntrega;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RutaEntregaRepository extends JpaRepository<RutaEntrega, UUID> {

    /**
     * Busca entregas por ruta
     */
    List<RutaEntrega> findByRutaIdAndActiveTrueOrderByOrdenEntrega(UUID rutaId);

    /**
     * Busca entregas por pedido
     */
    List<RutaEntrega> findByPedidoIdAndActiveTrue(UUID pedidoId);

    /**
     * Busca entregas por estado
     */
    @Query("SELECT re FROM RutaEntrega re WHERE re.ruta.id = :rutaId AND " +
           "re.estado = :estado AND re.active = true ORDER BY re.ordenEntrega")
    List<RutaEntrega> findByRutaIdAndEstado(@Param("rutaId") UUID rutaId, 
                                            @Param("estado") RutaEntrega.EstadoEntrega estado);

    /**
     * Cuenta entregas completadas por ruta
     */
    @Query("SELECT COUNT(re) FROM RutaEntrega re WHERE re.ruta.id = :rutaId AND " +
           "re.estado = 'ENTREGADO' AND re.active = true")
    Long countEntregasCompletadasByRuta(@Param("rutaId") UUID rutaId);
}
