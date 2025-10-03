package com.miportafolio.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String correoUsuario;
    private String contrasena;
}
