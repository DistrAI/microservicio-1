package com.sw2.gestorapi.domain.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "productos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Producto extends BaseEntity {

    @NotBlank(message = "El nombre del producto es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Size(max = 500, message = "La descripción no puede exceder 500 caracteres")
    @Column(name = "descripcion", length = 500)
    private String descripcion;

    @Size(max = 50, message = "El SKU no puede exceder 50 caracteres")
    @Column(name = "sku", unique = true, length = 50)
    private String sku;

    @DecimalMin(value = "0.0", inclusive = false, message = "El precio debe ser mayor a 0")
    @Column(name = "precio", nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;

    @DecimalMin(value = "0.0", message = "El costo no puede ser negativo")
    @Column(name = "costo", precision = 10, scale = 2)
    private BigDecimal costo;

    @Min(value = 0, message = "El stock no puede ser negativo")
    @Column(name = "stock_actual", nullable = false)
    @Builder.Default
    private Integer stockActual = 0;

    @Min(value = 0, message = "El stock mínimo no puede ser negativo")
    @Column(name = "stock_minimo")
    @Builder.Default
    private Integer stockMinimo = 0;

    @Size(max = 50, message = "La unidad de medida no puede exceder 50 caracteres")
    @Column(name = "unidad_medida", length = 50)
    private String unidadMedida;

    @Size(max = 100, message = "La categoría no puede exceder 100 caracteres")
    @Column(name = "categoria", length = 100)
    private String categoria;

    @Column(name = "peso_kg", precision = 8, scale = 3)
    private BigDecimal pesoKg;

    @Size(max = 200, message = "La URL de imagen no puede exceder 200 caracteres")
    @Column(name = "imagen_url", length = 200)
    private String imagenUrl;

    @Column(name = "requiere_refrigeracion")
    @Builder.Default
    private Boolean requiereRefrigeracion = false;

    @Column(name = "es_fragil")
    @Builder.Default
    private Boolean esFragil = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empresa_id", nullable = false)
    private Empresa empresa;

    @OneToMany(mappedBy = "producto", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<PedidoItem> pedidoItems = new HashSet<>();

    public boolean tieneStockBajo() {
        return stockActual <= stockMinimo;
    }

    public boolean tieneStock(Integer cantidad) {
        return stockActual >= cantidad;
    }

    public void reducirStock(Integer cantidad) {
        if (tieneStock(cantidad)) {
            this.stockActual -= cantidad;
        } else {
            throw new IllegalArgumentException("Stock insuficiente para el producto: " + nombre);
        }
    }

    public void aumentarStock(Integer cantidad) {
        this.stockActual += cantidad;
    }
}
