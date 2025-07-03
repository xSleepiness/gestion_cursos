package com.fullstack.fullstack.Repository;

import com.fullstack.fullstack.Model.Alumno;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas de integración para AlumnoRepository
 */
@DataJpaTest
class AlumnoRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private AlumnoRepository alumnoRepository;

    private Alumno alumnoTest;

    @BeforeEach
    void setUp() {
        // Crear un alumno de prueba
        alumnoTest = new Alumno();
        alumnoTest.setNombre("Juan Pérez");
        alumnoTest.setEmail("juan.perez@test.com");
        
        // Guardarlo en la base de datos de prueba
        entityManager.persistAndFlush(alumnoTest);
    }

    @Test
    void testFindByEmail_AlumnoExistente() {
        // Act
        Optional<Alumno> resultado = alumnoRepository.findByEmail("juan.perez@test.com");

        // Assert
        assertTrue(resultado.isPresent());
        assertEquals("Juan Pérez", resultado.get().getNombre());
        assertEquals("juan.perez@test.com", resultado.get().getEmail());
    }

    @Test
    void testFindByEmail_AlumnoNoExistente() {
        // Act
        Optional<Alumno> resultado = alumnoRepository.findByEmail("noexiste@test.com");

        // Assert
        assertFalse(resultado.isPresent());
    }

    @Test
    void testExistsByEmail_AlumnoExistente() {
        // Act
        boolean existe = alumnoRepository.existsByEmail("juan.perez@test.com");

        // Assert
        assertTrue(existe);
    }

    @Test
    void testExistsByEmail_AlumnoNoExistente() {
        // Act
        boolean existe = alumnoRepository.existsByEmail("noexiste@test.com");

        // Assert
        assertFalse(existe);
    }

    @Test
    void testSave_NuevoAlumno() {
        // Arrange
        Alumno nuevoAlumno = new Alumno();
        nuevoAlumno.setNombre("María González");
        nuevoAlumno.setEmail("maria.gonzalez@test.com");

        // Act
        Alumno alumnoGuardado = alumnoRepository.save(nuevoAlumno);

        // Assert
        assertNotNull(alumnoGuardado.getId());
        assertEquals("María González", alumnoGuardado.getNombre());
        assertEquals("maria.gonzalez@test.com", alumnoGuardado.getEmail());

        // Verificar que se guardó en la base de datos
        Optional<Alumno> alumnoEncontrado = alumnoRepository.findById(alumnoGuardado.getId());
        assertTrue(alumnoEncontrado.isPresent());
    }

    @Test
    void testDelete_AlumnoExistente() {
        // Arrange
        Long alumnoId = alumnoTest.getId();
        assertTrue(alumnoRepository.existsById(alumnoId));

        // Act
        alumnoRepository.deleteById(alumnoId);

        // Assert
        assertFalse(alumnoRepository.existsById(alumnoId));
        Optional<Alumno> alumnoEliminado = alumnoRepository.findById(alumnoId);
        assertFalse(alumnoEliminado.isPresent());
    }

    @Test
    void testFindAll() {
        // Arrange - Agregar otro alumno
        Alumno alumno2 = new Alumno();
        alumno2.setNombre("Carlos Rodríguez");
        alumno2.setEmail("carlos.rodriguez@test.com");
        entityManager.persistAndFlush(alumno2);

        // Act
        var alumnos = alumnoRepository.findAll();

        // Assert
        assertEquals(2, alumnos.size());
        assertTrue(alumnos.stream().anyMatch(a -> a.getEmail().equals("juan.perez@test.com")));
        assertTrue(alumnos.stream().anyMatch(a -> a.getEmail().equals("carlos.rodriguez@test.com")));
    }
}
