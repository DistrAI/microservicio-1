package com.sw2.gestorapi.application.services;

import com.sw2.gestorapi.domain.entities.Usuario;
import com.sw2.gestorapi.domain.repositories.UsuarioRepository;
import com.sw2.gestorapi.shared.exceptions.ResourceNotFoundException;
import com.sw2.gestorapi.shared.exceptions.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Crear un nuevo usuario
     */
    public Usuario crearUsuario(Usuario usuario) {
        log.info("Creando nuevo usuario: {}", usuario.getEmail());
        
        // Validar email único
        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            throw new BusinessException("Ya existe un usuario con el email: " + usuario.getEmail());
        }
        
        // Encriptar contraseña
        if (usuario.getPassword() != null) {
            usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        }
        
        // Establecer rol por defecto si no se especifica
        if (usuario.getRol() == null) {
            usuario.setRol(Usuario.Rol.EMPLEADO);
        }
        
        Usuario usuarioGuardado = usuarioRepository.save(usuario);
        log.info("Usuario creado exitosamente con ID: {}", usuarioGuardado.getId());
        
        return usuarioGuardado;
    }

    /**
     * Obtener usuario por ID
     */
    @Transactional(readOnly = true)
    public Usuario obtenerUsuarioPorId(UUID usuarioId) {
        return usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + usuarioId));
    }

    /**
     * Obtener usuario por email
     */
    @Transactional(readOnly = true)
    public Optional<Usuario> obtenerUsuarioPorEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    /**
     * Obtener usuarios por empresa
     */
    @Transactional(readOnly = true)
    public List<Usuario> obtenerUsuariosPorEmpresa(UUID empresaId) {
        return usuarioRepository.findByEmpresaIdAndActiveTrue(empresaId);
    }

    /**
     * Obtener usuarios por rol
     */
    @Transactional(readOnly = true)
    public List<Usuario> obtenerUsuariosPorRol(UUID empresaId, Usuario.Rol rol) {
        return usuarioRepository.findByEmpresaIdAndRol(empresaId, rol);
    }

    /**
     * Obtener administradores de una empresa
     */
    @Transactional(readOnly = true)
    public List<Usuario> obtenerAdministradores(UUID empresaId) {
        return usuarioRepository.findAdministradoresByEmpresa(empresaId);
    }

    /**
     * Buscar usuarios por nombre
     */
    @Transactional(readOnly = true)
    public List<Usuario> buscarUsuariosPorNombre(UUID empresaId, String termino) {
        return usuarioRepository.findByEmpresaIdAndNombreOrApellidoContaining(empresaId, termino);
    }

    /**
     * Actualizar usuario
     */
    public Usuario actualizarUsuario(UUID usuarioId, Usuario usuarioActualizado) {
        log.info("Actualizando usuario con ID: {}", usuarioId);
        
        Usuario usuarioExistente = obtenerUsuarioPorId(usuarioId);
        
        // Validar cambio de email
        if (!usuarioExistente.getEmail().equals(usuarioActualizado.getEmail())) {
            if (usuarioRepository.existsByEmail(usuarioActualizado.getEmail())) {
                throw new BusinessException("Ya existe un usuario con el email: " + usuarioActualizado.getEmail());
            }
        }
        
        // Actualizar campos (excepto password)
        usuarioExistente.setNombre(usuarioActualizado.getNombre());
        usuarioExistente.setApellido(usuarioActualizado.getApellido());
        usuarioExistente.setEmail(usuarioActualizado.getEmail());
        usuarioExistente.setTelefono(usuarioActualizado.getTelefono());
        usuarioExistente.setRol(usuarioActualizado.getRol());
        
        return usuarioRepository.save(usuarioExistente);
    }

    /**
     * Cambiar contraseña
     */
    public void cambiarPassword(UUID usuarioId, String passwordActual, String nuevaPassword) {
        log.info("Cambiando contraseña para usuario: {}", usuarioId);
        
        Usuario usuario = obtenerUsuarioPorId(usuarioId);
        
        // Verificar contraseña actual
        if (!passwordEncoder.matches(passwordActual, usuario.getPassword())) {
            throw new BusinessException("La contraseña actual es incorrecta");
        }
        
        // Actualizar contraseña
        usuario.setPassword(passwordEncoder.encode(nuevaPassword));
        usuarioRepository.save(usuario);
        
        log.info("Contraseña actualizada exitosamente");
    }

    /**
     * Resetear contraseña (solo para administradores)
     */
    public String resetearPassword(UUID usuarioId) {
        log.info("Reseteando contraseña para usuario: {}", usuarioId);
        
        Usuario usuario = obtenerUsuarioPorId(usuarioId);
        
        // Generar contraseña temporal
        String passwordTemporal = generarPasswordTemporal();
        usuario.setPassword(passwordEncoder.encode(passwordTemporal));
        usuarioRepository.save(usuario);
        
        log.info("Contraseña reseteada exitosamente");
        return passwordTemporal;
    }

    /**
     * Cambiar rol de usuario
     */
    public Usuario cambiarRol(UUID usuarioId, Usuario.Rol nuevoRol) {
        log.info("Cambiando rol del usuario {} a {}", usuarioId, nuevoRol);
        
        Usuario usuario = obtenerUsuarioPorId(usuarioId);
        usuario.setRol(nuevoRol);
        
        return usuarioRepository.save(usuario);
    }

    /**
     * Desactivar usuario
     */
    public void desactivarUsuario(UUID usuarioId) {
        log.info("Desactivando usuario con ID: {}", usuarioId);
        
        Usuario usuario = obtenerUsuarioPorId(usuarioId);
        usuario.setActive(false);
        usuarioRepository.save(usuario);
        
        log.info("Usuario desactivado exitosamente");
    }

    /**
     * Activar usuario
     */
    public void activarUsuario(UUID usuarioId) {
        log.info("Activando usuario con ID: {}", usuarioId);
        
        Usuario usuario = obtenerUsuarioPorId(usuarioId);
        usuario.setActive(true);
        usuarioRepository.save(usuario);
        
        log.info("Usuario activado exitosamente");
    }

    /**
     * Verificar si un usuario tiene permisos de administrador
     */
    @Transactional(readOnly = true)
    public boolean esAdministrador(UUID usuarioId) {
        Usuario usuario = obtenerUsuarioPorId(usuarioId);
        return usuario.getRol() == Usuario.Rol.ADMINISTRADOR;
    }

    /**
     * Verificar si un usuario puede gestionar otros usuarios
     */
    @Transactional(readOnly = true)
    public boolean puedeGestionarUsuarios(UUID usuarioId) {
        Usuario usuario = obtenerUsuarioPorId(usuarioId);
        return usuario.getRol() == Usuario.Rol.ADMINISTRADOR || usuario.getRol() == Usuario.Rol.GERENTE;
    }

    /**
     * Obtener estadísticas de usuarios
     */
    @Transactional(readOnly = true)
    public UsuarioEstadisticas obtenerEstadisticas(UUID empresaId) {
        Long totalUsuarios = usuarioRepository.countByEmpresaId(empresaId);
        List<Usuario> administradores = obtenerAdministradores(empresaId);
        List<Usuario> gerentes = obtenerUsuariosPorRol(empresaId, Usuario.Rol.GERENTE);
        List<Usuario> empleados = obtenerUsuariosPorRol(empresaId, Usuario.Rol.EMPLEADO);
        
        return UsuarioEstadisticas.builder()
                .totalUsuarios(totalUsuarios.intValue())
                .administradores(administradores.size())
                .gerentes(gerentes.size())
                .empleados(empleados.size())
                .build();
    }

    /**
     * Generar contraseña temporal
     */
    private String generarPasswordTemporal() {
        // Generar una contraseña temporal de 8 caracteres
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder password = new StringBuilder();
        
        for (int i = 0; i < 8; i++) {
            int index = (int) (Math.random() * chars.length());
            password.append(chars.charAt(index));
        }
        
        return password.toString();
    }

    /**
     * Clase para estadísticas de usuarios
     */
    public static class UsuarioEstadisticas {
        private Integer totalUsuarios;
        private Integer administradores;
        private Integer gerentes;
        private Integer empleados;

        public static UsuarioEstadisticasBuilder builder() {
            return new UsuarioEstadisticasBuilder();
        }

        public static class UsuarioEstadisticasBuilder {
            private Integer totalUsuarios;
            private Integer administradores;
            private Integer gerentes;
            private Integer empleados;

            public UsuarioEstadisticasBuilder totalUsuarios(Integer totalUsuarios) {
                this.totalUsuarios = totalUsuarios;
                return this;
            }

            public UsuarioEstadisticasBuilder administradores(Integer administradores) {
                this.administradores = administradores;
                return this;
            }

            public UsuarioEstadisticasBuilder gerentes(Integer gerentes) {
                this.gerentes = gerentes;
                return this;
            }

            public UsuarioEstadisticasBuilder empleados(Integer empleados) {
                this.empleados = empleados;
                return this;
            }

            public UsuarioEstadisticas build() {
                UsuarioEstadisticas estadisticas = new UsuarioEstadisticas();
                estadisticas.totalUsuarios = this.totalUsuarios;
                estadisticas.administradores = this.administradores;
                estadisticas.gerentes = this.gerentes;
                estadisticas.empleados = this.empleados;
                return estadisticas;
            }
        }
    }
}
