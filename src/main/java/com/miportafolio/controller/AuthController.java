package com.miportafolio.controller;



import com.miportafolio.dto.JwtResponse;
import com.miportafolio.dto.LoginRequest;
import com.miportafolio.dto.UsuarioRegistroDTO;
import com.miportafolio.model.Usuarios;
import com.miportafolio.service.JwtService;
import com.miportafolio.service.LoginAttemptService;
import com.miportafolio.service.UsuarioService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para manejar las peticiones de autenticación:
 * registro de usuarios y login, incluyendo la lógica de bloqueo por intentos fallidos.
 */
@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {

    private final UsuarioService usuarioService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final LoginAttemptService loginAttemptService;

    /**
     * Endpoint para registrar un nuevo usuario con el rol por defecto (USUARIO_NORMAL).
     * @param registroDto Datos del usuario a registrar.
     * @return ResponseEntity con el usuario registrado o un mensaje de error.
     */
    @PostMapping("/registro")
    public ResponseEntity<?> registrarUsuario(@RequestBody UsuarioRegistroDTO registroDto) {
        if (usuarioService.existePorCorreo(registroDto.getCorreo())) {
            return new ResponseEntity<>("El correo ya está registrado. Por favor, use otro.", HttpStatus.BAD_REQUEST);
        }
        Usuarios usuario = usuarioService.registrarUsuario(registroDto);
        return new ResponseEntity<>(usuario, HttpStatus.CREATED);
    }

    /**
     * Endpoint para que un usuario se autentique y obtenga un token JWT.
     * Incorpora la lógica de bloqueo/desbloqueo por intentos fallidos.
     * @param loginRequest Solicitud de login con correo y contraseña.
     * @return ResponseEntity con el token JWT o un mensaje de error de autenticación.
     */
    @PostMapping("/login")
    public ResponseEntity<?> autenticarUsuario(@RequestBody LoginRequest loginRequest) {
        String correo = loginRequest.getCorreoUsuario();

        // 1. **VERIFICACIÓN PREVIA DE BLOQUEO (RECOMENDADO)**
        if (loginAttemptService.estaBloqueado(correo)) {
            // Si el servicio dice que está bloqueado, devolvemos el error específico de inmediato.
            return new ResponseEntity<>("La cuenta ha sido temporalmente bloqueada por demasiados intentos fallidos.", HttpStatus.UNAUTHORIZED);
        }

        try {
            // Intenta autenticar. Si la contraseña es correcta pero el usuario está 'disabled', salta DisabledException.
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(correo, loginRequest.getContrasena())
            );

            // Si la autenticación es exitosa (usuario y contraseña correctos), resetea los intentos.
            loginAttemptService.registrarExito(correo);

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtService.generarToken(authentication);

            return ResponseEntity.ok(new JwtResponse(jwt));

        } catch (DisabledException e) {
            // Captura si el usuario está 'disabled' (como cuando lo deshabilitas manualmente en el Service)
            // Puedes darle un mensaje similar al bloqueo automático, o uno más general.
            return new ResponseEntity<>("La cuenta ha sido bloqueada. Contacte al administrador.", HttpStatus.UNAUTHORIZED);

        } catch (AuthenticationException e) {
            // Captura fallos de credenciales (contraseña incorrecta)
            loginAttemptService.registrarFallo(correo);

            // Ahora, el mensaje de error puede ser más simple, ya que la verificación de bloqueo
            // se hizo al principio.
            return new ResponseEntity<>("Credenciales inválidas.", HttpStatus.UNAUTHORIZED);
        }
    }
}
