package com.miportafolio.controller;


import com.miportafolio.model.Proyectos;
import com.miportafolio.service.ProyectosService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/proyectos/admin")
@RequiredArgsConstructor
public class ProyectosController {
    private final ProyectosService proyectosService;

    @GetMapping
    public List<Proyectos> getAllProyectos(){
        return proyectosService.getAllProyectosAdmin();
    }

    @GetMapping("/{id}")
    public Proyectos getById(@PathVariable Long id){
        return proyectosService.getProyectoByIdAdmin(id);
    }

    @PostMapping
    public Proyectos postProyecto(@RequestBody Proyectos proyectos){
        return proyectosService.createProyectoAdmin(proyectos);
    }

    @PutMapping("/{id}")
    public Proyectos updateProyecto(@PathVariable Long id,@RequestBody Proyectos proyectos){
        return proyectosService.updateProyectoAdmin(id, proyectos);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProyecto(@PathVariable Long id){
        proyectosService.deleteProyectoAmid(id);
        return ResponseEntity.noContent().build();
    }
}
