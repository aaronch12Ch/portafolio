package com.miportafolio.utils;


import com.miportafolio.model.Rol;
import com.miportafolio.model.RolNombre;
import com.miportafolio.model.Usuarios;
import com.miportafolio.repository.RolRepository;
import com.miportafolio.repository.UsuarioRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value; // 👈 NECESARIO
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;

@Component
// Ya no usamos @AllArgsConstructor, porque Spring tiene problemas
// para inyectar @Value antes de un constructor @AllArgsConstructor.
public class DataInitializer implements CommandLineRunner {

    private final RolRepository rolRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    // 👈 NUEVAS INYECCIONES DE VALOR
    @Value("${USER_APP}")
    private String adminUsername;

    @Value("${PASS_APP}")
    private String adminPassword;

    // 👈 CONSTRUCTOR MANUAL PARA DEPENDENCIAS REQUERIDAS
    public DataInitializer(RolRepository rolRepository, UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.rolRepository = rolRepository;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }


    @Override
    public void run(String... args) throws Exception {
        // --- 1. Inicializa Roles (Código de roles omitido por brevedad, asume que funciona) ---

        Optional<Rol> adminRole = rolRepository.findByNombre(RolNombre.ADMIN);
        // ... (Verificaciones y guardado de roles JEFE y USUARIO_NORMAL) ...

        if (adminRole.isEmpty()) {
            adminRole = Optional.of(rolRepository.save(new Rol(null, RolNombre.ADMIN)));
        }

        // --- 2. Crea el Usuario Administrador Inicial usando las propiedades inyectadas ---

        if (!usuarioRepository.findByCorreo(adminUsername).isPresent()) {
            Usuarios admin = new Usuarios();

            // Usando el valor inyectado desde application.properties
            admin.setCorreo(adminUsername);
            admin.setNombreUsuario("Admin Principal (Config)");

            // Usando la contraseña inyectada y cifrándola
            admin.setContrasena(passwordEncoder.encode(adminPassword));

            admin.setIntentosFallidos(0);
            admin.setBloqueado(false);
            admin.setRoles(Set.of(adminRole.get()));

            usuarioRepository.save(admin);

            System.out.println("✅ Administrador inicial creado desde config: " + adminUsername + " / Password: (revisar configuración)");
        }
    }
}
