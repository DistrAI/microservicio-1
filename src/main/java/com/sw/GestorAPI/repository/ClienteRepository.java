package com.sw.GestorAPI.repository;

import com.sw.GestorAPI.entity.Cliente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    /**
     * Busca todos los clientes activos paginados
     */
    Page<Cliente> findByActivoTrue(Pageable pageable);

    /**
     * Busca clientes por nombre (búsqueda parcial, case insensitive) paginado
     */
    Page<Cliente> findByNombreContainingIgnoreCase(String nombre, Pageable pageable);

    /**
     * Busca un cliente por email
     */
    Optional<Cliente> findByEmail(String email);

    /**
     * Verifica si existe un cliente con el email dado
     */
    boolean existsByEmail(String email);
}
