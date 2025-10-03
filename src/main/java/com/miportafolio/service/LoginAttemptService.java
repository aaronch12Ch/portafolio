package com.miportafolio.service;

import com.miportafolio.model.Usuarios;
import com.miportafolio.repository.UsuarioRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;



/**
 * Servicio para gestionar la lógica de bloqueo de cuentas por fallos de autenticación.
 * La cuenta se bloquea después de 3 intentos fallidos.
 */
@Service
@AllArgsConstructor
public class LoginAttemptService {

    // Límite de intentos fallidos antes de bloquear la cuenta.
    private static final int MAX_ATTEMPTS = 3;

    private final UsuarioRepository usuarioRepository;

    /**
     * Registra un intento fallido de autenticación para un usuario.
     * Si los intentos superan el límite (3), bloquea la cuenta.
     * @param correo Correo electrónico del usuario.
     */
    public void registrarFallo(String correo) {
        usuarioRepository.findByCorreo(correo).ifPresent(usuario -> {
            usuario.setIntentosFallidos(usuario.getIntentosFallidos() + 1);
            if (usuario.getIntentosFallidos() >= MAX_ATTEMPTS) {
                usuario.setBloqueado(true);
            }
            usuarioRepository.save(usuario);
        });
    }
    /**
     * Registra un intento exitoso de autenticación, reseteando los intentos fallidos.
     * También desbloquea la cuenta si estaba bloqueada por fallos.
     * @param correo Correo electrónico del usuario.
     */
    public void registrarExito(String correo) {
        usuarioRepository.findByCorreo(correo).ifPresent(usuario -> {
            usuario.setIntentosFallidos(0);
            usuario.setBloqueado(false); // Desbloquea en caso de éxito
            usuarioRepository.save(usuario);
        });
    }

    /**
     * Desbloquea un usuario y resetea los intentos fallidos. Usado por el ADMIN.
     * @param correo Correo electrónico del usuario a desbloquear.
     */
    public void desbloquearUsuario(String correo) {
        usuarioRepository.findByCorreo(correo).ifPresent(usuario -> {
            usuario.setIntentosFallidos(0);
            usuario.setBloqueado(false);
            usuarioRepository.save(usuario);
        });
    }
    // Ejemplo de método necesario en LoginAttemptService
    public boolean estaBloqueado(String correo) {
        // Implementa la lógica para verificar si el usuario ha excedido los intentos
        // y si su cuenta debe considerarse bloqueada.

        // Esto podría involucrar verificar el contador de intentos en memoria o la base de datos.
        // O podrías delegar esta verificación al UserService, que revisaría el campo 'locked' o 'enabled'
        // en la entidad del usuario.

        // Por ejemplo:
        Usuarios usuario = usuarioRepository.findByCorreo(correo).orElse(null);
        if (usuario != null && !usuario.isEnabled() && usuario.getIntentosFallidos() >=  MAX_ATTEMPTS) {
            return true;
        }
        return false;
    }
}
