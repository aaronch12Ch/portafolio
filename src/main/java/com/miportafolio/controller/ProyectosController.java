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
    private static final Logger logger = LoggerFactory.getLogger(ProyectosController.class);
    private final ProyectosService proyectosService;

    @GetMapping("/todos")
    public List<ProyectosDTO> getAllproyectosPublicos(){
        return proyectosService.getAllProyectosPublic();
    }

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

    @PostMapping("/admin")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'JEFE')")
    public ResponseEntity<?> createProyecto(@RequestBody Proyectos proyecto) {
        try {
            logger.info("POST /api/proyectos/admin - Creando proyecto: {}", proyecto.getNombreProyecto());

            Proyectos savedProyecto = proyectosService.createProyectoAdmin(proyecto);

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

    @PutMapping("/admin/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'JEFE')")
    public ResponseEntity<?> updateProyecto(
            @PathVariable Long id,
            @RequestBody Proyectos proyecto) {
        try {
            logger.info("PUT /api/proyectos/admin/{} - Actualizando proyecto", id);

            Proyectos updatedProyecto = proyectosService.updateProyectoAdmin(id, proyecto);

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

    // Nuevo endpoint para subir video por separado
    @PostMapping(value = "/admin/{id}/video", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyAuthority('ADMIN', 'JEFE')")
    public ResponseEntity<?> uploadVideo(
            @PathVariable Long id,
            @RequestPart("video") MultipartFile videoFile) {
        try {
            logger.info("POST /api/proyectos/admin/{}/video - Subiendo video: {}",
                    id, videoFile.getOriginalFilename());

            Proyectos updatedProyecto = proyectosService.uploadVideoToProyecto(id, videoFile);

            logger.info("Video subido exitosamente para proyecto: {}", id);
            return ResponseEntity.ok(updatedProyecto);

        } catch (IllegalArgumentException e) {
            logger.error("Validación fallida: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error de validación: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Error al subir video para proyecto: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al subir video: " + e.getMessage());
        }
    }

    // Nuevo endpoint para eliminar video
    @DeleteMapping("/admin/{id}/video")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'JEFE')")
    public ResponseEntity<?> deleteVideo(@PathVariable Long id) {
        try {
            logger.info("DELETE /api/proyectos/admin/{}/video - Eliminando video", id);

            Proyectos updatedProyecto = proyectosService.deleteVideoFromProyecto(id);

            logger.info("Video eliminado exitosamente del proyecto: {}", id);
            return ResponseEntity.ok(updatedProyecto);

        } catch (Exception e) {
            logger.error("Error al eliminar video del proyecto: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al eliminar video: " + e.getMessage());
        }
    }

    @DeleteMapping("/admin/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> deleteProyecto(@PathVariable Long id){
        proyectosService.deleteProyectoAmid(id);
        return ResponseEntity.noContent().build();
    }
}