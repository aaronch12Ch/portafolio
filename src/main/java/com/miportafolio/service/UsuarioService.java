package com.miportafolio.service;

import com.miportafolio.dto.UsuarioRegistroDTO;
import com.miportafolio.model.Rol;
import com.miportafolio.model.RolNombre;
import com.miportafolio.model.Usuarios;
import com.miportafolio.repository.RolRepository;
import com.miportafolio.repository.UsuarioRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@AllArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;

    public Usuarios registrarUsuario(UsuarioRegistroDTO usuarioRegistroDTO) {
        // Validación de correo existente ya manejada en el controlador, pero es buena práctica en el servicio.

        // Crear la entidad Usuario
        Usuarios nuevoUsuario = new Usuarios();
        nuevoUsuario.setNombreUsuario(usuarioRegistroDTO.getNombreUsuario());
        nuevoUsuario.setCorreo(usuarioRegistroDTO.getCorreo());

        // Encriptar la contraseña usando BCrypt antes de guardarla.
        nuevoUsuario.setContrasena(passwordEncoder.encode(usuarioRegistroDTO.getContrasena()));

        // Asignar el rol por defecto (USUARIO_NORMAL)
        Rol rolNormal = rolRepository.findByNombre(RolNombre.USUARIO_NORMAL)
                .orElseThrow(() -> new RuntimeException("Error: Rol USUARIO_NORMAL no encontrado."));

        nuevoUsuario.setRoles(Collections.singleton(rolNormal));

        return usuarioRepository.save(nuevoUsuario);
    }

    /**
     * Verifica si un correo ya existe en la base de datos.
     * @param correo Correo a verificar.
     * @return true si el correo existe, false si no.
     */
    public boolean existePorCorreo(String correo) {
        return usuarioRepository.existsByCorreo(correo);
    }
}
