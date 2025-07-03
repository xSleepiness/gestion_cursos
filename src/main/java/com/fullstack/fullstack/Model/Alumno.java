package com.fullstack.fullstack.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "alumnos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Alumno {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;
    
    @Column(nullable = false, unique = true, length = 100)
    private String email;

    // Relación con cursos
    @ManyToMany(mappedBy = "alumnos")
    private List<Curso> cursos = new ArrayList<>();

    // Métodos de utilidad para la relación ManyToMany
    public void addCurso(Curso curso) {
        if (this.cursos == null) {
            this.cursos = new ArrayList<>();
        }
        if (curso.getAlumnos() == null) {
            curso.setAlumnos(new ArrayList<>());
        }
        cursos.add(curso);
        curso.getAlumnos().add(this);
    }

    public void removeCurso(Curso curso) {
        if (this.cursos != null && curso.getAlumnos() != null) {
            cursos.remove(curso);
            curso.getAlumnos().remove(this);
        }
    }
}