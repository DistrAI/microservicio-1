package com.sw2.gestorapi.infrastructure.graphql;

import com.sw2.gestorapi.application.services.RutaService;
import com.sw2.gestorapi.domain.entities.Ruta;
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
public class RutaResolver {

    private final RutaService rutaService;

    @QueryMapping
    public Ruta ruta(@Argument String id) {
        log.info("Consultando ruta con ID: {}", id);
        return rutaService.obtenerRutaPorId(UUID.fromString(id));
    }

    @QueryMapping
    public List<Ruta> rutas(@Argument String empresaId) {
        log.info("Consultando rutas de empresa: {}", empresaId);
        return rutaService.obtenerRutasPorEmpresa(UUID.fromString(empresaId));
    }

    @QueryMapping
    public List<Ruta> rutasDeHoy(@Argument String empresaId) {
        log.info("Consultando rutas de hoy para empresa: {}", empresaId);
        return rutaService.obtenerRutasDeHoy(UUID.fromString(empresaId));
    }

    @QueryMapping
    public List<Ruta> rutasPorEstado(@Argument String empresaId, @Argument String estado) {
        log.info("Consultando rutas por estado {} en empresa: {}", estado, empresaId);
        Ruta.EstadoRuta estadoEnum = Ruta.EstadoRuta.valueOf(estado.toUpperCase());
        return rutaService.obtenerRutasPorEstado(UUID.fromString(empresaId), estadoEnum);
    }

    @QueryMapping
    public List<Ruta> rutasActivasPorRepartidor(@Argument String repartidorId) {
        log.info("Consultando rutas activas del repartidor: {}", repartidorId);
        return rutaService.obtenerRutasActivasPorRepartidor(UUID.fromString(repartidorId));
    }

    @MutationMapping
    public Ruta iniciarRuta(@Argument String rutaId) {
        log.info("Iniciando ruta: {}", rutaId);
        return rutaService.iniciarRuta(UUID.fromString(rutaId));
    }

    @MutationMapping
    public Ruta completarRuta(@Argument String rutaId) {
        log.info("Completando ruta: {}", rutaId);
        return rutaService.completarRuta(UUID.fromString(rutaId));
    }

    @MutationMapping
    public Ruta pausarRuta(@Argument String rutaId) {
        log.info("Pausando ruta: {}", rutaId);
        return rutaService.pausarRuta(UUID.fromString(rutaId));
    }

    @MutationMapping
    public Ruta reanudarRuta(@Argument String rutaId) {
        log.info("Reanudando ruta: {}", rutaId);
        return rutaService.reanudarRuta(UUID.fromString(rutaId));
    }

    @MutationMapping
    public Ruta cancelarRuta(@Argument String rutaId) {
        log.info("Cancelando ruta: {}", rutaId);
        return rutaService.cancelarRuta(UUID.fromString(rutaId));
    }

    @MutationMapping
    public Ruta optimizarRuta(@Argument String rutaId) {
        log.info("Optimizando ruta: {}", rutaId);
        return rutaService.optimizarRuta(UUID.fromString(rutaId));
    }
}
