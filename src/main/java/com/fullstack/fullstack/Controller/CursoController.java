package com.fullstack.fullstack.Controller;

import com.fullstack.fullstack.Model.Curso;
import com.fullstack.fullstack.Service.CursoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cursos")
@CrossOrigin(origins = "*")
public class CursoController {

    @Autowired
    private CursoService cursoService;

    @GetMapping
    public List<Curso> listar() {
        return cursoService.obtenerTodos();
    }

    @GetMapping("/{id}")
    public Curso obtener(@PathVariable Long id) {
        return cursoService.obtenerPorId(id).orElse(null);
    }

    @PostMapping
    public Curso crear(@RequestBody Curso curso) {
        return cursoService.crear(curso);
    }

    @PutMapping("/{id}")
    public Curso actualizar(@PathVariable Long id, @RequestBody Curso curso) {
        return cursoService.actualizar(id, curso);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        cursoService.eliminar(id);
    }
}
