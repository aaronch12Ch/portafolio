package com.miportafolio.service;

import com.miportafolio.dto.ProyectosDTO;
import com.miportafolio.exception.ResourceNotFoundException;
import com.miportafolio.model.Proyectos;
import com.miportafolio.repository.ProyectosRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProyectosService {
    private final ProyectosRepository proyectosRepository;
    private final S3Service s3Service;

    public List<ProyectosDTO> getAllProyectosPublic(){
        return proyectosRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<Proyectos> getAllProyectosAdmin(){
        return proyectosRepository.findAll();
    }

    public Proyectos getProyectoByIdAdmin(Long id){
        return proyectosRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Proyecto no encontrado: " + id));
    }

    // Crear proyecto SIN video
    public Proyectos createProyectoAdmin(Proyectos proyectos) {
        return proyectosRepository.save(proyectos);
    }

    // Actualizar proyecto SIN video
    public Proyectos updateProyectoAdmin(Long id, Proyectos proyectos) {
        Proyectos proyectoExistente = proyectosRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Proyecto no encontrado" + id));

        // Actualizar campos (sin tocar el s3VideoKey)
        proyectoExistente.setNombreProyecto(proyectos.getNombreProyecto());
        proyectoExistente.setUrl(proyectos.getUrl());
        proyectoExistente.setUrlImagen(proyectos.getUrlImagen());
        proyectoExistente.setDisponibleProyecto(proyectos.isDisponibleProyecto());
        proyectoExistente.setDescripcionProyecto(proyectos.getDescripcionProyecto());

        return proyectosRepository.save(proyectoExistente);
    }

    // Subir video a un proyecto existente
    public Proyectos uploadVideoToProyecto(Long id, MultipartFile videoFile) throws IOException {
        Proyectos proyecto = proyectosRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Proyecto no encontrado: " + id));

        if (videoFile == null || videoFile.isEmpty()) {
            throw new IllegalArgumentException("El archivo de video es requerido");
        }

        // Si ya tiene un video, eliminar el anterior de S3
        if (proyecto.getS3VideoKey() != null && !proyecto.getS3VideoKey().isEmpty()) {
            s3Service.deleteFile(proyecto.getS3VideoKey());
        }

        // Subir el nuevo video a S3
        String s3Key = s3Service.uploadFile(videoFile);
        proyecto.setS3VideoKey(s3Key);

        return proyectosRepository.save(proyecto);
    }

    // Eliminar video de un proyecto
    public Proyectos deleteVideoFromProyecto(Long id) {
        Proyectos proyecto = proyectosRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Proyecto no encontrado: " + id));

        // Si tiene video en S3, eliminarlo
        if (proyecto.getS3VideoKey() != null && !proyecto.getS3VideoKey().isEmpty()) {
            s3Service.deleteFile(proyecto.getS3VideoKey());
            proyecto.setS3VideoKey(null);
        }

        return proyectosRepository.save(proyecto);
    }

    // Eliminar proyecto completo
    public void deleteProyectoAmid(Long id){
        Proyectos proyectoDelete = proyectosRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Proyecto no encontrado" + id));

        // Eliminar video de S3 si existe
        if (proyectoDelete.getS3VideoKey() != null && !proyectoDelete.getS3VideoKey().isEmpty()) {
            s3Service.deleteFile(proyectoDelete.getS3VideoKey());
        }

        // Eliminar de la base de datos
        proyectosRepository.delete(proyectoDelete);
    }

    private ProyectosDTO convertToDto(Proyectos proyectos) {
        return new ProyectosDTO(
                proyectos.getNombreProyecto(),
                proyectos.getUrl(),
                proyectos.getUrlImagen(),
                proyectos.getDescripcionProyecto(),
                proyectos.getS3VideoKey(),
                proyectos.isDisponibleProyecto()
        );
    }

    private Proyectos convertToEntity(ProyectosDTO proyectoDTO) {
        Proyectos proyecto = new Proyectos();
        proyecto.setNombreProyecto(proyectoDTO.getNombreProyecto());
        proyecto.setUrl(proyectoDTO.getUrl());
        proyecto.setUrlImagen(proyectoDTO.getUrlImagen());
        proyecto.setDisponibleProyecto(proyectoDTO.isDisponibleProyecto());
        proyecto.setS3VideoKey(proyectoDTO.getS3VideoKey());
        proyecto.setDescripcionProyecto(proyectoDTO.getDescripcionProyecto());
        return proyecto;
    }
}