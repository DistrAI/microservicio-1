package com.sw2.gestorapi.application.services;

import com.sw2.gestorapi.domain.entities.Ruta;
import com.sw2.gestorapi.domain.entities.RutaEntrega;
import com.sw2.gestorapi.domain.entities.Pedido;
import com.sw2.gestorapi.domain.entities.Repartidor;
import com.sw2.gestorapi.domain.repositories.RutaRepository;
import com.sw2.gestorapi.domain.repositories.RutaEntregaRepository;
import com.sw2.gestorapi.domain.repositories.RepartidorRepository;
import com.sw2.gestorapi.shared.exceptions.ResourceNotFoundException;
import com.sw2.gestorapi.shared.exceptions.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class RutaService {

    private final RutaRepository rutaRepository;
    private final RutaEntregaRepository rutaEntregaRepository;
    private final RepartidorRepository repartidorRepository;

    /**
     * Crear una nueva ruta
     */
    public Ruta crearRuta(Ruta ruta) {
        log.info("Creando nueva ruta para repartidor: {}", ruta.getRepartidor().getId());
        
        // Verificar que el repartidor esté disponible
        if (ruta.getRepartidor().getEstado() != com.sw2.gestorapi.domain.entities.Repartidor.EstadoRepartidor.DISPONIBLE) {
            throw new BusinessException("El repartidor no está disponible para asignar una nueva ruta");
        }
        
        ruta.setEstado(Ruta.EstadoRuta.PLANIFICADA);
        if (ruta.getFechaRuta() == null) {
            ruta.setFechaRuta(LocalDate.now());
        }
        
        Ruta rutaGuardada = rutaRepository.save(ruta);
        log.info("Ruta creada exitosamente con ID: {}", rutaGuardada.getId());
        
        return rutaGuardada;
    }

    /**
     * Obtener ruta por ID
     */
    @Transactional(readOnly = true)
    public Ruta obtenerRutaPorId(UUID rutaId) {
        return rutaRepository.findById(rutaId)
                .orElseThrow(() -> new ResourceNotFoundException("Ruta no encontrada con ID: " + rutaId));
    }

    /**
     * Obtener rutas por empresa
     */
    @Transactional(readOnly = true)
    public List<Ruta> obtenerRutasPorEmpresa(UUID empresaId) {
        return rutaRepository.findByEmpresaId(empresaId);
    }

    /**
     * Obtener rutas de hoy
     */
    @Transactional(readOnly = true)
    public List<Ruta> obtenerRutasDeHoy(UUID empresaId) {
        return rutaRepository.findRutasDeHoy(empresaId);
    }

    /**
     * Obtener rutas por estado
     */
    @Transactional(readOnly = true)
    public List<Ruta> obtenerRutasPorEstado(UUID empresaId, Ruta.EstadoRuta estado) {
        return rutaRepository.findByEmpresaIdAndEstado(empresaId, estado);
    }

    /**
     * Iniciar ruta
     */
    public Ruta iniciarRuta(UUID rutaId) {
        log.info("Iniciando ruta: {}", rutaId);
        
        Ruta ruta = obtenerRutaPorId(rutaId);
        
        if (ruta.getEstado() != Ruta.EstadoRuta.PLANIFICADA) {
            throw new BusinessException("Solo se pueden iniciar rutas planificadas");
        }
        
        // Cambiar estado del repartidor a EN_RUTA
        Repartidor repartidor = repartidorRepository.findById(ruta.getRepartidor().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Repartidor no encontrado"));
        repartidor.setEstado(Repartidor.EstadoRepartidor.EN_RUTA);
        repartidorRepository.save(repartidor);
        
        ruta.iniciarRuta();
        return rutaRepository.save(ruta);
    }

    /**
     * Completar ruta
     */
    public Ruta completarRuta(UUID rutaId) {
        log.info("Completando ruta: {}", rutaId);
        
        Ruta ruta = obtenerRutaPorId(rutaId);
        
        if (ruta.getEstado() != Ruta.EstadoRuta.EN_PROGRESO) {
            throw new BusinessException("Solo se pueden completar rutas en progreso");
        }
        
        // Verificar que todas las entregas estén completadas
        List<RutaEntrega> entregasPendientes = rutaEntregaRepository.findByRutaIdAndEstado(
                rutaId, RutaEntrega.EstadoEntrega.PENDIENTE);
        
        if (!entregasPendientes.isEmpty()) {
            throw new BusinessException("No se puede completar la ruta. Hay entregas pendientes");
        }
        
        // Cambiar estado del repartidor a DISPONIBLE
        Repartidor repartidor = repartidorRepository.findById(ruta.getRepartidor().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Repartidor no encontrado"));
        repartidor.setEstado(Repartidor.EstadoRepartidor.DISPONIBLE);
        repartidorRepository.save(repartidor);
        
        ruta.completarRuta();
        return rutaRepository.save(ruta);
    }

    /**
     * Pausar ruta
     */
    public Ruta pausarRuta(UUID rutaId) {
        log.info("Pausando ruta: {}", rutaId);
        
        Ruta ruta = obtenerRutaPorId(rutaId);
        
        if (ruta.getEstado() != Ruta.EstadoRuta.EN_PROGRESO) {
            throw new BusinessException("Solo se pueden pausar rutas en progreso");
        }
        
        ruta.setEstado(Ruta.EstadoRuta.PAUSADA);
        return rutaRepository.save(ruta);
    }

    /**
     * Reanudar ruta
     */
    public Ruta reanudarRuta(UUID rutaId) {
        log.info("Reanudando ruta: {}", rutaId);
        
        Ruta ruta = obtenerRutaPorId(rutaId);
        
        if (ruta.getEstado() != Ruta.EstadoRuta.PAUSADA) {
            throw new BusinessException("Solo se pueden reanudar rutas pausadas");
        }
        
        ruta.setEstado(Ruta.EstadoRuta.EN_PROGRESO);
        return rutaRepository.save(ruta);
    }

    /**
     * Cancelar ruta
     */
    public Ruta cancelarRuta(UUID rutaId) {
        log.info("Cancelando ruta: {}", rutaId);
        
        Ruta ruta = obtenerRutaPorId(rutaId);
        
        if (ruta.getEstado() == Ruta.EstadoRuta.COMPLETADA) {
            throw new BusinessException("No se puede cancelar una ruta completada");
        }
        
        // Cambiar estado del repartidor a DISPONIBLE
        Repartidor repartidor = repartidorRepository.findById(ruta.getRepartidor().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Repartidor no encontrado"));
        repartidor.setEstado(Repartidor.EstadoRepartidor.DISPONIBLE);
        repartidorRepository.save(repartidor);
        
        ruta.setEstado(Ruta.EstadoRuta.CANCELADA);
        return rutaRepository.save(ruta);
    }

    /**
     * Agregar entrega a ruta
     */
    public RutaEntrega agregarEntregaARuta(UUID rutaId, Pedido pedido, Integer ordenEntrega) {
        log.info("Agregando entrega del pedido {} a la ruta {}", pedido.getId(), rutaId);
        
        Ruta ruta = obtenerRutaPorId(rutaId);
        
        if (ruta.getEstado() != Ruta.EstadoRuta.PLANIFICADA) {
            throw new BusinessException("Solo se pueden agregar entregas a rutas planificadas");
        }
        
        RutaEntrega rutaEntrega = RutaEntrega.builder()
                .ruta(ruta)
                .pedido(pedido)
                .ordenEntrega(ordenEntrega)
                .estado(RutaEntrega.EstadoEntrega.PENDIENTE)
                .fechaEntregaEstimada(LocalDateTime.now().plusHours(2))
                .build();
        
        return rutaEntregaRepository.save(rutaEntrega);
    }

    /**
     * Obtener rutas activas de un repartidor
     */
    @Transactional(readOnly = true)
    public List<Ruta> obtenerRutasActivasPorRepartidor(UUID repartidorId) {
        return rutaRepository.findRutasActivasByRepartidor(repartidorId);
    }

    /**
     * Optimizar ruta (algoritmo básico)
     */
    public Ruta optimizarRuta(UUID rutaId) {
        log.info("Optimizando ruta: {}", rutaId);
        
        Ruta ruta = obtenerRutaPorId(rutaId);
        List<RutaEntrega> entregas = rutaEntregaRepository.findByRutaIdAndActiveTrueOrderByOrdenEntrega(rutaId);
        
        if (entregas.isEmpty()) {
            throw new BusinessException("No hay entregas para optimizar en esta ruta");
        }
        
        // Algoritmo básico de optimización (se puede mejorar con algoritmos más sofisticados)
        double distanciaTotal = calcularDistanciaTotal(entregas);
        int tiempoEstimado = calcularTiempoEstimado(entregas);
        
        ruta.setDistanciaTotalKm(distanciaTotal);
        ruta.setTiempoEstimadoMinutos(tiempoEstimado);
        
        return rutaRepository.save(ruta);
    }

    /**
     * Obtener estadísticas de rutas
     */
    @Transactional(readOnly = true)
    public RutaEstadisticas obtenerEstadisticas(UUID empresaId) {
        Long rutasPlanificadas = rutaRepository.countByEmpresaIdAndEstado(empresaId, Ruta.EstadoRuta.PLANIFICADA);
        Long rutasEnProgreso = rutaRepository.countByEmpresaIdAndEstado(empresaId, Ruta.EstadoRuta.EN_PROGRESO);
        Long rutasCompletadas = rutaRepository.countByEmpresaIdAndEstado(empresaId, Ruta.EstadoRuta.COMPLETADA);
        Double distanciaTotal = rutaRepository.sumDistanciaTotalByEmpresaId(empresaId);
        
        return RutaEstadisticas.builder()
                .rutasPlanificadas(rutasPlanificadas.intValue())
                .rutasEnProgreso(rutasEnProgreso.intValue())
                .rutasCompletadas(rutasCompletadas.intValue())
                .distanciaTotalKm(distanciaTotal != null ? distanciaTotal : 0.0)
                .build();
    }

    /**
     * Calcular distancia total de la ruta (algoritmo básico)
     */
    private double calcularDistanciaTotal(List<RutaEntrega> entregas) {
        // Implementación básica - en producción se usaría una API de mapas
        return entregas.size() * 5.0; // 5 km promedio por entrega
    }

    /**
     * Calcular tiempo estimado de la ruta
     */
    private int calcularTiempoEstimado(List<RutaEntrega> entregas) {
        // Implementación básica - 30 minutos por entrega
        return entregas.size() * 30;
    }

    /**
     * Clase para estadísticas de rutas
     */
    public static class RutaEstadisticas {
        private Integer rutasPlanificadas;
        private Integer rutasEnProgreso;
        private Integer rutasCompletadas;
        private Double distanciaTotalKm;

        public static RutaEstadisticasBuilder builder() {
            return new RutaEstadisticasBuilder();
        }

        public static class RutaEstadisticasBuilder {
            private Integer rutasPlanificadas;
            private Integer rutasEnProgreso;
            private Integer rutasCompletadas;
            private Double distanciaTotalKm;

            public RutaEstadisticasBuilder rutasPlanificadas(Integer rutasPlanificadas) {
                this.rutasPlanificadas = rutasPlanificadas;
                return this;
            }

            public RutaEstadisticasBuilder rutasEnProgreso(Integer rutasEnProgreso) {
                this.rutasEnProgreso = rutasEnProgreso;
                return this;
            }

            public RutaEstadisticasBuilder rutasCompletadas(Integer rutasCompletadas) {
                this.rutasCompletadas = rutasCompletadas;
                return this;
            }

            public RutaEstadisticasBuilder distanciaTotalKm(Double distanciaTotalKm) {
                this.distanciaTotalKm = distanciaTotalKm;
                return this;
            }

            public RutaEstadisticas build() {
                RutaEstadisticas estadisticas = new RutaEstadisticas();
                estadisticas.rutasPlanificadas = this.rutasPlanificadas;
                estadisticas.rutasEnProgreso = this.rutasEnProgreso;
                estadisticas.rutasCompletadas = this.rutasCompletadas;
                estadisticas.distanciaTotalKm = this.distanciaTotalKm;
                return estadisticas;
            }
        }
    }
}
