package com.miportafolio.repository;

import com.miportafolio.model.Usuarios;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuarios,Long> {
    Optional<Usuarios> findByCorreo(String correo);

    // Verifica si ya existe un usuario con un correo dado (usado para el registro).
    Boolean existsByCorreo(String correo);
}
