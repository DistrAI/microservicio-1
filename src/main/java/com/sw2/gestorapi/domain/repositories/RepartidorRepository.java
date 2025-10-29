package com.sw2.gestorapi.domain.repositories;

import com.sw2.gestorapi.domain.entities.Repartidor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RepartidorRepository extends JpaRepository<Repartidor, UUID> {

    /**
     * Busca repartidores por empresa
     */
    List<Repartidor> findByEmpresaIdAndActiveTrue(UUID empresaId);

    /**
     * Busca un repartidor por documento
     */
    Optional<Repartidor> findByDocumentoAndEmpresaId(String documento, UUID empresaId);

    /**
     * Busca repartidores disponibles
     */
    @Query("SELECT r FROM Repartidor r WHERE r.empresa.id = :empresaId AND " +
           "r.estado = 'DISPONIBLE' AND r.active = true")
    List<Repartidor> findRepartidoresDisponibles(@Param("empresaId") UUID empresaId);

    /**
     * Busca repartidores por tipo de vehículo
     */
    @Query("SELECT r FROM Repartidor r WHERE r.empresa.id = :empresaId AND " +
           "r.tipoVehiculo = :tipoVehiculo AND r.active = true")
    List<Repartidor> findByEmpresaIdAndTipoVehiculo(@Param("empresaId") UUID empresaId,
                                                    @Param("tipoVehiculo") Repartidor.TipoVehiculo tipoVehiculo);

    /**
     * Cuenta repartidores por estado
     */
    @Query("SELECT COUNT(r) FROM Repartidor r WHERE r.empresa.id = :empresaId AND " +
           "r.estado = :estado AND r.active = true")
    Long countByEmpresaIdAndEstado(@Param("empresaId") UUID empresaId, 
                                   @Param("estado") Repartidor.EstadoRepartidor estado);

    /**
     * Verifica si existe un repartidor con el documento dado
     */
    boolean existsByDocumentoAndEmpresaId(String documento, UUID empresaId);
}
