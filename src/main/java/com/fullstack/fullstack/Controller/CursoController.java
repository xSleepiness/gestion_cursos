package com.fullstack.fullstack.Controller;

import com.fullstack.fullstack.Model.Curso;
import com.fullstack.fullstack.Service.CursoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/cursos")
@CrossOrigin(origins = "*")
public class CursoController {

    @Autowired
    private CursoService cursoService;

    @GetMapping
    public ResponseEntity<List<Curso>> listar() {
        List<Curso> cursos = cursoService.obtenerTodos();
        return ResponseEntity.ok(cursos); // 200 OK: Solicitud exitosa, devuelve la lista de cursos
    }

    @GetMapping("/{id}")
    public ResponseEntity<Curso> obtener(@PathVariable Long id) {
        Optional<Curso> curso = cursoService.obtenerPorId(id);
        return curso.map(ResponseEntity::ok) // 200 OK: Curso encontrado
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build()); // 404 NOT_FOUND: Curso no encontrado
    }

    @PostMapping
    public ResponseEntity<Curso> crear(@RequestBody Curso curso) {
        try {
            Curso creado = cursoService.crear(curso);
            return ResponseEntity.status(HttpStatus.CREATED).body(creado); // 201 CREATED: Curso creado exitosamente
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build(); // 400 BAD_REQUEST: Datos inválidos o error en la creación
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Curso> actualizar(@PathVariable Long id, @RequestBody Curso curso) {
        try {
            Optional<Curso> actualizado = cursoService.actualizar(id, curso);
            return actualizado.map(ResponseEntity::ok) // 200 OK: Curso actualizado exitosamente
                    .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build()); // 404 NOT_FOUND: Curso no encontrado para actualizar
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build(); // 400 BAD_REQUEST: Datos inválidos o error en la actualización
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        try {
            boolean eliminado = cursoService.eliminar(id);
            if (eliminado) {
                return ResponseEntity.noContent().build(); // 204 NO_CONTENT: Curso eliminado exitosamente
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // 404 NOT_FOUND: Curso no encontrado para eliminar
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // 500 INTERNAL_SERVER_ERROR: Error inesperado en el servidor
        }
    }
}