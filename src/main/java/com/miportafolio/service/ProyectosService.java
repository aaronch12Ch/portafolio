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

    public Proyectos getProyectoById(Long id){
        Proyectos proyectos = proyectosRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Proyecto no encontrado: "+ id));
        return proyectos;

    }
}
