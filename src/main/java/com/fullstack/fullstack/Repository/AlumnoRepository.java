package com.fullstack.fullstack.Repository;

import com.fullstack.fullstack.Model.Alumno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio para la entidad Alumno.
 *
 * Esta interfaz extiende JpaRepository, lo que permite realizar operaciones CRUD
 * (Crear, Leer, Actualizar y Eliminar) sobre la entidad Alumno sin necesidad de implementar
 * métodos manualmente.
 *
 * JpaRepository proporciona métodos como:
 * - findAll(): Obtiene todos los alumnos.
 * - findById(Long id): Busca un alumno por su ID.
 * - save(Alumno alumno): Guarda o actualiza un alumno.
 * - deleteById(Long id): Elimina un alumno por su ID.
 *
 * Métodos personalizados adicionales para consultas específicas.
 */
@Repository
public interface AlumnoRepository extends JpaRepository<Alumno, Long> {
    
    /**
     * Busca un alumno por su email.
     * @param email Email del alumno a buscar
     * @return Optional<Alumno> que puede contener o no el alumno encontrado
     */
    Optional<Alumno> findByEmail(String email);
    
    /**
     * Verifica si existe un alumno con el email especificado.
     * @param email Email a verificar
     * @return true si existe un alumno con ese email, false en caso contrario
     */
    boolean existsByEmail(String email);
}
