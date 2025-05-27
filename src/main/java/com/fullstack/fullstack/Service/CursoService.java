package com.fullstack.fullstack.Service;

import com.fullstack.fullstack.Model.Curso;
import com.fullstack.fullstack.Repository.CursoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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

    public Curso actualizar(Long id, Curso datosCurso) {
        return cursoRepository.findById(id).map(curso -> {
            curso.setNombre(datosCurso.getNombre());
            curso.setDescripcion(datosCurso.getDescripcion());
            curso.setDuracion(datosCurso.getDuracion());
            return cursoRepository.save(curso);
        }).orElse(null);
    }

    public void eliminar(Long id) {
        cursoRepository.deleteById(id);
    }
}
