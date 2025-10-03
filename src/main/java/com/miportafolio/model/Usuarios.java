package com.miportafolio.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Data
@NoArgsConstructor
public class Usuarios implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idUsuario;

    @Column(nullable = false)
    private String nombreUsuario;

    @Column(unique = true)
    private String correo;

    @Column(nullable = false)
    private String contrasena;

    private int intentosFallidos = 0;

    private boolean bloqueado = false;


    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "usuario_rol",
            joinColumns = @JoinColumn(name = "usuario_id"),
            inverseJoinColumns = @JoinColumn(name = "rol_id")
    )
    private Set<Rol> roles = new HashSet<>();

    // ===============================================
    // Métodos de UserDetails (para Spring Security)
    // ===============================================

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(rol -> new SimpleGrantedAuthority(rol.getNombre().name()))
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return this.contrasena;
    }

    @Override
    public String getUsername() {
        return this.correo;
    }
    @Override
    public boolean isAccountNonLocked() {
        return !this.bloqueado;
    }

    @Override
    public boolean isEnabled() {
        return !this.bloqueado;
    }

    @Override
    public boolean isAccountNonExpired() { return true; }
    @Override
    public boolean isCredentialsNonExpired() { return true; }
}
