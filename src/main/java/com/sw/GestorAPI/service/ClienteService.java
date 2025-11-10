package com.sw.GestorAPI.service;

import com.sw.GestorAPI.dto.ActualizarUbicacionClienteInput;
import com.sw.GestorAPI.entity.Cliente;
import com.sw.GestorAPI.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;

    public Page<Cliente> listar(@NonNull Pageable pageable) {
        return clienteRepository.findAll(pageable);
    }

    public Page<Cliente> listarActivos(@NonNull Pageable pageable) {
        return clienteRepository.findByActivoTrue(pageable);
    }

    public Page<Cliente> buscarPorNombre(@NonNull String nombre, @NonNull Pageable pageable) {
        return clienteRepository.findByNombreContainingIgnoreCase(nombre, pageable);
    }

    public Optional<Cliente> obtenerPorId(@NonNull Long id) {
        return clienteRepository.findById(id);
    }

    public Optional<Cliente> obtenerPorEmail(@NonNull String email) {
        return clienteRepository.findByEmail(email);
    }

    @Transactional
    public Cliente crearCliente(@NonNull Cliente cliente) {
        // Verificar que el email no exista
        if (clienteRepository.existsByEmail(cliente.getEmail())) {
            throw new IllegalArgumentException("Ya existe un cliente con el email: " + cliente.getEmail());
        }
        return clienteRepository.save(cliente);
    }

    @Transactional
    public Cliente actualizarCliente(@NonNull Long id, @NonNull Cliente datos) {
        Cliente existente = clienteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado con ID: " + id));

        // Verificar email único si se está cambiando
        if (datos.getEmail() != null && !datos.getEmail().equals(existente.getEmail())) {
            if (clienteRepository.existsByEmail(datos.getEmail())) {
                throw new IllegalArgumentException("Ya existe un cliente con el email: " + datos.getEmail());
            }
        }

        if (datos.getNombre() != null) existente.setNombre(datos.getNombre());
        if (datos.getEmail() != null) existente.setEmail(datos.getEmail());
        if (datos.getTelefono() != null) existente.setTelefono(datos.getTelefono());
        if (datos.getDireccion() != null) existente.setDireccion(datos.getDireccion());
        if (datos.getActivo() != null) existente.setActivo(datos.getActivo());

        return clienteRepository.save(existente);
    }

    @Transactional
    public Cliente desactivarCliente(@NonNull Long id) {
        Cliente existente = clienteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado con ID: " + id));
        existente.setActivo(false);
        return clienteRepository.save(existente);
    }

    @Transactional
    public Cliente activarCliente(@NonNull Long id) {
        Cliente existente = clienteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado con ID: " + id));
        existente.setActivo(true);
        return clienteRepository.save(existente);
    }

    @Transactional
    public void eliminarCliente(@NonNull Long id) {
        if (!clienteRepository.existsById(id)) {
            throw new IllegalArgumentException("Cliente no encontrado con ID: " + id);
        }
        clienteRepository.deleteById(id);
    }

    public Page<Cliente> pagina(int page, int size) {
        return listar(PageRequest.of(page, size));
    }

    @Transactional
    public Cliente actualizarUbicacionCliente(@NonNull Long id, @NonNull ActualizarUbicacionClienteInput input) {
        Cliente existente = clienteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado con ID: " + id));

        // Actualizar campos de ubicación
        existente.setDireccion(input.getDireccion());
        existente.setLatitudCliente(input.getLatitudCliente());
        existente.setLongitudCliente(input.getLongitudCliente());
        existente.setReferenciaDireccion(input.getReferenciaDireccion());

        return clienteRepository.save(existente);
    }
}
