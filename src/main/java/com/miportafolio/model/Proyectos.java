package com.miportafolio.model;

import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Proyectos {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idProyecto;

    @Column(length = 8, nullable = false)
    private String proyecto;

    @Column(length = 20, nullable = false)
    private String url;

    @Column(length = 20, nullable = false)
    private String urlImagen;

    @Column(nullable = false)
    private boolean disponibleProyecto;
}
