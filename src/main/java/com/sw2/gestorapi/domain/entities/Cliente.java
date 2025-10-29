package com.sw2.gestorapi.domain.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "clientes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cliente extends BaseEntity {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 50, message = "El nombre no puede exceder 50 caracteres")
    @Column(name = "nombre", nullable = false, length = 50)
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    @Size(max = 50, message = "El apellido no puede exceder 50 caracteres")
    @Column(name = "apellido", nullable = false, length = 50)
    private String apellido;

    @Email(message = "El email debe tener un formato válido")
    @Size(max = 100, message = "El email no puede exceder 100 caracteres")
    @Column(name = "email", length = 100)
    private String email;

    @Size(max = 20, message = "El teléfono no puede exceder 20 caracteres")
    @Column(name = "telefono", length = 20)
    private String telefono;

    @Size(max = 20, message = "El documento no puede exceder 20 caracteres")
    @Column(name = "documento", length = 20)
    private String documento;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_documento")
    private TipoDocumento tipoDocumento;

    @Column(name = "fecha_nacimiento")
    private LocalDate fechaNacimiento;

    @Enumerated(EnumType.STRING)
    @Column(name = "segmento")
    @Builder.Default
    private SegmentoCliente segmento = SegmentoCliente.NUEVO;

    @Column(name = "preferencias_entrega", length = 500)
    private String preferenciasEntrega;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empresa_id", nullable = false)
    private Empresa empresa;

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<Pedido> pedidos = new HashSet<>();

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<Direccion> direcciones = new HashSet<>();

    public enum TipoDocumento {
        CI,
        PASAPORTE,
        RUC,
        NIT
    }

    public enum SegmentoCliente {
        NUEVO,
        FRECUENTE,
        VIP,
        INACTIVO
    }

    public String getNombreCompleto() {
        return nombre + " " + apellido;
    }

    public Direccion getDireccionPrincipal() {
        return direcciones.stream()
                .filter(Direccion::getEsPrincipal)
                .findFirst()
                .orElse(null);
    }
}
