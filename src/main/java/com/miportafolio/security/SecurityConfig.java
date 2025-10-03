package com.miportafolio.security;


import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Clase de configuración principal para Spring Security. Define la cadena de filtros
 * y las reglas de autorización.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true) // Habilita anotaciones como @PreAuthorize
@AllArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final UserDetailsService userDetailsService; // Inyecta UserDetailsServiceImpl

    /**
     * Define el bean para el codificador de contraseñas (BCrypt).
     * @return Instancia de BCryptPasswordEncoder.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configura el proveedor de autenticación con nuestro UserDetailsService y PasswordEncoder.
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /**
     * Exposición del AuthenticationManager. Necesario para el login en AuthController.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Define la cadena de filtros de seguridad y las reglas de autorización.
     * Reemplaza el enfoque de formLogin/httpBasic por JWT (STATELESS).
     * @param http Objeto HttpSecurity para configurar la seguridad.
     * @return El filtro de seguridad configurado.
     * @throws Exception Si ocurre un error en la configuración.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // Deshabilita CSRF para API REST
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS) // JWT: sin estado
                )
                .authorizeHttpRequests(authorize -> authorize
                        // Rutas públicas de autenticación (Login y Registro)
                        .requestMatchers("/api/auth/**").permitAll()

                        // 1. REGLA ESPECÍFICA para la gestión ADMIN de proyectos
                        .requestMatchers("/api/proyectos/admin/**").hasAuthority("ADMIN") // <-- ¡AGREGA ESTA LÍNEA!

                        .requestMatchers(HttpMethod.GET, "/api/proyectos/todos").permitAll()
                        // 2. Ruta de administración general
                        .requestMatchers("/api/admin/**").hasAuthority("ADMIN")



                        // Cualquier otra petición debe estar autenticada
                        .anyRequest().authenticated()
                )
                .authenticationProvider(authenticationProvider())
                // Agrega nuestro filtro JWT antes del filtro de autenticación estándar de Spring
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
