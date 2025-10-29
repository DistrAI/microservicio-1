package com.sw2.gestorapi.domain.repositories;

import com.sw2.gestorapi.domain.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {

    /**
     * Busca un usuario por email (para autenticación)
     */
    Optional<Usuario> findByEmail(String email);

    /**
     * Busca un usuario por email y empresa
     */
    Optional<Usuario> findByEmailAndEmpresaId(String email, UUID empresaId);

    /**
     * Verifica si existe un usuario con el email dado
     */
    boolean existsByEmail(String email);

    /**
     * Busca usuarios por empresa
     */
    List<Usuario> findByEmpresaIdAndActiveTrue(UUID empresaId);

    /**
     * Busca usuarios por rol en una empresa específica
     */
    @Query("SELECT u FROM Usuario u WHERE u.empresa.id = :empresaId AND u.rol = :rol AND u.active = true")
    List<Usuario> findByEmpresaIdAndRol(@Param("empresaId") UUID empresaId, @Param("rol") Usuario.Rol rol);

    /**
     * Busca administradores de una empresa
     */
    @Query("SELECT u FROM Usuario u WHERE u.empresa.id = :empresaId AND u.rol = 'ADMINISTRADOR' AND u.active = true")
    List<Usuario> findAdministradoresByEmpresa(@Param("empresaId") UUID empresaId);

    /**
     * Busca usuarios por nombre o apellido
     */
    @Query("SELECT u FROM Usuario u WHERE u.empresa.id = :empresaId AND " +
           "(LOWER(u.nombre) LIKE LOWER(CONCAT('%', :termino, '%')) OR " +
           "LOWER(u.apellido) LIKE LOWER(CONCAT('%', :termino, '%'))) AND u.active = true")
    List<Usuario> findByEmpresaIdAndNombreOrApellidoContaining(@Param("empresaId") UUID empresaId, 
                                                               @Param("termino") String termino);

    /**
     * Cuenta usuarios por empresa
     */
    @Query("SELECT COUNT(u) FROM Usuario u WHERE u.empresa.id = :empresaId AND u.active = true")
    Long countByEmpresaId(@Param("empresaId") UUID empresaId);
}
