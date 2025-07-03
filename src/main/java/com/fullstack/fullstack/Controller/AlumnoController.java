package com.fullstack.fullstack.Controller;

import com.fullstack.fullstack.Model.Alumno;
import com.fullstack.fullstack.Service.AlumnoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/alumnos")
@CrossOrigin(origins = "*")
@Tag(name = "Alumnos", description = "API para gestión de alumnos integrada con API externa de usuarios")
public class AlumnoController {

    @Autowired
    private AlumnoService alumnoService;

    @GetMapping
    @Operation(summary = "Listar todos los alumnos", 
               description = "Obtiene una lista de todos los alumnos. Prioriza datos de la API externa de usuarios, con fallback a datos locales.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de alumnos obtenida exitosamente"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<List<Alumno>> listar() {
        try {
            List<Alumno> alumnos = alumnoService.obtenerTodos();
            return ResponseEntity.ok(alumnos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener alumno por ID", 
               description = "Busca y devuelve un alumno específico por su ID. Prioriza la API externa de usuarios.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Alumno encontrado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Alumno no encontrado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<Alumno> obtener(@Parameter(description = "ID del alumno a buscar") @PathVariable Long id) {
        try {
            Optional<Alumno> alumno = alumnoService.obtenerPorId(id);
            return alumno.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    @Operation(summary = "Crear nuevo alumno", 
               description = "Crea un nuevo alumno en la base de datos local. Verifica que no exista en la API externa.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Alumno creado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos o email ya existe"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<Alumno> crear(@Parameter(description = "Datos del alumno a crear") @RequestBody Alumno alumno) {
        try {
            Alumno creado = alumnoService.crear(alumno);
            return ResponseEntity.status(HttpStatus.CREATED).body(creado);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar alumno", 
               description = "Actualiza un alumno existente en la base de datos local.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Alumno actualizado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Alumno no encontrado para actualizar"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos o error en la actualización"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<Alumno> actualizar(
            @Parameter(description = "ID del alumno a actualizar") @PathVariable Long id, 
            @Parameter(description = "Nuevos datos del alumno") @RequestBody Alumno alumno) {
        try {
            Alumno actualizado = alumnoService.actualizar(id, alumno);
            return ResponseEntity.ok(actualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar alumno", 
               description = "Elimina un alumno existente de la base de datos local.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Alumno eliminado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Alumno no encontrado para eliminar"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<Void> eliminar(@Parameter(description = "ID del alumno a eliminar") @PathVariable Long id) {
        try {
            alumnoService.eliminar(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/email/{email}")
    @Operation(summary = "Buscar alumno por email", 
               description = "Busca y devuelve un alumno por su dirección de email. Prioriza la API externa de usuarios.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Alumno encontrado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Alumno no encontrado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<Alumno> buscarPorEmail(@Parameter(description = "Email del alumno a buscar") @PathVariable String email) {
        try {
            Optional<Alumno> alumno = alumnoService.obtenerPorEmail(email);
            return alumno.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/status/api-externa")
    @Operation(summary = "Verificar estado de la API externa", 
               description = "Verifica si la API externa de usuarios está disponible.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Estado de la API externa obtenido exitosamente"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<String> verificarApiExterna() {
        try {
            boolean disponible = alumnoService.isApiExternaDisponible();
            String mensaje = disponible ? "API externa disponible" : "API externa no disponible";
            return ResponseEntity.ok(mensaje);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al verificar la API externa");
        }
    }
}
