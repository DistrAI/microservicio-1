package com.sw.GestorAPI.entity;

import com.sw.GestorAPI.enums.TipoMovimiento;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "movimientos_inventario", indexes = {
        @Index(name = "idx_movimiento_producto", columnList = "producto_id"),
        @Index(name = "idx_movimiento_fecha", columnList = "fecha_movimiento"),
        @Index(name = "idx_movimiento_pedido", columnList = "pedido_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovimientoInventario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "El producto es obligatorio")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @NotNull(message = "El tipo de movimiento es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoMovimiento tipo;

    @NotNull(message = "La cantidad es obligatoria")
    @Column(nullable = false)
    private Integer cantidad;

    @NotBlank(message = "El motivo es obligatorio")
    @Column(nullable = false, length = 255)
    private String motivo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id")
    private Pedido pedido; // Opcional: para movimientos relacionados con pedidos

    @CreationTimestamp
    @Column(name = "fecha_movimiento", nullable = false, updatable = false)
    private LocalDateTime fechaMovimiento;

    @NotNull(message = "La cantidad anterior es obligatoria")
    @Column(name = "cantidad_anterior", nullable = false)
    private Integer cantidadAnterior;

    @NotNull(message = "La cantidad nueva es obligatoria")
    @Column(name = "cantidad_nueva", nullable = false)
    private Integer cantidadNueva;

    // Método de conveniencia para obtener el delta según el tipo
    public Integer getDelta() {
        return switch (tipo) {
            case ENTRADA -> cantidad;
            case SALIDA -> -cantidad;
            case AJUSTE -> cantidad; // El ajuste puede ser positivo o negativo
        };
    }
}
