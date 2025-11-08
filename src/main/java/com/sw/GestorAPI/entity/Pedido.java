package com.sw.GestorAPI.entity;

import com.sw.GestorAPI.enums.EstadoPedido;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pedidos", indexes = {
        @Index(name = "idx_pedido_cliente", columnList = "cliente_id"),
        @Index(name = "idx_pedido_estado", columnList = "estado"),
        @Index(name = "idx_pedido_fecha", columnList = "fecha_pedido")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "items") // Evitar recursión en toString
@EqualsAndHashCode(exclude = "items") // Evitar recursión en equals/hashCode
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "El cliente es obligatorio")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @NotNull(message = "El estado es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private EstadoPedido estado = EstadoPedido.PENDIENTE;

    @NotNull(message = "El total es obligatorio")
    @DecimalMin(value = "0.0", inclusive = true, message = "El total debe ser mayor o igual a 0")
    @Column(nullable = false, precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal total = BigDecimal.ZERO;

    @NotBlank(message = "La dirección de entrega es obligatoria")
    @Column(name = "direccion_entrega", nullable = false, length = 255)
    private String direccionEntrega;

    @Column(length = 500)
    private String observaciones;

    @Column(name = "fecha_entrega")
    private LocalDateTime fechaEntrega;

    @Builder.Default
    @Column(nullable = false)
    private Boolean activo = true;

    @CreationTimestamp
    @Column(name = "fecha_pedido", nullable = false, updatable = false)
    private LocalDateTime fechaPedido;

    @UpdateTimestamp
    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<ItemPedido> items = new ArrayList<>();

    // Métodos de conveniencia
    public void addItem(ItemPedido item) {
        items.add(item);
        item.setPedido(this);
        recalcularTotal();
    }

    public void removeItem(ItemPedido item) {
        items.remove(item);
        item.setPedido(null);
        recalcularTotal();
    }

    public void recalcularTotal() {
        this.total = items.stream()
                .map(ItemPedido::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
