package com.sw.GestorAPI.entity;

import com.sw.GestorAPI.enums.Rol;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Entidad que representa un usuario del sistema DistrIA
 * Puede ser ADMIN (acceso completo) o REPARTIDOR (acceso limitado a rutas)
 */
@Entity
@Table(name = "usuarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre completo es obligatorio")
    @Column(name = "nombre_completo", nullable = false, length = 100)
    private String nombreCompleto;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email debe ser válido")
    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    @Column(nullable = false)
    private String password;

    @NotNull(message = "El rol es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Rol rol;

    @Column(length = 20)
    private String telefono;

    // Campos de ubicación de la empresa/PYME
    @Column(name = "direccion_empresa", length = 500)
    private String direccionEmpresa;

    @Column(name = "latitud_empresa")
    private Double latitudEmpresa;

    @Column(name = "longitud_empresa")
    private Double longitudEmpresa;

    @Column(name = "nombre_empresa", length = 200)
    private String nombreEmpresa;

    @Column(nullable = false)
    @Builder.Default
    private Boolean activo = true;

    @CreationTimestamp
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "ultimo_acceso")
    private LocalDateTime ultimoAcceso;

    @UpdateTimestamp
    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    /**
     * Método helper para verificar si el usuario es administrador
     */
    @Transient
    public boolean isAdmin() {
        return this.rol == Rol.ADMIN;
    }

    /**
     * Método helper para verificar si el usuario es repartidor
     */
    @Transient
    public boolean isRepartidor() {
        return this.rol == Rol.REPARTIDOR;
    }

    /**
     * Actualiza la fecha del último acceso del usuario
     */
    public void actualizarUltimoAcceso() {
        this.ultimoAcceso = LocalDateTime.now();
    }
}
