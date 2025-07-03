package com.fullstack.fullstack.Controller;

import com.fullstack.fullstack.Model.Curso;
import com.fullstack.fullstack.Service.CursoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/cursos")
@CrossOrigin(origins = "*")
@Tag(name = "Cursos", description = "API para gestión de cursos")
public class CursoController {

    @Autowired
    private CursoService cursoService;

    @GetMapping
    @Operation(summary = "Listar todos los cursos", description = "Obtiene una lista de todos los cursos disponibles")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de cursos obtenida exitosamente"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<List<Curso>> listar() {
        List<Curso> cursos = cursoService.obtenerTodos();
        return ResponseEntity.ok(cursos); // 200 OK: Solicitud exitosa, devuelve la lista de cursos
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener curso por ID", description = "Busca y devuelve un curso específico por su ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Curso encontrado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Curso no encontrado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<Curso> obtener(@Parameter(description = "ID del curso a buscar") @PathVariable Long id) {
        Optional<Curso> curso = cursoService.obtenerPorId(id);
        return curso.map(ResponseEntity::ok) // 200 OK: Curso encontrado
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build()); // 404 NOT_FOUND: Curso no encontrado
    }

    @PostMapping
    @Operation(summary = "Crear nuevo curso", description = "Crea un nuevo curso con los datos proporcionados")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Curso creado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos o error en la creación"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<Curso> crear(@Parameter(description = "Datos del curso a crear") @RequestBody Curso curso) {
        try {
            Curso creado = cursoService.crear(curso);
            return ResponseEntity.status(HttpStatus.CREATED).body(creado); // 201 CREATED: Curso creado exitosamente
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build(); // 400 BAD_REQUEST: Datos inválidos o error en la creación
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar curso", description = "Actualiza un curso existente con los nuevos datos proporcionados")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Curso actualizado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Curso no encontrado para actualizar"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos o error en la actualización"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<Curso> actualizar(
            @Parameter(description = "ID del curso a actualizar") @PathVariable Long id, 
            @Parameter(description = "Nuevos datos del curso") @RequestBody Curso curso) {
        try {
            Optional<Curso> actualizado = cursoService.actualizar(id, curso);
            return actualizado.map(ResponseEntity::ok) // 200 OK: Curso actualizado exitosamente
                    .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build()); // 404 NOT_FOUND: Curso no encontrado para actualizar
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build(); // 400 BAD_REQUEST: Datos inválidos o error en la actualización
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar curso", description = "Elimina un curso existente por su ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Curso eliminado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Curso no encontrado para eliminar"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<Void> eliminar(@Parameter(description = "ID del curso a eliminar") @PathVariable Long id) {
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