package com.miportafolio.model;

import jakarta.persistence.*;

import jakarta.validation.constraints.Max;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class                Proyectos {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idProyecto;

    @Column(length = 20, nullable = false)
    private String nombreProyecto;

    @Column(length = 255, nullable = false)
    private String url;

    @Column(length = 255, nullable = false)
    private String urlImagen;

    @Column(length = 500, nullable = false)
    private String descripcionProyecto;

    @Column(nullable = false)
    private boolean disponibleProyecto;

    @Column(columnDefinition = "TEXT")
    private String s3VideoKey;
}
