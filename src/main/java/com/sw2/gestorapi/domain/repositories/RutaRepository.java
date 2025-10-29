package com.sw2.gestorapi.domain.repositories;

import com.sw2.gestorapi.domain.entities.Ruta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RutaRepository extends JpaRepository<Ruta, UUID> {

    /**
     * Busca una ruta por código
     */
    Optional<Ruta> findByCodigoRuta(String codigoRuta);

    /**
     * Busca rutas por repartidor
     */
    List<Ruta> findByRepartidorIdAndActiveTrue(UUID repartidorId);

    /**
     * Busca rutas por empresa (a través del repartidor)
     */
    @Query("SELECT r FROM Ruta r WHERE r.repartidor.empresa.id = :empresaId AND r.active = true")
    List<Ruta> findByEmpresaId(@Param("empresaId") UUID empresaId);

    /**
     * Busca rutas por estado
     */
    @Query("SELECT r FROM Ruta r WHERE r.repartidor.empresa.id = :empresaId AND " +
           "r.estado = :estado AND r.active = true")
    List<Ruta> findByEmpresaIdAndEstado(@Param("empresaId") UUID empresaId, 
                                        @Param("estado") Ruta.EstadoRuta estado);

    /**
     * Busca rutas planificadas
     */
    @Query("SELECT r FROM Ruta r WHERE r.repartidor.empresa.id = :empresaId AND " +
           "r.estado = 'PLANIFICADA' AND r.active = true")
    List<Ruta> findRutasPlanificadas(@Param("empresaId") UUID empresaId);

    /**
     * Busca rutas en progreso
     */
    @Query("SELECT r FROM Ruta r WHERE r.repartidor.empresa.id = :empresaId AND " +
           "r.estado = 'EN_PROGRESO' AND r.active = true")
    List<Ruta> findRutasEnProgreso(@Param("empresaId") UUID empresaId);

    /**
     * Busca rutas completadas
     */
    @Query("SELECT r FROM Ruta r WHERE r.repartidor.empresa.id = :empresaId AND " +
           "r.estado = 'COMPLETADA' AND r.active = true")
    List<Ruta> findRutasCompletadas(@Param("empresaId") UUID empresaId);

    /**
     * Busca rutas por fecha
     */
    @Query("SELECT r FROM Ruta r WHERE r.repartidor.empresa.id = :empresaId AND " +
           "r.fechaRuta = :fecha AND r.active = true")
    List<Ruta> findByEmpresaIdAndFechaRuta(@Param("empresaId") UUID empresaId, 
                                           @Param("fecha") LocalDate fecha);

    /**
     * Busca rutas de hoy
     */
    @Query("SELECT r FROM Ruta r WHERE r.repartidor.empresa.id = :empresaId AND " +
           "r.fechaRuta = CURRENT_DATE AND r.active = true")
    List<Ruta> findRutasDeHoy(@Param("empresaId") UUID empresaId);

    /**
     * Busca rutas por rango de fechas
     */
    @Query("SELECT r FROM Ruta r WHERE r.repartidor.empresa.id = :empresaId AND " +
           "r.fechaRuta BETWEEN :fechaInicio AND :fechaFin AND r.active = true")
    List<Ruta> findByEmpresaIdAndFechaRutaBetween(@Param("empresaId") UUID empresaId,
                                                  @Param("fechaInicio") LocalDate fechaInicio,
                                                  @Param("fechaFin") LocalDate fechaFin);

    /**
     * Busca rutas por repartidor y fecha
     */
    @Query("SELECT r FROM Ruta r WHERE r.repartidor.id = :repartidorId AND " +
           "r.fechaRuta = :fecha AND r.active = true")
    List<Ruta> findByRepartidorIdAndFechaRuta(@Param("repartidorId") UUID repartidorId, 
                                              @Param("fecha") LocalDate fecha);

    /**
     * Busca rutas activas de un repartidor (planificadas o en progreso)
     */
    @Query("SELECT r FROM Ruta r WHERE r.repartidor.id = :repartidorId AND " +
           "r.estado IN ('PLANIFICADA', 'EN_PROGRESO') AND r.active = true")
    List<Ruta> findRutasActivasByRepartidor(@Param("repartidorId") UUID repartidorId);

    /**
     * Cuenta rutas por estado
     */
    @Query("SELECT COUNT(r) FROM Ruta r WHERE r.repartidor.empresa.id = :empresaId AND " +
           "r.estado = :estado AND r.active = true")
    Long countByEmpresaIdAndEstado(@Param("empresaId") UUID empresaId, 
                                   @Param("estado") Ruta.EstadoRuta estado);

    /**
     * Suma distancia total de rutas completadas
     */
    @Query("SELECT COALESCE(SUM(r.distanciaTotalKm), 0) FROM Ruta r WHERE r.repartidor.empresa.id = :empresaId AND " +
           "r.estado = 'COMPLETADA' AND r.distanciaTotalKm IS NOT NULL AND r.active = true")
    Double sumDistanciaTotalByEmpresaId(@Param("empresaId") UUID empresaId);

    /**
     * Promedio de tiempo de rutas completadas
     */
    @Query("SELECT AVG(r.tiempoEstimadoMinutos) FROM Ruta r WHERE r.repartidor.empresa.id = :empresaId AND " +
           "r.estado = 'COMPLETADA' AND r.tiempoEstimadoMinutos IS NOT NULL AND r.active = true")
    Double avgTiempoEstimadoByEmpresaId(@Param("empresaId") UUID empresaId);

    /**
     * Verifica si existe una ruta con el código dado
     */
    boolean existsByCodigoRuta(String codigoRuta);
}
