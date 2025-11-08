package com.sw.GestorAPI.resolver;

import com.sw.GestorAPI.dto.ActualizarUsuarioInput;
import com.sw.GestorAPI.dto.CrearUsuarioInput;
import com.sw.GestorAPI.entity.Usuario;
import com.sw.GestorAPI.enums.Rol;
import com.sw.GestorAPI.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

import java.util.List;

/**
 * Resolver GraphQL para operaciones relacionadas con Usuario
 * Maneja todas las queries y mutations definidas en el schema
 */
@Controller
@RequiredArgsConstructor
@Slf4j
public class UsuarioResolver {

    private final UsuarioService usuarioService;

    // ===================================
    // QUERIES
    // ===================================

    /**
     * Obtiene todos los usuarios del sistema
     */
    @QueryMapping
    public List<Usuario> usuarios() {
        log.info("GraphQL Query: usuarios");
        return usuarioService.obtenerTodosLosUsuarios();
    }

    /**
     * Obtiene un usuario por su ID
     */
    @QueryMapping
    public Usuario usuario(@Argument Long id) {
        log.info("GraphQL Query: usuario con ID: {}", id);
        return usuarioService.obtenerUsuarioPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con ID: " + id));
    }

    /**
     * Obtiene un usuario por su email
     */
    @QueryMapping
    public Usuario usuarioPorEmail(@Argument String email) {
        log.info("GraphQL Query: usuarioPorEmail: {}", email);
        return usuarioService.obtenerUsuarioPorEmail(email)
                .orElse(null);
    }

    /**
     * Obtiene todos los usuarios activos
     */
    @QueryMapping
    public List<Usuario> usuariosActivos() {
        log.info("GraphQL Query: usuariosActivos");
        return usuarioService.obtenerUsuariosActivos();
    }

    /**
     * Obtiene usuarios filtrados por rol
     */
    @QueryMapping
    public List<Usuario> usuariosPorRol(@Argument Rol rol) {
        log.info("GraphQL Query: usuariosPorRol: {}", rol);
        return usuarioService.obtenerUsuariosPorRol(rol);
    }

    /**
     * Obtiene usuarios activos filtrados por rol
     */
    @QueryMapping
    public List<Usuario> usuariosActivosPorRol(@Argument Rol rol) {
        log.info("GraphQL Query: usuariosActivosPorRol: {}", rol);
        return usuarioService.obtenerUsuariosActivosPorRol(rol);
    }

    /**
     * Busca usuarios por nombre (búsqueda parcial)
     */
    @QueryMapping
    public List<Usuario> buscarUsuariosPorNombre(@Argument String nombre) {
        log.info("GraphQL Query: buscarUsuariosPorNombre: {}", nombre);
        return usuarioService.buscarUsuariosPorNombre(nombre);
    }

    /**
     * Devuelve el usuario autenticado según el token (SecurityContext)
     */
    @QueryMapping
    public Usuario me() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null) {
            return null;
        }
        String email = auth.getName();
        log.info("GraphQL Query: me -> {}", email);
        return usuarioService.obtenerUsuarioPorEmail(email).orElse(null);
    }

    // ===================================
    // MUTATIONS
    // ===================================

    /**
     * Crea un nuevo usuario
     */
    @MutationMapping
    public Usuario crearUsuario(@Argument CrearUsuarioInput input) {
        log.info("GraphQL Mutation: crearUsuario con email: {}", input.getEmail());

        Usuario nuevoUsuario = Usuario.builder()
                .nombreCompleto(input.getNombreCompleto())
                .email(input.getEmail())
                .password(input.getPassword())
                .rol(input.getRol())
                .telefono(input.getTelefono())
                .activo(true)
                .build();

        return usuarioService.crearUsuario(nuevoUsuario);
    }

    /**
     * Actualiza un usuario existente
     */
    @MutationMapping
    public Usuario actualizarUsuario(@Argument Long id, @Argument ActualizarUsuarioInput input) {
        log.info("GraphQL Mutation: actualizarUsuario ID: {}", id);

        Usuario usuarioActualizado = Usuario.builder()
                .nombreCompleto(input.getNombreCompleto())
                .email(input.getEmail())
                .password(input.getPassword())
                .rol(input.getRol())
                .telefono(input.getTelefono())
                .activo(input.getActivo())
                .build();

        return usuarioService.actualizarUsuario(id, usuarioActualizado);
    }

    /**
     * Desactiva un usuario (soft delete)
     */
    @MutationMapping
    public Usuario desactivarUsuario(@Argument Long id) {
        log.info("GraphQL Mutation: desactivarUsuario ID: {}", id);
        return usuarioService.desactivarUsuario(id);
    }

    /**
     * Activa un usuario previamente desactivado
     */
    @MutationMapping
    public Usuario activarUsuario(@Argument Long id) {
        log.info("GraphQL Mutation: activarUsuario ID: {}", id);
        return usuarioService.activarUsuario(id);
    }

    /**
     * Elimina permanentemente un usuario
     */
    @MutationMapping
    public Boolean eliminarUsuario(@Argument Long id) {
        log.info("GraphQL Mutation: eliminarUsuario ID: {}", id);
        usuarioService.eliminarUsuario(id);
        return true;
    }
}
