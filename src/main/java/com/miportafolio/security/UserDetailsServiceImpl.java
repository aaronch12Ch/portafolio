package com.miportafolio.security;

import com.miportafolio.model.Usuarios;
import com.miportafolio.repository.UsuarioRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UsuarioRepository usuarioRepository;

    /**
     * Carga el usuario por su correo (username) y lanza una excepción si no existe.
     * @param username Correo electrónico del usuario.
     * @return UserDetails (nuestra entidad Usuario).
     * @throws UsernameNotFoundException Si el usuario no existe.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Busca el usuario por correo. La entidad Usuario ya implementa UserDetails.
        Usuarios usuario = usuarioRepository.findByCorreo(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con correo: " + username));

        // Si el usuario está bloqueado, Spring Security lanzará automáticamente una
        // DisabledException o LockedException durante el proceso de login,
        // gracias a la implementación de isAccountNonLocked() y isEnabled() en Usuario.java.

        return usuario;
    }

}
