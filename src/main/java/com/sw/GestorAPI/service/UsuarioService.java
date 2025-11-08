package com.sw.GestorAPI.service;

import com.sw.GestorAPI.entity.Usuario;
import com.sw.GestorAPI.enums.Rol;
import com.sw.GestorAPI.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Servicio para la gestión de usuarios
 * Contiene la lógica de negocio relacionada con usuarios
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Crea un nuevo usuario con password encriptado
     * 
     * @param usuario Usuario a crear
     * @return Usuario creado
     * @throws IllegalArgumentException si el email ya existe
     */
    @Transactional
    public Usuario crearUsuario(Usuario usuario) {
        log.info("Creando usuario con email: {}", usuario.getEmail());
        
        // Validar que el email no exista
        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            log.error("El email {} ya está registrado", usuario.getEmail());
            throw new IllegalArgumentException("El email ya está registrado");
        }

        // Encriptar password con BCrypt
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        
        Usuario usuarioGuardado = usuarioRepository.save(usuario);
        log.info("Usuario creado exitosamente con ID: {}", usuarioGuardado.getId());
        
        return usuarioGuardado;
    }

    /**
     * Actualiza un usuario existente
     * 
     * @param id ID del usuario a actualizar
     * @param usuarioActualizado Datos actualizados
     * @return Usuario actualizado
     * @throws IllegalArgumentException si el usuario no existe o el email ya está en uso
     */
    @Transactional
    public Usuario actualizarUsuario(Long id, Usuario usuarioActualizado) {
        log.info("Actualizando usuario con ID: {}", id);
        
        Usuario usuarioExistente = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con ID: " + id));

        // Validar email único si se está cambiando
        if (!usuarioExistente.getEmail().equals(usuarioActualizado.getEmail()) &&
                usuarioRepository.existsByEmail(usuarioActualizado.getEmail())) {
            throw new IllegalArgumentException("El email ya está registrado");
        }

        // Actualizar campos
        usuarioExistente.setNombreCompleto(usuarioActualizado.getNombreCompleto());
        usuarioExistente.setEmail(usuarioActualizado.getEmail());
        usuarioExistente.setTelefono(usuarioActualizado.getTelefono());
        usuarioExistente.setRol(usuarioActualizado.getRol());
        usuarioExistente.setActivo(usuarioActualizado.getActivo());

        // Solo actualizar password si se proporciona uno nuevo
        if (usuarioActualizado.getPassword() != null && !usuarioActualizado.getPassword().isEmpty()) {
            // Encriptar nuevo password
            usuarioExistente.setPassword(passwordEncoder.encode(usuarioActualizado.getPassword()));
        }

        Usuario guardado = usuarioRepository.save(usuarioExistente);
        log.info("Usuario actualizado exitosamente: {}", guardado.getEmail());
        
        return guardado;
    }

    /**
     * Obtiene un usuario por ID
     * 
     * @param id ID del usuario
     * @return Optional con el usuario si existe
     */
    public Optional<Usuario> obtenerUsuarioPorId(Long id) {
        log.debug("Buscando usuario con ID: {}", id);
        return usuarioRepository.findById(id);
    }

    /**
     * Obtiene un usuario por email
     * 
     * @param email Email del usuario
     * @return Optional con el usuario si existe
     */
    public Optional<Usuario> obtenerUsuarioPorEmail(String email) {
        log.debug("Buscando usuario con email: {}", email);
        return usuarioRepository.findByEmail(email);
    }

    /**
     * Obtiene todos los usuarios
     * 
     * @return Lista de todos los usuarios
     */
    public List<Usuario> obtenerTodosLosUsuarios() {
        log.debug("Obteniendo todos los usuarios");
        return usuarioRepository.findAll();
    }

    /**
     * Obtiene todos los usuarios activos
     * 
     * @return Lista de usuarios activos
     */
    public List<Usuario> obtenerUsuariosActivos() {
        log.debug("Obteniendo usuarios activos");
        return usuarioRepository.findByActivoTrue();
    }

    /**
     * Obtiene usuarios por rol
     * 
     * @param rol Rol a filtrar
     * @return Lista de usuarios con ese rol
     */
    public List<Usuario> obtenerUsuariosPorRol(Rol rol) {
        log.debug("Obteniendo usuarios con rol: {}", rol);
        return usuarioRepository.findByRol(rol);
    }

    /**
     * Obtiene usuarios activos por rol
     * 
     * @param rol Rol a filtrar
     * @return Lista de usuarios activos con ese rol
     */
    public List<Usuario> obtenerUsuariosActivosPorRol(Rol rol) {
        log.debug("Obteniendo usuarios activos con rol: {}", rol);
        return usuarioRepository.findByRolAndActivoTrue(rol);
    }

    /**
     * Busca usuarios por nombre
     * 
     * @param nombre Nombre a buscar (parcial)
     * @return Lista de usuarios que coinciden
     */
    public List<Usuario> buscarUsuariosPorNombre(String nombre) {
        log.debug("Buscando usuarios con nombre: {}", nombre);
        return usuarioRepository.findByNombreCompletoContainingIgnoreCase(nombre);
    }

    /**
     * Desactiva un usuario (soft delete)
     * 
     * @param id ID del usuario a desactivar
     * @return Usuario desactivado
     */
    @Transactional
    public Usuario desactivarUsuario(Long id) {
        log.info("Desactivando usuario con ID: {}", id);
        
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con ID: " + id));
        
        usuario.setActivo(false);
        Usuario guardado = usuarioRepository.save(usuario);
        
        log.info("Usuario desactivado: {}", guardado.getEmail());
        return guardado;
    }

    /**
     * Activa un usuario
     * 
     * @param id ID del usuario a activar
     * @return Usuario activado
     */
    @Transactional
    public Usuario activarUsuario(Long id) {
        log.info("Activando usuario con ID: {}", id);
        
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con ID: " + id));
        
        usuario.setActivo(true);
        Usuario guardado = usuarioRepository.save(usuario);
        
        log.info("Usuario activado: {}", guardado.getEmail());
        return guardado;
    }

    /**
     * Elimina un usuario permanentemente
     * ADVERTENCIA: Esta operación no se puede deshacer
     * 
     * @param id ID del usuario a eliminar
     */
    @Transactional
    public void eliminarUsuario(Long id) {
        log.warn("Eliminando permanentemente usuario con ID: {}", id);
        
        if (!usuarioRepository.existsById(id)) {
            throw new IllegalArgumentException("Usuario no encontrado con ID: " + id);
        }
        
        usuarioRepository.deleteById(id);
        log.warn("Usuario eliminado permanentemente con ID: {}", id);
    }

    /**
     * Actualiza la fecha de último acceso de un usuario
     * 
     * @param id ID del usuario
     */
    @Transactional
    public void actualizarUltimoAcceso(Long id) {
        log.debug("Actualizando último acceso para usuario ID: {}", id);
        
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con ID: " + id));
        
        usuario.actualizarUltimoAcceso();
        usuarioRepository.save(usuario);
    }
}
