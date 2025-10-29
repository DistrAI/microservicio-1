package com.sw2.gestorapi.application.services;

import com.sw2.gestorapi.domain.entities.Empresa;
import com.sw2.gestorapi.domain.repositories.EmpresaRepository;
import com.sw2.gestorapi.shared.exceptions.ResourceNotFoundException;
import com.sw2.gestorapi.shared.exceptions.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class EmpresaService {

    private final EmpresaRepository empresaRepository;

    /**
     * Crear una nueva empresa
     */
    public Empresa crearEmpresa(Empresa empresa) {
        log.info("Creando nueva empresa: {}", empresa.getNombre());
        
        // Validar que no exista una empresa con el mismo RUC/NIT
        if (empresaRepository.existsByRucNit(empresa.getRucNit())) {
            throw new BusinessException("Ya existe una empresa con el RUC/NIT: " + empresa.getRucNit());
        }
        
        // Establecer límites según el plan
        establecerLimitesPorPlan(empresa);
        
        Empresa empresaGuardada = empresaRepository.save(empresa);
        log.info("Empresa creada exitosamente con ID: {}", empresaGuardada.getId());
        
        return empresaGuardada;
    }

    /**
     * Obtener empresa por ID
     */
    @Transactional(readOnly = true)
    public Empresa obtenerEmpresaPorId(UUID empresaId) {
        return empresaRepository.findById(empresaId)
                .orElseThrow(() -> new ResourceNotFoundException("Empresa no encontrada con ID: " + empresaId));
    }

    /**
     * Obtener empresa por RUC/NIT
     */
    @Transactional(readOnly = true)
    public Optional<Empresa> obtenerEmpresaPorRucNit(String rucNit) {
        return empresaRepository.findByRucNit(rucNit);
    }

    /**
     * Actualizar empresa
     */
    public Empresa actualizarEmpresa(UUID empresaId, Empresa empresaActualizada) {
        log.info("Actualizando empresa con ID: {}", empresaId);
        
        Empresa empresaExistente = obtenerEmpresaPorId(empresaId);
        
        // Validar cambio de RUC/NIT
        if (!empresaExistente.getRucNit().equals(empresaActualizada.getRucNit())) {
            if (empresaRepository.existsByRucNit(empresaActualizada.getRucNit())) {
                throw new BusinessException("Ya existe una empresa con el RUC/NIT: " + empresaActualizada.getRucNit());
            }
        }
        
        // Actualizar campos
        empresaExistente.setNombre(empresaActualizada.getNombre());
        empresaExistente.setRucNit(empresaActualizada.getRucNit());
        empresaExistente.setEmail(empresaActualizada.getEmail());
        empresaExistente.setTelefono(empresaActualizada.getTelefono());
        empresaExistente.setDireccion(empresaActualizada.getDireccion());
        empresaExistente.setCiudad(empresaActualizada.getCiudad());
        empresaExistente.setPais(empresaActualizada.getPais());
        
        // Si cambió el plan, actualizar límites
        if (!empresaExistente.getPlanSuscripcion().equals(empresaActualizada.getPlanSuscripcion())) {
            empresaExistente.setPlanSuscripcion(empresaActualizada.getPlanSuscripcion());
            establecerLimitesPorPlan(empresaExistente);
        }
        
        return empresaRepository.save(empresaExistente);
    }

    /**
     * Cambiar plan de suscripción
     */
    public Empresa cambiarPlanSuscripcion(UUID empresaId, Empresa.PlanSuscripcion nuevoPlan) {
        log.info("Cambiando plan de suscripción para empresa ID: {} a plan: {}", empresaId, nuevoPlan);
        
        Empresa empresa = obtenerEmpresaPorId(empresaId);
        empresa.setPlanSuscripcion(nuevoPlan);
        establecerLimitesPorPlan(empresa);
        
        return empresaRepository.save(empresa);
    }

    /**
     * Obtener todas las empresas activas
     */
    @Transactional(readOnly = true)
    public List<Empresa> obtenerTodasLasEmpresas() {
        return empresaRepository.findAll().stream()
                .filter(empresa -> empresa.getActive())
                .toList();
    }

    /**
     * Obtener empresas por plan
     */
    @Transactional(readOnly = true)
    public List<Empresa> obtenerEmpresasPorPlan(Empresa.PlanSuscripcion plan) {
        return empresaRepository.findByPlanSuscripcion(plan);
    }

    /**
     * Desactivar empresa (soft delete)
     */
    public void desactivarEmpresa(UUID empresaId) {
        log.info("Desactivando empresa con ID: {}", empresaId);
        
        Empresa empresa = obtenerEmpresaPorId(empresaId);
        empresa.setActive(false);
        empresaRepository.save(empresa);
        
        log.info("Empresa desactivada exitosamente");
    }

    /**
     * Verificar límites de la empresa
     */
    @Transactional(readOnly = true)
    public boolean puedeAgregarProductos(UUID empresaId, int cantidadAdicional) {
        Empresa empresa = obtenerEmpresaPorId(empresaId);
        if (empresa.getLimiteProductos() == null) return true;
        
        // Aquí se podría consultar el número actual de productos
        // Por ahora retornamos true, se implementará cuando tengamos los contadores
        return true;
    }

    /**
     * Obtener estadísticas de la empresa
     */
    @Transactional(readOnly = true)
    public EmpresaEstadisticas obtenerEstadisticas(UUID empresaId) {
        Empresa empresa = obtenerEmpresaPorId(empresaId);
        
        return EmpresaEstadisticas.builder()
                .empresaId(empresaId)
                .nombreEmpresa(empresa.getNombre())
                .planSuscripcion(empresa.getPlanSuscripcion())
                .limiteProductos(empresa.getLimiteProductos())
                .limiteClientes(empresa.getLimiteClientes())
                .limiteUsuarios(empresa.getLimiteUsuarios())
                // Los contadores se implementarán cuando tengamos los otros servicios
                .build();
    }

    /**
     * Establecer límites según el plan de suscripción
     */
    private void establecerLimitesPorPlan(Empresa empresa) {
        switch (empresa.getPlanSuscripcion()) {
            case BASICO -> {
                empresa.setLimiteProductos(100);
                empresa.setLimiteClientes(500);
                empresa.setLimiteUsuarios(3);
            }
            case PROFESIONAL -> {
                empresa.setLimiteProductos(1000);
                empresa.setLimiteClientes(5000);
                empresa.setLimiteUsuarios(10);
            }
            case EMPRESARIAL -> {
                empresa.setLimiteProductos(null); // Ilimitado
                empresa.setLimiteClientes(null);  // Ilimitado
                empresa.setLimiteUsuarios(null);  // Ilimitado
            }
        }
    }

    /**
     * Clase para estadísticas de empresa
     */
    public static class EmpresaEstadisticas {
        private UUID empresaId;
        private String nombreEmpresa;
        private Empresa.PlanSuscripcion planSuscripcion;
        private Integer limiteProductos;
        private Integer limiteClientes;
        private Integer limiteUsuarios;
        private Long totalProductos;
        private Long totalClientes;
        private Long totalUsuarios;

        public static EmpresaEstadisticasBuilder builder() {
            return new EmpresaEstadisticasBuilder();
        }

        public static class EmpresaEstadisticasBuilder {
            private UUID empresaId;
            private String nombreEmpresa;
            private Empresa.PlanSuscripcion planSuscripcion;
            private Integer limiteProductos;
            private Integer limiteClientes;
            private Integer limiteUsuarios;

            public EmpresaEstadisticasBuilder empresaId(UUID empresaId) {
                this.empresaId = empresaId;
                return this;
            }

            public EmpresaEstadisticasBuilder nombreEmpresa(String nombreEmpresa) {
                this.nombreEmpresa = nombreEmpresa;
                return this;
            }

            public EmpresaEstadisticasBuilder planSuscripcion(Empresa.PlanSuscripcion planSuscripcion) {
                this.planSuscripcion = planSuscripcion;
                return this;
            }

            public EmpresaEstadisticasBuilder limiteProductos(Integer limiteProductos) {
                this.limiteProductos = limiteProductos;
                return this;
            }

            public EmpresaEstadisticasBuilder limiteClientes(Integer limiteClientes) {
                this.limiteClientes = limiteClientes;
                return this;
            }

            public EmpresaEstadisticasBuilder limiteUsuarios(Integer limiteUsuarios) {
                this.limiteUsuarios = limiteUsuarios;
                return this;
            }

            public EmpresaEstadisticas build() {
                EmpresaEstadisticas estadisticas = new EmpresaEstadisticas();
                estadisticas.empresaId = this.empresaId;
                estadisticas.nombreEmpresa = this.nombreEmpresa;
                estadisticas.planSuscripcion = this.planSuscripcion;
                estadisticas.limiteProductos = this.limiteProductos;
                estadisticas.limiteClientes = this.limiteClientes;
                estadisticas.limiteUsuarios = this.limiteUsuarios;
                return estadisticas;
            }
        }
    }
}
