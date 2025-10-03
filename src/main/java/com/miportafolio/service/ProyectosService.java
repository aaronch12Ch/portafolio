package com.miportafolio.service;

import com.miportafolio.exception.ResourceNotFoundException;
import com.miportafolio.model.Proyectos;
import com.miportafolio.repository.ProyectosRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProyectosService {
    private final ProyectosRepository proyectosRepository;

    public List<Proyectos> getAllProyectosAdmin(){
        return proyectosRepository.findAll();
    }

    public Proyectos getProyectoByIdAdmin(Long id){
        Proyectos proyectos = proyectosRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Proyecto no encontrado: "+ id));
        return proyectos;

    }


    public Proyectos createProyectoAdmin(Proyectos proyectos){
        Proyectos saveProyectos = proyectosRepository.save(proyectos);
        return saveProyectos;
    }

    public Proyectos deleteProyectoAmid(Long id){
        Proyectos proyectoDelete = proyectosRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Proyecto no encontrado"+ id));
        proyectosRepository.delete(proyectoDelete);
        return proyectoDelete;
    }

    public Proyectos updateProyectoAdmin(Long id, Proyectos proyectos){
        Proyectos proyectoExistente = proyectosRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Proyecto no encontrado"+ id));
        proyectoExistente.setNombreProyecto(proyectos.getNombreProyecto());
        proyectoExistente.setUrl(proyectos.getUrl());
        proyectoExistente.setUrlImagen(proyectos.getUrlImagen());
        proyectoExistente.setDisponibleProyecto(proyectos.isDisponibleProyecto());
        proyectoExistente.setDescripcionProyecto(proyectos.getDescripcionProyecto());
        Proyectos proyectoUpdate = proyectosRepository.save(proyectoExistente);
        return proyectoUpdate;

    }

}
