package com.sw2.gestorapi.infrastructure.graphql;

import com.sw2.gestorapi.application.services.RepartidorService;
import com.sw2.gestorapi.domain.entities.Repartidor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
@Slf4j
public class RepartidorResolver {

    private final RepartidorService repartidorService;

    @QueryMapping
    public Repartidor repartidor(@Argument String id) {
        log.info("Consultando repartidor con ID: {}", id);
        return repartidorService.obtenerRepartidorPorId(UUID.fromString(id));
    }

    @QueryMapping
    public List<Repartidor> repartidores(@Argument String empresaId) {
        log.info("Consultando repartidores de empresa: {}", empresaId);
        return repartidorService.obtenerRepartidoresPorEmpresa(UUID.fromString(empresaId));
    }

    @QueryMapping
    public List<Repartidor> repartidoresDisponibles(@Argument String empresaId) {
        log.info("Consultando repartidores disponibles de empresa: {}", empresaId);
        return repartidorService.obtenerRepartidoresDisponibles(UUID.fromString(empresaId));
    }

    @MutationMapping
    public Repartidor actualizarEstadoRepartidor(@Argument String repartidorId, @Argument String nuevoEstado) {
        log.info("Actualizando estado del repartidor {} a {}", repartidorId, nuevoEstado);
        Repartidor.EstadoRepartidor estado = Repartidor.EstadoRepartidor.valueOf(nuevoEstado.toUpperCase());
        return repartidorService.actualizarEstado(UUID.fromString(repartidorId), estado);
    }

    @MutationMapping
    public Repartidor actualizarUbicacionRepartidor(@Argument String repartidorId, 
                                                    @Argument Double latitud, 
                                                    @Argument Double longitud) {
        log.info("Actualizando ubicación del repartidor {} a [{}, {}]", repartidorId, latitud, longitud);
        return repartidorService.actualizarUbicacion(UUID.fromString(repartidorId), latitud, longitud);
    }
}
