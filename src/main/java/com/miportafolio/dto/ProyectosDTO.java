package com.miportafolio.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProyectosDTO {

    @NotBlank(message = "El nombre no puede estar vacío")
    @Size(min = 4, max = 20, message = "El nombre debe tener entre 4 y 20 caracteres")
    private String nombreProyecto;

    @NotBlank(message = "El Url no puede estar vacío")
    @Size(min = 4, max = 20, message = "El url debe tener entre 4 y 20 caracteres")
    private String url;

    @NotBlank(message = "El Url Imagen no puede estar vacío")
    @Size(min = 4, max = 20, message = "El Url Imagen debe tener entre 4 y 20 caracteres")
    private String urlImagen;

    @NotNull(message = "La disponibilidad es obligatoria")
    private boolean disponibleProyecto;

}
