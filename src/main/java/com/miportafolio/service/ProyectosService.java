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
        Proyectos proyectos = proyectosRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Proyecto no encontrado: "+ id));
        return proyectos;

    }


    /*public Proyectos createProyectoAdmin(Proyectos proyectos){
        Proyectos saveProyectos = proyectosRepository.save(proyectos);
        return saveProyectos;
    }*/
    public Proyectos createProyectoAdmin(Proyectos proyectos, MultipartFile videoFile) throws IOException {

        // 1. Si se adjuntó un archivo, subirlo a S3
        if (videoFile != null && !videoFile.isEmpty()) {
            String s3Key = s3Service.uploadFile(videoFile);
            proyectos.setS3VideoKey(s3Key); // Guardar la clave en la entidad
        }

        // 2. Guardar el proyecto en la DB
        Proyectos saveProyectos = proyectosRepository.save(proyectos);
        return saveProyectos;
    }

    /*public Proyectos deleteProyectoAmid(Long id){
        Proyectos proyectoDelete = proyectosRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Proyecto no encontrado"+ id));
        proyectosRepository.delete(proyectoDelete);
        return proyectoDelete;
    }*/
    public void deleteProyectoAmid(Long id){
        Proyectos proyectoDelete = proyectosRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Proyecto no encontrado" + id));

        // 1. LÓGICA DE ELIMINACIÓN DE S3 (¡CRUCIAL!)
        // Solo borra de S3 si tiene una clave asociada
        if (proyectoDelete.getS3VideoKey() != null && !proyectoDelete.getS3VideoKey().isEmpty()) {
            s3Service.deleteFile(proyectoDelete.getS3VideoKey());
        }

        // 2. Eliminar de la base de datos
        proyectosRepository.delete(proyectoDelete);
    }

    /*public Proyectos updateProyectoAdmin(Long id, Proyectos proyectos){
        Proyectos proyectoExistente = proyectosRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Proyecto no encontrado"+ id));
        proyectoExistente.setNombreProyecto(proyectos.getNombreProyecto());
        proyectoExistente.setUrl(proyectos.getUrl());
        proyectoExistente.setUrlImagen(proyectos.getUrlImagen());
        proyectoExistente.setDisponibleProyecto(proyectos.isDisponibleProyecto());
        proyectoExistente.setDescripcionProyecto(proyectos.getDescripcionProyecto());
        Proyectos proyectoUpdate = proyectosRepository.save(proyectoExistente);
        return proyectoUpdate;

    }*/
    public Proyectos updateProyectoAdmin(Long id, Proyectos proyectos, MultipartFile videoFile) throws IOException {
        Proyectos proyectoExistente = proyectosRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Proyecto no encontrado" + id));

        // 1. LÓGICA DEL VIDEO
        if (videoFile != null && !videoFile.isEmpty()) {

            // a. Eliminar el video antiguo de S3 (si existe)
            if (proyectoExistente.getS3VideoKey() != null) {
                s3Service.deleteFile(proyectoExistente.getS3VideoKey());
            }

            // b. Subir el nuevo video
            String newS3Key = s3Service.uploadFile(videoFile);
            proyectoExistente.setS3VideoKey(newS3Key);
        }
        // Nota: Si videoFile es null, se conserva el s3VideoKey existente.

        // 2. Actualizar el resto de campos
        proyectoExistente.setNombreProyecto(proyectos.getNombreProyecto());
        proyectoExistente.setUrl(proyectos.getUrl());
        proyectoExistente.setUrlImagen(proyectos.getUrlImagen());
        proyectoExistente.setDisponibleProyecto(proyectos.isDisponibleProyecto());
        proyectoExistente.setDescripcionProyecto(proyectos.getDescripcionProyecto());

        // 3. Guardar en la DB
        Proyectos proyectoUpdate = proyectosRepository.save(proyectoExistente);
        return proyectoUpdate;
    }

    private ProyectosDTO convertToDto(Proyectos proyectos) {
        // Mapea la entidad a un DTO
        return new ProyectosDTO(proyectos.getNombreProyecto(), proyectos.getUrl(),proyectos.getUrlImagen(),proyectos.getDescripcionProyecto(),proyectos.getS3VideoKey(), proyectos.isDisponibleProyecto());
    }
    private Proyectos convertToEntity(ProyectosDTO carroDTO) {
        // Mapea el DTO a una entidad
        Proyectos proyecto = new Proyectos();

        proyecto.setNombreProyecto(proyecto.getNombreProyecto());
        proyecto.setUrl(proyecto.getUrl());
        proyecto.setUrlImagen(proyecto.getUrlImagen());
        proyecto.setDisponibleProyecto(proyecto.isDisponibleProyecto());
        proyecto.setS3VideoKey(proyecto.getS3VideoKey());
        proyecto.setDescripcionProyecto(proyecto.getDescripcionProyecto());
        return proyecto;
    }
}
