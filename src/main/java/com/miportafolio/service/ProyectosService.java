package com.miportafolio.service;

import com.miportafolio.dto.ProyectosDTO;
import com.miportafolio.exception.ResourceNotFoundException;
import com.miportafolio.model.Proyectos;
import com.miportafolio.repository.ProyectosRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProyectosService {
    private final ProyectosRepository proyectosRepository;

    public List<ProyectosDTO> getAllProyectosPublic(){
        return proyectosRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

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

    private ProyectosDTO convertToDto(Proyectos proyectos) {
        // Mapea la entidad a un DTO
        return new ProyectosDTO(proyectos.getNombreProyecto(), proyectos.getUrl(),proyectos.getUrlImagen(),proyectos.getDescripcionProyecto(), proyectos.isDisponibleProyecto());
    }
    private Proyectos convertToEntity(ProyectosDTO carroDTO) {
        // Mapea el DTO a una entidad
        Proyectos proyecto = new Proyectos();

        proyecto.setNombreProyecto(proyecto.getNombreProyecto());
        proyecto.setUrl(proyecto.getUrl());
        proyecto.setUrlImagen(proyecto.getUrlImagen());
        proyecto.setDisponibleProyecto(proyecto.isDisponibleProyecto());
        return proyecto;
    }
}
