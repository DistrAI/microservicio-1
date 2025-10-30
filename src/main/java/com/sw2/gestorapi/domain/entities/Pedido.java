package com.sw2.gestorapi.domain.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "pedidos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pedido extends BaseEntity {

    @Size(max = 50, message = "El número de pedido no puede exceder 50 caracteres")
    @Column(name = "numero_pedido", unique = true, length = 50)
    private String numeroPedido;

    @NotNull(message = "La fecha del pedido es obligatoria")
    @Column(name = "fecha_pedido", nullable = false)
    private LocalDateTime fechaPedido;

    @Column(name = "fecha_entrega_estimada")
    private LocalDateTime fechaEntregaEstimada;

    @Column(name = "fecha_entrega_real")
    private LocalDateTime fechaEntregaReal;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    @Builder.Default
    private EstadoPedido estado = EstadoPedido.PENDIENTE;

    @DecimalMin(value = "0.0", message = "El subtotal no puede ser negativo")
    @Column(name = "subtotal", nullable = false)
    @Builder.Default
    private BigDecimal subtotal = BigDecimal.ZERO;

    @DecimalMin(value = "0.0", message = "El descuento no puede ser negativo")
    @Column(name = "descuento")
    @Builder.Default
    private BigDecimal descuento = BigDecimal.ZERO;

    @DecimalMin(value = "0.0", message = "El costo de envío no puede ser negativo")
    @Column(name = "costo_envio")
    @Builder.Default
    private BigDecimal costoEnvio = BigDecimal.ZERO;

    @DecimalMin(value = "0.0", message = "El total no puede ser negativo")
    @Column(name = "total", nullable = false)
    @Builder.Default
    private BigDecimal total = BigDecimal.ZERO;

    @Size(max = 500, message = "Las observaciones no pueden exceder 500 caracteres")
    @Column(name = "observaciones", length = 500)
    private String observaciones;

    @Enumerated(EnumType.STRING)
    @Column(name = "metodo_pago")
    private MetodoPago metodoPago;

    @Enumerated(EnumType.STRING)
    @Column(name = "prioridad")
    @Builder.Default
    private PrioridadPedido prioridad = PrioridadPedido.NORMAL;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<PedidoItem> items = new HashSet<>();

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<RutaEntrega> rutaEntregas = new HashSet<>();

    public enum EstadoPedido {
        PENDIENTE,
        CONFIRMADO,
        PREPARANDO,
        EN_RUTA,
        ENTREGADO,
        CANCELADO,
        DEVUELTO
    }

    public enum MetodoPago {
        EFECTIVO,
        TARJETA_CREDITO,
        TARJETA_DEBITO,
        TRANSFERENCIA,
        QR_PAGO
    }

    public enum PrioridadPedido {
        BAJA,
        NORMAL,
        ALTA,
        URGENTE
    }

    @PrePersist
    public void generarNumeroPedido() {
        if (numeroPedido == null) {
            numeroPedido = "PED-" + System.currentTimeMillis();
        }
        if (fechaPedido == null) {
            fechaPedido = LocalDateTime.now();
        }
    }

    public void calcularTotal() {
        subtotal = items.stream()
                .map(item -> item.getPrecioUnitario().multiply(BigDecimal.valueOf(item.getCantidad())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        total = subtotal.subtract(descuento).add(costoEnvio);
    }

    public boolean puedeSerCancelado() {
        return estado == EstadoPedido.PENDIENTE || estado == EstadoPedido.CONFIRMADO;
    }

    public boolean estaEntregado() {
        return estado == EstadoPedido.ENTREGADO;
    }

    public int getCantidadTotalItems() {
        return items.stream().mapToInt(PedidoItem::getCantidad).sum();
    }
}
