package com.sw2.gestorapi.domain.repositories;

import com.sw2.gestorapi.domain.entities.Direccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DireccionRepository extends JpaRepository<Direccion, UUID> {

    /**
     * Busca direcciones por cliente
     */
    List<Direccion> findByClienteIdAndActiveTrue(UUID clienteId);

    /**
     * Busca la dirección principal de un cliente
     */
    @Query("SELECT d FROM Direccion d WHERE d.cliente.id = :clienteId AND " +
           "d.esPrincipal = true AND d.active = true")
    Optional<Direccion> findDireccionPrincipalByCliente(@Param("clienteId") UUID clienteId);

    /**
     * Busca direcciones por ciudad
     */
    @Query("SELECT d FROM Direccion d WHERE LOWER(d.ciudad) LIKE LOWER(CONCAT('%', :ciudad, '%')) AND d.active = true")
    List<Direccion> findByCiudadContainingIgnoreCase(@Param("ciudad") String ciudad);

    /**
     * Busca direcciones con coordenadas GPS
     */
    @Query("SELECT d FROM Direccion d WHERE d.latitud IS NOT NULL AND d.longitud IS NOT NULL AND d.active = true")
    List<Direccion> findDireccionesConGPS();
}
