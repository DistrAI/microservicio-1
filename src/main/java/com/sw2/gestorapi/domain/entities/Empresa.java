package com.sw2.gestorapi.domain.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "empresas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Empresa extends BaseEntity {

    @NotBlank(message = "El nombre de la empresa es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @NotBlank(message = "El RUC/NIT es obligatorio")
    @Size(max = 20, message = "El RUC/NIT no puede exceder 20 caracteres")
    @Column(name = "ruc_nit", nullable = false, unique = true, length = 20)
    private String rucNit;

    @Email(message = "El email debe tener un formato válido")
    @Size(max = 100, message = "El email no puede exceder 100 caracteres")
    @Column(name = "email", length = 100)
    private String email;

    @Size(max = 20, message = "El teléfono no puede exceder 20 caracteres")
    @Column(name = "telefono", length = 20)
    private String telefono;

    @Size(max = 200, message = "La dirección no puede exceder 200 caracteres")
    @Column(name = "direccion", length = 200)
    private String direccion;

    @Size(max = 100, message = "La ciudad no puede exceder 100 caracteres")
    @Column(name = "ciudad", length = 100)
    private String ciudad;

    @Size(max = 100, message = "El país no puede exceder 100 caracteres")
    @Column(name = "pais", length = 100)
    private String pais;

    @Enumerated(EnumType.STRING)
    @Column(name = "plan_suscripcion", nullable = false)
    @Builder.Default
    private PlanSuscripcion planSuscripcion = PlanSuscripcion.BASICO;

    @Column(name = "limite_productos")
    private Integer limiteProductos;

    @Column(name = "limite_clientes")
    private Integer limiteClientes;

    @Column(name = "limite_usuarios")
    private Integer limiteUsuarios;

    @OneToMany(mappedBy = "empresa", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<Usuario> usuarios = new HashSet<>();

    @OneToMany(mappedBy = "empresa", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<Producto> productos = new HashSet<>();

    @OneToMany(mappedBy = "empresa", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<Cliente> clientes = new HashSet<>();

    @OneToMany(mappedBy = "empresa", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<Repartidor> repartidores = new HashSet<>();

    @OneToMany(mappedBy = "empresa", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<Ruta> rutas = new HashSet<>();

    public enum PlanSuscripcion {
        BASICO,
        PROFESIONAL,
        EMPRESARIAL
    }
}
