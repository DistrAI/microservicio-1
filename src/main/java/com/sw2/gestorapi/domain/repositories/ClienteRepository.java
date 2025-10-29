package com.sw2.gestorapi.domain.repositories;

import com.sw2.gestorapi.domain.entities.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, UUID> {

    /**
     * Busca clientes por empresa
     */
    List<Cliente> findByEmpresaIdAndActiveTrue(UUID empresaId);

    /**
     * Busca un cliente por email
     */
    Optional<Cliente> findByEmailAndEmpresaId(String email, UUID empresaId);

    /**
     * Busca un cliente por documento
     */
    Optional<Cliente> findByDocumentoAndEmpresaId(String documento, UUID empresaId);

    /**
     * Busca clientes por segmento
     */
    @Query("SELECT c FROM Cliente c WHERE c.empresa.id = :empresaId AND " +
           "c.segmento = :segmento AND c.active = true")
    List<Cliente> findByEmpresaIdAndSegmento(@Param("empresaId") UUID empresaId, 
                                             @Param("segmento") Cliente.SegmentoCliente segmento);

    /**
     * Busca clientes VIP
     */
    @Query("SELECT c FROM Cliente c WHERE c.empresa.id = :empresaId AND " +
           "c.segmento = 'VIP' AND c.active = true")
    List<Cliente> findClientesVIP(@Param("empresaId") UUID empresaId);

    /**
     * Busca clientes por nombre o apellido
     */
    @Query("SELECT c FROM Cliente c WHERE c.empresa.id = :empresaId AND " +
           "(LOWER(c.nombre) LIKE LOWER(CONCAT('%', :termino, '%')) OR " +
           "LOWER(c.apellido) LIKE LOWER(CONCAT('%', :termino, '%'))) AND c.active = true")
    List<Cliente> findByEmpresaIdAndNombreOrApellidoContaining(@Param("empresaId") UUID empresaId, 
                                                               @Param("termino") String termino);

    /**
     * Busca clientes por teléfono
     */
    @Query("SELECT c FROM Cliente c WHERE c.empresa.id = :empresaId AND " +
           "c.telefono LIKE CONCAT('%', :telefono, '%') AND c.active = true")
    List<Cliente> findByEmpresaIdAndTelefonoContaining(@Param("empresaId") UUID empresaId, 
                                                       @Param("telefono") String telefono);

    /**
     * Busca clientes por rango de fechas de nacimiento
     */
    @Query("SELECT c FROM Cliente c WHERE c.empresa.id = :empresaId AND " +
           "c.fechaNacimiento BETWEEN :fechaInicio AND :fechaFin AND c.active = true")
    List<Cliente> findByEmpresaIdAndFechaNacimientoBetween(@Param("empresaId") UUID empresaId,
                                                           @Param("fechaInicio") LocalDate fechaInicio,
                                                           @Param("fechaFin") LocalDate fechaFin);

    /**
     * Busca clientes que cumplen años hoy
     */
    @Query("SELECT c FROM Cliente c WHERE c.empresa.id = :empresaId AND " +
           "MONTH(c.fechaNacimiento) = MONTH(:fecha) AND DAY(c.fechaNacimiento) = DAY(:fecha) AND c.active = true")
    List<Cliente> findClientesQueCumplenHoy(@Param("empresaId") UUID empresaId, 
                                            @Param("fecha") LocalDate fecha);

    /**
     * Cuenta clientes por empresa
     */
    @Query("SELECT COUNT(c) FROM Cliente c WHERE c.empresa.id = :empresaId AND c.active = true")
    Long countByEmpresaId(@Param("empresaId") UUID empresaId);

    /**
     * Cuenta clientes por segmento
     */
    @Query("SELECT COUNT(c) FROM Cliente c WHERE c.empresa.id = :empresaId AND " +
           "c.segmento = :segmento AND c.active = true")
    Long countByEmpresaIdAndSegmento(@Param("empresaId") UUID empresaId, 
                                     @Param("segmento") Cliente.SegmentoCliente segmento);

    /**
     * Verifica si existe un cliente con el email dado en la empresa
     */
    boolean existsByEmailAndEmpresaId(String email, UUID empresaId);

    /**
     * Verifica si existe un cliente con el documento dado en la empresa
     */
    boolean existsByDocumentoAndEmpresaId(String documento, UUID empresaId);
}
