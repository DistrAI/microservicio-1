package com.sw.GestorAPI.entity;

import com.sw.GestorAPI.enums.EstadoRuta;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "rutas_entrega", indexes = {
        @Index(name = "idx_ruta_repartidor", columnList = "repartidor_id"),
        @Index(name = "idx_ruta_estado", columnList = "estado"),
        @Index(name = "idx_ruta_fecha", columnList = "fecha_ruta")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "pedidos")
@EqualsAndHashCode(exclude = "pedidos")
public class RutaEntrega {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "El repartidor es obligatorio")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "repartidor_id", nullable = false)
    private Usuario repartidor;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoRuta estado = EstadoRuta.PLANIFICADA;

    @NotNull(message = "La fecha de la ruta es obligatoria")
    @Column(name = "fecha_ruta", nullable = false)
    private LocalDate fechaRuta;

    @Column(name = "distancia_total_km")
    private Double distanciaTotalKm;

    @Column(name = "tiempo_estimado_min")
    private Integer tiempoEstimadoMin;

    @Builder.Default
    @Column(nullable = false)
    private Boolean activo = true;

    @CreationTimestamp
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @UpdateTimestamp
    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    @Builder.Default
    @ManyToMany
    @JoinTable(name = "ruta_pedidos",
            joinColumns = @JoinColumn(name = "ruta_id"),
            inverseJoinColumns = @JoinColumn(name = "pedido_id"))
    private List<Pedido> pedidos = new ArrayList<>();

    public void addPedido(Pedido p) {
        pedidos.add(p);
    }

    public void removePedido(Pedido p) {
        pedidos.remove(p);
    }
}
