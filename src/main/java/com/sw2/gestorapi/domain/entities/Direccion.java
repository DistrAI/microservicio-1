package com.sw2.gestorapi.domain.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Table(name = "direcciones")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Direccion extends BaseEntity {

    @NotBlank(message = "La dirección es obligatoria")
    @Size(max = 200, message = "La dirección no puede exceder 200 caracteres")
    @Column(name = "direccion", nullable = false, length = 200)
    private String direccion;

    @Size(max = 100, message = "La ciudad no puede exceder 100 caracteres")
    @Column(name = "ciudad", length = 100)
    private String ciudad;

    @Size(max = 100, message = "El departamento no puede exceder 100 caracteres")
    @Column(name = "departamento", length = 100)
    private String departamento;

    @Size(max = 100, message = "El país no puede exceder 100 caracteres")
    @Column(name = "pais", length = 100)
    private String pais;

    @Size(max = 10, message = "El código postal no puede exceder 10 caracteres")
    @Column(name = "codigo_postal", length = 10)
    private String codigoPostal;

    @Column(name = "latitud")
    private Double latitud;

    @Column(name = "longitud")
    private Double longitud;

    @Column(name = "es_principal", nullable = false)
    @Builder.Default
    private Boolean esPrincipal = false;

    @Size(max = 200, message = "Las referencias no pueden exceder 200 caracteres")
    @Column(name = "referencias", length = 200)
    private String referencias;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "repartidor_id")
    private Repartidor repartidor;

    public String getDireccionCompleta() {
        StringBuilder sb = new StringBuilder();
        sb.append(direccion);
        if (ciudad != null) sb.append(", ").append(ciudad);
        if (departamento != null) sb.append(", ").append(departamento);
        if (pais != null) sb.append(", ").append(pais);
        return sb.toString();
    }

    public boolean tieneCoordenadasGPS() {
        return latitud != null && longitud != null;
    }
}
