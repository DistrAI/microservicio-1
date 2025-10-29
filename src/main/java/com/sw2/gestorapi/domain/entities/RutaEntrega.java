package com.sw2.gestorapi.domain.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "ruta_entregas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RutaEntrega extends BaseEntity {

    @NotNull(message = "El orden de entrega es obligatorio")
    @Column(name = "orden_entrega", nullable = false)
    private Integer ordenEntrega;

    @Column(name = "fecha_entrega_estimada")
    private LocalDateTime fechaEntregaEstimada;

    @Column(name = "fecha_entrega_real")
    private LocalDateTime fechaEntregaReal;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    @Builder.Default
    private EstadoEntrega estado = EstadoEntrega.PENDIENTE;

    @Size(max = 500, message = "Las observaciones no pueden exceder 500 caracteres")
    @Column(name = "observaciones", length = 500)
    private String observaciones;

    @Size(max = 200, message = "La URL de foto no puede exceder 200 caracteres")
    @Column(name = "foto_entrega_url", length = 200)
    private String fotoEntregaUrl;

    @Column(name = "latitud_entrega")
    private Double latitudEntrega;

    @Column(name = "longitud_entrega")
    private Double longitudEntrega;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ruta_id", nullable = false)
    private Ruta ruta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id", nullable = false)
    private Pedido pedido;

    public enum EstadoEntrega {
        PENDIENTE,
        EN_CAMINO,
        ENTREGADO,
        FALLIDO,
        REPROGRAMADO
    }

    public void marcarComoEntregado(Double latitud, Double longitud, String fotoUrl) {
        this.estado = EstadoEntrega.ENTREGADO;
        this.fechaEntregaReal = LocalDateTime.now();
        this.latitudEntrega = latitud;
        this.longitudEntrega = longitud;
        this.fotoEntregaUrl = fotoUrl;
    }

    public void marcarComoFallido(String observacion) {
        this.estado = EstadoEntrega.FALLIDO;
        this.observaciones = observacion;
    }

    public boolean estaEntregado() {
        return estado == EstadoEntrega.ENTREGADO;
    }

    public boolean estaFallido() {
        return estado == EstadoEntrega.FALLIDO;
    }
}
