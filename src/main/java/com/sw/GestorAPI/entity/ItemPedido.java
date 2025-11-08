package com.sw.GestorAPI.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "items_pedido", indexes = {
        @Index(name = "idx_item_pedido", columnList = "pedido_id"),
        @Index(name = "idx_item_producto", columnList = "producto_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "pedido") // Evitar recursión en toString
@EqualsAndHashCode(exclude = "pedido") // Evitar recursión en equals/hashCode
public class ItemPedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "El pedido es obligatorio")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id", nullable = false)
    private Pedido pedido;

    @NotNull(message = "El producto es obligatorio")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser mayor a 0")
    @Column(nullable = false)
    private Integer cantidad;

    @NotNull(message = "El precio unitario es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "El precio unitario debe ser mayor a 0")
    @Column(name = "precio_unitario", nullable = false, precision = 12, scale = 2)
    private BigDecimal precioUnitario;

    @NotNull(message = "El subtotal es obligatorio")
    @DecimalMin(value = "0.0", inclusive = true, message = "El subtotal debe ser mayor o igual a 0")
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal subtotal;

    // Método para calcular subtotal automáticamente
    @PrePersist
    @PreUpdate
    public void calcularSubtotal() {
        if (cantidad != null && precioUnitario != null) {
            this.subtotal = precioUnitario.multiply(BigDecimal.valueOf(cantidad));
        }
    }

    // Método de conveniencia para establecer cantidad y recalcular
    public void setCantidadYRecalcular(Integer cantidad) {
        this.cantidad = cantidad;
        calcularSubtotal();
    }

    // Método de conveniencia para establecer precio y recalcular
    public void setPrecioUnitarioYRecalcular(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
        calcularSubtotal();
    }
}
