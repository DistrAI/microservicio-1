package com.sw.GestorAPI.repository;

import com.sw.GestorAPI.entity.Usuario;
import com.sw.GestorAPI.enums.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio JPA para la entidad Usuario
 * Proporciona métodos de consulta personalizados
 */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    /**
     * Busca un usuario por su email
     * @param email Email del usuario
     * @return Optional con el usuario si existe
     */
    Optional<Usuario> findByEmail(String email);

    /**
     * Verifica si existe un usuario con el email dado
     * @param email Email a verificar
     * @return true si existe, false si no
     */
    boolean existsByEmail(String email);

    /**
     * Busca todos los usuarios activos
     * @return Lista de usuarios activos
     */
    List<Usuario> findByActivoTrue();

    /**
     * Busca todos los usuarios por rol
     * @param rol Rol a filtrar
     * @return Lista de usuarios con ese rol
     */
    List<Usuario> findByRol(Rol rol);

    /**
     * Busca usuarios activos por rol
     * @param rol Rol a filtrar
     * @return Lista de usuarios activos con ese rol
     */
    List<Usuario> findByRolAndActivoTrue(Rol rol);

    /**
     * Busca usuarios por nombre completo (parcial, case insensitive)
     * @param nombre Nombre a buscar
     * @return Lista de usuarios que coinciden
     */
    List<Usuario> findByNombreCompletoContainingIgnoreCase(String nombre);
}
