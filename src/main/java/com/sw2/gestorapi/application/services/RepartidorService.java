package com.sw2.gestorapi.application.services;

import com.sw2.gestorapi.domain.entities.Repartidor;
import com.sw2.gestorapi.domain.repositories.RepartidorRepository;
import com.sw2.gestorapi.shared.exceptions.ResourceNotFoundException;
import com.sw2.gestorapi.shared.exceptions.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class RepartidorService {

    private final RepartidorRepository repartidorRepository;

    /**
     * Crear un nuevo repartidor
     */
    public Repartidor crearRepartidor(Repartidor repartidor) {
        log.info("Creando nuevo repartidor: {} {}", repartidor.getNombre(), repartidor.getApellido());
        
        // Validar documento único por empresa
        if (repartidor.getDocumento() != null && 
            repartidorRepository.existsByDocumentoAndEmpresaId(repartidor.getDocumento(), repartidor.getEmpresa().getId())) {
            throw new BusinessException("Ya existe un repartidor con el documento: " + repartidor.getDocumento());
        }
        
        // Validar teléfono único por empresa
        if (repartidor.getTelefono() != null && 
            repartidorRepository.existsByTelefonoAndEmpresaId(repartidor.getTelefono(), repartidor.getEmpresa().getId())) {
            throw new BusinessException("Ya existe un repartidor con el teléfono: " + repartidor.getTelefono());
        }
        
        // Establecer estado inicial
        if (repartidor.getEstado() == null) {
            repartidor.setEstado(Repartidor.EstadoRepartidor.DISPONIBLE);
        }
        
        Repartidor repartidorGuardado = repartidorRepository.save(repartidor);
        log.info("Repartidor creado exitosamente con ID: {}", repartidorGuardado.getId());
        
        return repartidorGuardado;
    }

    /**
     * Obtener repartidor por ID
     */
    @Transactional(readOnly = true)
    public Repartidor obtenerRepartidorPorId(UUID repartidorId) {
        return repartidorRepository.findById(repartidorId)
                .orElseThrow(() -> new ResourceNotFoundException("Repartidor no encontrado con ID: " + repartidorId));
    }

    /**
     * Obtener repartidores por empresa
     */
    @Transactional(readOnly = true)
    public List<Repartidor> obtenerRepartidoresPorEmpresa(UUID empresaId) {
        return repartidorRepository.findByEmpresaIdAndActiveTrue(empresaId);
    }

    /**
     * Obtener repartidores disponibles
     */
    @Transactional(readOnly = true)
    public List<Repartidor> obtenerRepartidoresDisponibles(UUID empresaId) {
        return repartidorRepository.findRepartidoresDisponibles(empresaId);
    }

    /**
     * Obtener repartidores por tipo de vehículo
     */
    @Transactional(readOnly = true)
    public List<Repartidor> obtenerRepartidoresPorTipoVehiculo(UUID empresaId, Repartidor.TipoVehiculo tipoVehiculo) {
        return repartidorRepository.findByEmpresaIdAndTipoVehiculo(empresaId, tipoVehiculo);
    }

    /**
     * Actualizar estado del repartidor
     */
    public Repartidor actualizarEstado(UUID repartidorId, Repartidor.EstadoRepartidor nuevoEstado) {
        log.info("Actualizando estado del repartidor {} a {}", repartidorId, nuevoEstado);
        
        Repartidor repartidor = obtenerRepartidorPorId(repartidorId);
        repartidor.setEstado(nuevoEstado);
        
        return repartidorRepository.save(repartidor);
    }

    /**
     * Actualizar ubicación del repartidor
     */
    public Repartidor actualizarUbicacion(UUID repartidorId, Double latitud, Double longitud) {
        log.info("Actualizando ubicación del repartidor {} a [{}, {}]", repartidorId, latitud, longitud);
        
        Repartidor repartidor = obtenerRepartidorPorId(repartidorId);
        repartidor.setLatitudActual(latitud);
        repartidor.setLongitudActual(longitud);
        
        return repartidorRepository.save(repartidor);
    }

    /**
     * Buscar repartidores cercanos a una ubicación
     */
    @Transactional(readOnly = true)
    public List<Repartidor> buscarRepartidoresCercanos(UUID empresaId, Double latitud, Double longitud, Double radioKm) {
        log.info("Buscando repartidores cercanos a [{}, {}] en un radio de {} km", latitud, longitud, radioKm);
        return repartidorRepository.findRepartidoresCercanos(empresaId, latitud, longitud, radioKm);
    }

    /**
     * Asignar repartidor automáticamente basado en criterios
     */
    public Repartidor asignarRepartidorOptimo(UUID empresaId, Double pesoKg, Boolean requiereRefrigeracion, 
                                              Double latitudDestino, Double longitudDestino) {
        log.info("Buscando repartidor óptimo para entrega con peso: {} kg, refrigeración: {}", pesoKg, requiereRefrigeracion);
        
        List<Repartidor> repartidoresDisponibles = obtenerRepartidoresDisponibles(empresaId);
        
        if (repartidoresDisponibles.isEmpty()) {
            throw new BusinessException("No hay repartidores disponibles en este momento");
        }
        
        // Filtrar por capacidad de carga
        List<Repartidor> repartidoresConCapacidad = repartidoresDisponibles.stream()
                .filter(r -> r.getCapacidadCargaKg() == null || r.getCapacidadCargaKg() >= pesoKg)
                .toList();
        
        if (repartidoresConCapacidad.isEmpty()) {
            throw new BusinessException("No hay repartidores con capacidad suficiente para esta entrega");
        }
        
        // Si hay coordenadas de destino, buscar el más cercano
        if (latitudDestino != null && longitudDestino != null) {
            List<Repartidor> repartidoresCercanos = buscarRepartidoresCercanos(empresaId, latitudDestino, longitudDestino, 10.0);
            
            if (!repartidoresCercanos.isEmpty()) {
                return repartidoresCercanos.get(0); // El más cercano
            }
        }
        
        // Si no hay criterios de ubicación, devolver el primero disponible
        return repartidoresConCapacidad.get(0);
    }

    /**
     * Actualizar repartidor
     */
    public Repartidor actualizarRepartidor(UUID repartidorId, Repartidor repartidorActualizado) {
        log.info("Actualizando repartidor con ID: {}", repartidorId);
        
        Repartidor repartidorExistente = obtenerRepartidorPorId(repartidorId);
        
        // Actualizar campos
        repartidorExistente.setNombre(repartidorActualizado.getNombre());
        repartidorExistente.setApellido(repartidorActualizado.getApellido());
        repartidorExistente.setTelefono(repartidorActualizado.getTelefono());
        repartidorExistente.setLicenciaConducir(repartidorActualizado.getLicenciaConducir());
        repartidorExistente.setTipoVehiculo(repartidorActualizado.getTipoVehiculo());
        repartidorExistente.setPlacaVehiculo(repartidorActualizado.getPlacaVehiculo());
        repartidorExistente.setCapacidadCargaKg(repartidorActualizado.getCapacidadCargaKg());
        
        return repartidorRepository.save(repartidorExistente);
    }

    /**
     * Desactivar repartidor
     */
    public void desactivarRepartidor(UUID repartidorId) {
        log.info("Desactivando repartidor con ID: {}", repartidorId);
        
        Repartidor repartidor = obtenerRepartidorPorId(repartidorId);
        repartidor.setActive(false);
        repartidor.setEstado(Repartidor.EstadoRepartidor.INACTIVO);
        repartidorRepository.save(repartidor);
        
        log.info("Repartidor desactivado exitosamente");
    }

    /**
     * Obtener estadísticas de repartidores
     */
    @Transactional(readOnly = true)
    public RepartidorEstadisticas obtenerEstadisticas(UUID empresaId) {
        Long totalRepartidores = repartidorRepository.countByEmpresaIdAndEstado(empresaId, null);
        Long repartidoresDisponibles = repartidorRepository.countByEmpresaIdAndEstado(empresaId, Repartidor.EstadoRepartidor.DISPONIBLE);
        Long repartidoresEnRuta = repartidorRepository.countByEmpresaIdAndEstado(empresaId, Repartidor.EstadoRepartidor.EN_RUTA);
        
        return RepartidorEstadisticas.builder()
                .totalRepartidores(totalRepartidores.intValue())
                .repartidoresDisponibles(repartidoresDisponibles.intValue())
                .repartidoresEnRuta(repartidoresEnRuta.intValue())
                .build();
    }

    /**
     * Clase para estadísticas de repartidores
     */
    public static class RepartidorEstadisticas {
        private Integer totalRepartidores;
        private Integer repartidoresDisponibles;
        private Integer repartidoresEnRuta;

        public static RepartidorEstadisticasBuilder builder() {
            return new RepartidorEstadisticasBuilder();
        }

        public static class RepartidorEstadisticasBuilder {
            private Integer totalRepartidores;
            private Integer repartidoresDisponibles;
            private Integer repartidoresEnRuta;

            public RepartidorEstadisticasBuilder totalRepartidores(Integer totalRepartidores) {
                this.totalRepartidores = totalRepartidores;
                return this;
            }

            public RepartidorEstadisticasBuilder repartidoresDisponibles(Integer repartidoresDisponibles) {
                this.repartidoresDisponibles = repartidoresDisponibles;
                return this;
            }

            public RepartidorEstadisticasBuilder repartidoresEnRuta(Integer repartidoresEnRuta) {
                this.repartidoresEnRuta = repartidoresEnRuta;
                return this;
            }

            public RepartidorEstadisticas build() {
                RepartidorEstadisticas estadisticas = new RepartidorEstadisticas();
                estadisticas.totalRepartidores = this.totalRepartidores;
                estadisticas.repartidoresDisponibles = this.repartidoresDisponibles;
                estadisticas.repartidoresEnRuta = this.repartidoresEnRuta;
                return estadisticas;
            }
        }
    }
}
