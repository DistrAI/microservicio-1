package com.sw2.gestorapi.domain.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "pedido_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PedidoItem extends BaseEntity {

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser mayor a 0")
    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;

    @NotNull(message = "El precio unitario es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "El precio unitario debe ser mayor a 0")
    @Column(name = "precio_unitario", nullable = false)
    private BigDecimal precioUnitario;

    @DecimalMin(value = "0.0", message = "El descuento no puede ser negativo")
    @Column(name = "descuento_item")
    @Builder.Default
    private BigDecimal descuentoItem = BigDecimal.ZERO;

    @NotNull(message = "El pedido es obligatorio")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id", nullable = false)
    private Pedido pedido;

    @NotNull(message = "El producto es obligatorio")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    public BigDecimal getSubtotal() {
        BigDecimal subtotal = precioUnitario.multiply(BigDecimal.valueOf(cantidad));
        return subtotal.subtract(descuentoItem);
    }

    public BigDecimal getDescuentoTotal() {
        return descuentoItem.multiply(BigDecimal.valueOf(cantidad));
    }
}
