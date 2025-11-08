package com.sw.GestorAPI.repository;

import com.sw.GestorAPI.entity.RutaEntrega;
import com.sw.GestorAPI.enums.EstadoRuta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RutaEntregaRepository extends JpaRepository<RutaEntrega, Long> {
    Page<RutaEntrega> findByActivoTrue(Pageable pageable);
    Page<RutaEntrega> findByRepartidorId(Long repartidorId, Pageable pageable);
    Page<RutaEntrega> findByEstado(EstadoRuta estado, Pageable pageable);
    Page<RutaEntrega> findByRepartidorIdAndEstado(Long repartidorId, EstadoRuta estado, Pageable pageable);
}
