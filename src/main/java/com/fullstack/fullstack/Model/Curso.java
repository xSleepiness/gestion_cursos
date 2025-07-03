package com.fullstack.fullstack.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad que representa un Curso en el sistema.
 * 
 * Atributos:
 * - id: Identificador único del curso (autogenerado).
 * - nombre: Nombre del curso (obligatorio, no puede estar en blanco).
 * - descripcion: Descripción opcional del curso.
 * - duracion: Duración del curso en horas (debe ser mayor a 0).
 * 
 * Validaciones:
 * - El nombre no puede estar vacío.
 * - La duración debe ser al menos 1 hora.
 * 
 * Esta clase utiliza Lombok para generar automáticamente los métodos getters, setters,
 * constructores y otros métodos útiles.
 */
@Entity
@Table(name = "curso")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Curso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Identificador único del curso

    @NotBlank(message = "El nombre es obligatorio")
    @Column(length = 100)
    private String nombre; // Nombre del curso

    @Column(length = 1000)
    private String descripcion; // Descripción opcional del curso

    @Min(value = 1, message = "La duración debe ser mayor a 0")
    private int duracion; // Duración en horas

    // Relación con alumnos
    @ManyToMany
    @JoinTable(
        name = "curso_alumno",
        joinColumns = @JoinColumn(name = "curso_id"),
        inverseJoinColumns = @JoinColumn(name = "alumno_id")
    )
    private List<Alumno> alumnos = new ArrayList<>();

    // Métodos de utilidad para la relación ManyToMany
    public void addAlumno(Alumno alumno) {
        if (this.alumnos == null) {
            this.alumnos = new ArrayList<>();
        }
        if (alumno.getCursos() == null) {
            alumno.setCursos(new ArrayList<>());
        }
        alumnos.add(alumno);
        alumno.getCursos().add(this);
    }

    public void removeAlumno(Alumno alumno) {
        if (this.alumnos != null && alumno.getCursos() != null) {
            alumnos.remove(alumno);
            alumno.getCursos().remove(this);
        }
    }
}