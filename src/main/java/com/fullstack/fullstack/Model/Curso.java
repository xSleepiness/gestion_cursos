package com.fullstack.fullstack.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

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
    private String nombre; // Nombre del curso

    private String descripcion; // Descripción opcional del curso

    @Min(value = 1, message = "La duración debe ser mayor a 0")
    private int duracion; // Duración en horas
}