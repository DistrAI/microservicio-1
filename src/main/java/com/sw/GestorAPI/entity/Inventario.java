package com.sw.GestorAPI.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "inventarios", indexes = {
        @Index(name = "idx_inventario_producto", columnList = "producto_id", unique = true),
        @Index(name = "idx_inventario_activo", columnList = "activo")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Inventario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "El producto es obligatorio")
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false, unique = true)
    private Producto producto;

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 0, message = "La cantidad no puede ser negativa")
    @Column(nullable = false)
    @Builder.Default
    private Integer cantidad = 0;

    @NotBlank(message = "La ubicación es obligatoria")
    @Column(nullable = false, length = 100)
    private String ubicacion;

    @NotNull(message = "El stock mínimo es obligatorio")
    @Min(value = 0, message = "El stock mínimo no puede ser negativo")
    @Column(name = "stock_minimo", nullable = false)
    @Builder.Default
    private Integer stockMinimo = 0;

    @Builder.Default
    @Column(nullable = false)
    private Boolean activo = true;

    @CreationTimestamp
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @UpdateTimestamp
    @Column(name = "fecha_ultima_actualizacion")
    private LocalDateTime fechaUltimaActualizacion;

    // Métodos de conveniencia
    public boolean tieneStockSuficiente(Integer cantidadRequerida) {
        return this.cantidad >= cantidadRequerida;
    }

    public boolean estaEnStockMinimo() {
        return this.cantidad <= this.stockMinimo;
    }

    public void ajustarCantidad(Integer delta) {
        this.cantidad += delta;
        if (this.cantidad < 0) {
            throw new IllegalArgumentException("La cantidad no puede ser negativa después del ajuste");
        }
    }
}
