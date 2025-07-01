package com.fullstack.fullstack.Repository;

import com.fullstack.fullstack.Model.Curso;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio para la entidad Curso.
 *
 * Esta interfaz extiende JpaRepository, lo que permite realizar operaciones CRUD
 * (Crear, Leer, Actualizar y Eliminar) sobre la entidad Curso sin necesidad de implementar
 * métodos manualmente.
 *
 * JpaRepository proporciona métodos como:
 * - findAll(): Obtiene todos los cursos.
 * - findById(Long id): Busca un curso por su ID.
 * - save(Curso curso): Guarda o actualiza un curso.
 * - deleteById(Long id): Elimina un curso por su ID.
 *
 * Si se requieren consultas personalizadas, se pueden agregar métodos adicionales aquí.
 */
public interface CursoRepository extends JpaRepository<Curso, Long> {
}