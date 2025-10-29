package com.sw2.gestorapi.domain.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "rutas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ruta extends BaseEntity {

    @Size(max = 50, message = "El código de ruta no puede exceder 50 caracteres")
    @Column(name = "codigo_ruta", unique = true, length = 50)
    private String codigoRuta;

    @NotNull(message = "La fecha de la ruta es obligatoria")
    @Column(name = "fecha_ruta", nullable = false)
    private LocalDate fechaRuta;

    @Column(name = "hora_inicio")
    private LocalDateTime horaInicio;

    @Column(name = "hora_fin")
    private LocalDateTime horaFin;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    @Builder.Default
    private EstadoRuta estado = EstadoRuta.PLANIFICADA;

    @Column(name = "distancia_total_km", precision = 8, scale = 2)
    private Double distanciaTotalKm;

    @Column(name = "tiempo_estimado_minutos")
    private Integer tiempoEstimadoMinutos;

    @Size(max = 500, message = "Las observaciones no pueden exceder 500 caracteres")
    @Column(name = "observaciones", length = 500)
    private String observaciones;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "repartidor_id", nullable = false)
    private Repartidor repartidor;

    @OneToMany(mappedBy = "ruta", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<RutaEntrega> entregas = new HashSet<>();

    public enum EstadoRuta {
        PLANIFICADA,
        EN_PROGRESO,
        COMPLETADA,
        CANCELADA,
        PAUSADA
    }

    @PrePersist
    public void generarCodigoRuta() {
        if (codigoRuta == null) {
            codigoRuta = "RUT-" + System.currentTimeMillis();
        }
    }

    public void iniciarRuta() {
        this.estado = EstadoRuta.EN_PROGRESO;
        this.horaInicio = LocalDateTime.now();
    }

    public void completarRuta() {
        this.estado = EstadoRuta.COMPLETADA;
        this.horaFin = LocalDateTime.now();
    }

    public boolean estaEnProgreso() {
        return estado == EstadoRuta.EN_PROGRESO;
    }

    public boolean estaCompletada() {
        return estado == EstadoRuta.COMPLETADA;
    }

    public int getCantidadEntregas() {
        return entregas.size();
    }

    public int getEntregasCompletadas() {
        return (int) entregas.stream()
                .filter(entrega -> entrega.getEstado() == RutaEntrega.EstadoEntrega.ENTREGADO)
                .count();
    }
}
