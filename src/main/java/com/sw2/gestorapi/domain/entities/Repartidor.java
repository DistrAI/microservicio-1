package com.sw2.gestorapi.domain.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "repartidores")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Repartidor extends BaseEntity {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 50, message = "El nombre no puede exceder 50 caracteres")
    @Column(name = "nombre", nullable = false, length = 50)
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    @Size(max = 50, message = "El apellido no puede exceder 50 caracteres")
    @Column(name = "apellido", nullable = false, length = 50)
    private String apellido;

    @Size(max = 20, message = "El teléfono no puede exceder 20 caracteres")
    @Column(name = "telefono", length = 20)
    private String telefono;

    @Size(max = 20, message = "El documento no puede exceder 20 caracteres")
    @Column(name = "documento", length = 20)
    private String documento;

    @Size(max = 20, message = "La licencia no puede exceder 20 caracteres")
    @Column(name = "licencia_conducir", length = 20)
    private String licenciaConducir;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_vehiculo")
    private TipoVehiculo tipoVehiculo;

    @Size(max = 20, message = "La placa no puede exceder 20 caracteres")
    @Column(name = "placa_vehiculo", length = 20)
    private String placaVehiculo;

    @Column(name = "capacidad_carga_kg", precision = 8, scale = 2)
    private Double capacidadCargaKg;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    @Builder.Default
    private EstadoRepartidor estado = EstadoRepartidor.DISPONIBLE;

    @Column(name = "latitud_actual")
    private Double latitudActual;

    @Column(name = "longitud_actual")
    private Double longitudActual;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empresa_id", nullable = false)
    private Empresa empresa;

    @OneToOne(mappedBy = "repartidor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Direccion direccion;

    @OneToMany(mappedBy = "repartidor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<Ruta> rutas = new HashSet<>();

    public enum TipoVehiculo {
        BICICLETA,
        MOTOCICLETA,
        AUTO,
        CAMIONETA,
        CAMION
    }

    public enum EstadoRepartidor {
        DISPONIBLE,
        EN_RUTA,
        OCUPADO,
        DESCANSO,
        INACTIVO
    }

    public String getNombreCompleto() {
        return nombre + " " + apellido;
    }

    public boolean estaDisponible() {
        return estado == EstadoRepartidor.DISPONIBLE;
    }

    public void actualizarUbicacion(Double latitud, Double longitud) {
        this.latitudActual = latitud;
        this.longitudActual = longitud;
    }
}
