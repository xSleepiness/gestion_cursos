package com.fullstack.fullstack.Controller;

import com.fullstack.fullstack.Model.Curso;
import com.fullstack.fullstack.Service.CursoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/api/cursos")
@CrossOrigin(origins = "*")
@Tag(name = "Cursos", description = "API para gestión de cursos con enlaces HATEOAS")
public class CursoController {

    @Autowired
    private CursoService cursoService;

    @GetMapping
    @Operation(summary = "Listar todos los cursos", description = "Obtiene una lista de todos los cursos disponibles con enlaces HATEOAS")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de cursos obtenida exitosamente"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<CollectionModel<EntityModel<Curso>>> listar() {
        List<Curso> cursos = cursoService.obtenerTodos();
        
        // Crear EntityModel para cada curso con sus enlaces
        List<EntityModel<Curso>> cursosConEnlaces = cursos.stream()
                .map(curso -> EntityModel.of(curso)
                        .add(linkTo(methodOn(CursoController.class).obtener(curso.getId())).withSelfRel())
                        .add(linkTo(methodOn(CursoController.class).actualizar(curso.getId(), curso)).withRel("actualizar"))
                        .add(linkTo(methodOn(CursoController.class).eliminar(curso.getId())).withRel("eliminar")))
                .collect(Collectors.toList());
        
        // Crear CollectionModel con enlaces de la colección
        CollectionModel<EntityModel<Curso>> resultado = CollectionModel.of(cursosConEnlaces)
                .add(linkTo(methodOn(CursoController.class).listar()).withSelfRel())
                .add(linkTo(methodOn(CursoController.class).crear(null)).withRel("crear"));
        
        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener curso por ID", description = "Busca y devuelve un curso específico por su ID con enlaces HATEOAS")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Curso encontrado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Curso no encontrado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<EntityModel<Curso>> obtener(@Parameter(description = "ID del curso a buscar") @PathVariable Long id) {
        Optional<Curso> curso = cursoService.obtenerPorId(id);
        
        if (curso.isPresent()) {
            EntityModel<Curso> cursoConEnlaces = EntityModel.of(curso.get())
                    .add(linkTo(methodOn(CursoController.class).obtener(id)).withSelfRel())
                    .add(linkTo(methodOn(CursoController.class).actualizar(id, curso.get())).withRel("actualizar"))
                    .add(linkTo(methodOn(CursoController.class).eliminar(id)).withRel("eliminar"))
                    .add(linkTo(methodOn(CursoController.class).listar()).withRel("cursos"));
            
            return ResponseEntity.ok(cursoConEnlaces);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PostMapping
    @Operation(summary = "Crear nuevo curso", description = "Crea un nuevo curso con los datos proporcionados y devuelve enlaces HATEOAS")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Curso creado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos o error en la creación"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<EntityModel<Curso>> crear(@Parameter(description = "Datos del curso a crear") @RequestBody Curso curso) {
        try {
            Curso creado = cursoService.crear(curso);
            
            EntityModel<Curso> cursoConEnlaces = EntityModel.of(creado)
                    .add(linkTo(methodOn(CursoController.class).obtener(creado.getId())).withSelfRel())
                    .add(linkTo(methodOn(CursoController.class).actualizar(creado.getId(), creado)).withRel("actualizar"))
                    .add(linkTo(methodOn(CursoController.class).eliminar(creado.getId())).withRel("eliminar"))
                    .add(linkTo(methodOn(CursoController.class).listar()).withRel("cursos"));
            
            return ResponseEntity.status(HttpStatus.CREATED).body(cursoConEnlaces);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar curso", description = "Actualiza un curso existente con los nuevos datos proporcionados y devuelve enlaces HATEOAS")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Curso actualizado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Curso no encontrado para actualizar"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos o error en la actualización"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<EntityModel<Curso>> actualizar(
            @Parameter(description = "ID del curso a actualizar") @PathVariable Long id, 
            @Parameter(description = "Nuevos datos del curso") @RequestBody Curso curso) {
        try {
            Optional<Curso> actualizado = cursoService.actualizar(id, curso);
            
            if (actualizado.isPresent()) {
                EntityModel<Curso> cursoConEnlaces = EntityModel.of(actualizado.get())
                        .add(linkTo(methodOn(CursoController.class).obtener(id)).withSelfRel())
                        .add(linkTo(methodOn(CursoController.class).actualizar(id, actualizado.get())).withRel("actualizar"))
                        .add(linkTo(methodOn(CursoController.class).eliminar(id)).withRel("eliminar"))
                        .add(linkTo(methodOn(CursoController.class).listar()).withRel("cursos"));
                
                return ResponseEntity.ok(cursoConEnlaces);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
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