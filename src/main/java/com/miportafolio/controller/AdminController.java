package com.miportafolio.controller;


import com.miportafolio.service.LoginAttemptService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para operaciones de administración de usuarios.
 * Solo accesible para el rol ADMIN.
 */
@RestController
@RequestMapping("/api/admin/usuarios")
@AllArgsConstructor
public class AdminController {

    private final LoginAttemptService loginAttemptService;

    /**
     * Endpoint para que un ADMIN desbloquee una cuenta de usuario.
     * Requiere el rol 'ADMIN' para su ejecución.
     * @param correo Correo del usuario a desbloquear.
     * @return Mensaje de confirmación.
     */
    @PutMapping("/desbloquear/{correo}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<String> desbloquearUsuario(@PathVariable String correo) {
        loginAttemptService.desbloquearUsuario(correo);
        return ResponseEntity.ok("Usuario " + correo + " desbloqueado exitosamente. Intentos reseteados.");
    }
}
