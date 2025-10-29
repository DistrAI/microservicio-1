package com.sw2.gestorapi.domain.repositories;

import com.sw2.gestorapi.domain.entities.Empresa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface EmpresaRepository extends JpaRepository<Empresa, UUID> {

    /**
     * Busca una empresa por su RUC/NIT
     */
    Optional<Empresa> findByRucNit(String rucNit);

    /**
     * Busca una empresa por email
     */
    Optional<Empresa> findByEmail(String email);

    /**
     * Verifica si existe una empresa con el RUC/NIT dado
     */
    boolean existsByRucNit(String rucNit);

    /**
     * Busca empresas por plan de suscripción
     */
    @Query("SELECT e FROM Empresa e WHERE e.planSuscripcion = :plan AND e.active = true")
    java.util.List<Empresa> findByPlanSuscripcion(@Param("plan") Empresa.PlanSuscripcion plan);

    /**
     * Busca empresas por ciudad
     */
    @Query("SELECT e FROM Empresa e WHERE LOWER(e.ciudad) LIKE LOWER(CONCAT('%', :ciudad, '%')) AND e.active = true")
    java.util.List<Empresa> findByCiudadContainingIgnoreCase(@Param("ciudad") String ciudad);

    /**
     * Cuenta el número total de empresas activas
     */
    @Query("SELECT COUNT(e) FROM Empresa e WHERE e.active = true")
    Long countActiveEmpresas();
}
