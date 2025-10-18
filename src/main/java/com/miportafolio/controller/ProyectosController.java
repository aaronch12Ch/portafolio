package com.miportafolio.controller;


import com.miportafolio.dto.ProyectosDTO;
import com.miportafolio.model.Proyectos;
import com.miportafolio.service.ProyectosService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@RestController
@RequestMapping("/api/proyectos")
@RequiredArgsConstructor
public class ProyectosController {
    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);
    private final ProyectosService proyectosService;

    @GetMapping("/todos")
    public List<ProyectosDTO> getAllproyectosPublicos(){return proyectosService.getAllProyectosPublic();}

    @GetMapping("/admin")
    @PreAuthorize("isAuthenticated()")
    public List<Proyectos> getAllProyectos(){
        return proyectosService.getAllProyectosAdmin();
    }

    @GetMapping("/admin/{id}")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('JEFE')")
    public Proyectos getById(@PathVariable Long id){
        return proyectosService.getProyectoByIdAdmin(id);
    }


    /*@PostMapping("/admin")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('JEFE')")
    public Proyectos postProyecto(@RequestBody Proyectos proyectos){
        return proyectosService.createProyectoAdmin(proyectos);
    }

    @PutMapping("/admin/{id}")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('JEFE')")
    public Proyectos updateProyecto(@PathVariable Long id,@RequestBody Proyectos proyectos){
        return proyectosService.updateProyectoAdmin(id, proyectos);
    }*/
    @PostMapping(value = "/admin", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyAuthority('ADMIN', 'JEFE')")
    public ResponseEntity<?> createProyecto(
            @RequestPart("proyecto") Proyectos proyecto,
            @RequestPart(value = "video", required = false) MultipartFile videoFile) {

        try {
            logger.info("POST /api/proyectos/admin - Creando proyecto: {}", proyecto.getNombreProyecto());
            logger.info("Video recibido: {}", videoFile != null ? videoFile.getOriginalFilename() : "ninguno");
            logger.info("Tipo de contenido del video: {}", videoFile != null ? videoFile.getContentType() : "N/A");
            logger.info("Tamaño del video: {} bytes", videoFile != null ? videoFile.getSize() : 0);

            Proyectos savedProyecto = proyectosService.createProyectoAdmin(proyecto, videoFile);

            logger.info("Proyecto creado exitosamente con ID: {}", savedProyecto.getIdProyecto());
            return ResponseEntity.status(HttpStatus.CREATED).body(savedProyecto);

        } catch (IllegalArgumentException e) {
            logger.error("Validación fallida: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error de validación: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Error al crear proyecto", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al crear proyecto: " + e.getMessage());
        }
    }

    @PutMapping(value = "/admin/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyAuthority('ADMIN', 'JEFE')")
    public ResponseEntity<?> updateProyecto(
            @PathVariable Long id,
            @RequestPart("proyecto") Proyectos proyecto,
            @RequestPart(value = "video", required = false) MultipartFile videoFile) {

        try {
            logger.info("PUT /api/proyectos/admin/{} - Actualizando proyecto", id);
            logger.info("Video recibido: {}", videoFile != null ? videoFile.getOriginalFilename() : "ninguno");

            Proyectos updatedProyecto = proyectosService.updateProyectoAdmin(id, proyecto, videoFile);

            logger.info("Proyecto actualizado exitosamente: {}", id);
            return ResponseEntity.ok(updatedProyecto);

        } catch (IllegalArgumentException e) {
            logger.error("Validación fallida: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error de validación: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Error al actualizar proyecto: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al actualizar proyecto: " + e.getMessage());
        }
    }

    @DeleteMapping("/admin/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> deleteProyecto(@PathVariable Long id){
        proyectosService.deleteProyectoAmid(id);
        return ResponseEntity.noContent().build();
    }
}
