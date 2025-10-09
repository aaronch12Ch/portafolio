package com.miportafolio.controller;


import com.miportafolio.dto.ProyectosDTO;
import com.miportafolio.model.Proyectos;
import com.miportafolio.service.ProyectosService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@RestController
@RequestMapping("/api/proyectos")
@RequiredArgsConstructor
public class ProyectosController {
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
    @PostMapping("/admin")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('JEFE')")
    public Proyectos postProyecto(
            @RequestPart("proyecto") Proyectos proyectos,
            @RequestParam(value = "video", required = false) MultipartFile videoFile) throws Exception {

        // Llama al servicio con el objeto y el archivo
        return proyectosService.createProyectoAdmin(proyectos, videoFile);
    }

    @PutMapping("/admin/{id}")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('JEFE')")
    public Proyectos updateProyecto(
            @PathVariable Long id,
            @RequestPart("proyecto") Proyectos proyectos,
            @RequestParam(value = "video", required = false) MultipartFile videoFile) throws Exception {

        // Llama al servicio con el ID, el objeto y el archivo (el servicio manejará si borrar o no el viejo)
        return proyectosService.updateProyectoAdmin(id, proyectos, videoFile);
    }

    @DeleteMapping("/admin/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> deleteProyecto(@PathVariable Long id){
        proyectosService.deleteProyectoAmid(id);
        return ResponseEntity.noContent().build();
    }
}
