package com.fullstack.fullstack.Service;

import com.fullstack.fullstack.Model.Curso;
import com.fullstack.fullstack.Repository.CursoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Servicio que contiene la lógica de negocio para la gestión de cursos.
 *
 * Este servicio actúa como intermediario entre el controlador (CursoController)
 * y el repositorio (CursoRepository), encapsulando las operaciones CRUD
 * y cualquier lógica adicional relacionada con la entidad Curso.
 *
 * Métodos principales:
 * - obtenerTodos(): Devuelve la lista de todos los cursos.
 * - obtenerPorId(Long id): Busca un curso por su ID.
 * - crear(Curso curso): Guarda un nuevo curso en la base de datos.
 * - actualizar(Long id, Curso datosCurso): Actualiza los datos de un curso existente.
 * - eliminar(Long id): Elimina un curso por su ID.
 *
 * El uso de Optional en algunos métodos permite manejar de forma segura
 * los casos en los que un curso no existe.
 */
@Service
public class CursoService {

    @Autowired
    private CursoRepository cursoRepository;

    public List<Curso> obtenerTodos() {
        return cursoRepository.findAll();
    }

    public Optional<Curso> obtenerPorId(Long id) {
        return cursoRepository.findById(id);
    }

    public Curso crear(Curso curso) {
        return cursoRepository.save(curso);
    }

    public Optional<Curso> actualizar(Long id, Curso datosCurso) {
        return cursoRepository.findById(id).map(curso -> {
            curso.setNombre(datosCurso.getNombre());
            curso.setDescripcion(datosCurso.getDescripcion());
            curso.setDuracion(datosCurso.getDuracion());
            return cursoRepository.save(curso);
        });
    }

    public boolean eliminar(Long id) {
        if (cursoRepository.existsById(id)) {
            cursoRepository.deleteById(id);
            return true;
        }
        return false;
    }
}