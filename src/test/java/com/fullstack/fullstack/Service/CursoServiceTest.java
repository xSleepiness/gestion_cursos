package com.fullstack.fullstack.Service;

import com.fullstack.fullstack.Model.Curso;
import com.fullstack.fullstack.Repository.CursoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para CursoService usando Mockito
 */
@ExtendWith(MockitoExtension.class)
class CursoServiceTest {

    @Mock
    private CursoRepository cursoRepository;

    @InjectMocks
    private CursoService cursoService;

    private Curso cursoMock;
    private List<Curso> cursosMock;

    @BeforeEach
    void setUp() {
        // Crear datos de prueba
        cursoMock = new Curso();
        cursoMock.setId(1L);
        cursoMock.setNombre("Programación Java");
        cursoMock.setDescripcion("Curso completo de Java");
        cursoMock.setDuracion(40);

        Curso curso2 = new Curso();
        curso2.setId(2L);
        curso2.setNombre("Desarrollo Web");
        curso2.setDescripcion("Curso de desarrollo web con Spring Boot");
        curso2.setDuracion(60);

        cursosMock = Arrays.asList(cursoMock, curso2);
    }

    @Test
    void testObtenerTodos() {
        // Arrange
        when(cursoRepository.findAll()).thenReturn(cursosMock);

        // Act
        List<Curso> resultado = cursoService.obtenerTodos();

        // Assert
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals("Programación Java", resultado.get(0).getNombre());
        assertEquals("Desarrollo Web", resultado.get(1).getNombre());
        
        // Verificar que se llamó al repositorio
        verify(cursoRepository, times(1)).findAll();
    }

    @Test
    void testObtenerPorId_CursoExistente() {
        // Arrange
        when(cursoRepository.findById(1L)).thenReturn(Optional.of(cursoMock));

        // Act
        Optional<Curso> resultado = cursoService.obtenerPorId(1L);

        // Assert
        assertTrue(resultado.isPresent());
        assertEquals("Programación Java", resultado.get().getNombre());
        assertEquals(40, resultado.get().getDuracion());
        
        // Verificar que se llamó al repositorio
        verify(cursoRepository, times(1)).findById(1L);
    }

    @Test
    void testObtenerPorId_CursoNoExistente() {
        // Arrange
        when(cursoRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        Optional<Curso> resultado = cursoService.obtenerPorId(999L);

        // Assert
        assertFalse(resultado.isPresent());
        
        // Verificar que se llamó al repositorio
        verify(cursoRepository, times(1)).findById(999L);
    }

    @Test
    void testCrear() {
        // Arrange
        Curso nuevoCurso = new Curso();
        nuevoCurso.setNombre("Machine Learning");
        nuevoCurso.setDescripcion("Curso de inteligencia artificial");
        nuevoCurso.setDuracion(80);

        when(cursoRepository.save(any(Curso.class))).thenReturn(cursoMock);

        // Act
        Curso resultado = cursoService.crear(nuevoCurso);

        // Assert
        assertNotNull(resultado);
        assertEquals("Programación Java", resultado.getNombre());
        
        // Verificar que se llamó al repositorio
        verify(cursoRepository, times(1)).save(any(Curso.class));
    }

    @Test
    void testActualizar_CursoExistente() {
        // Arrange
        Curso cursoActualizado = new Curso();
        cursoActualizado.setNombre("Java Avanzado");
        cursoActualizado.setDescripcion("Curso avanzado de Java");
        cursoActualizado.setDuracion(50);

        when(cursoRepository.findById(1L)).thenReturn(Optional.of(cursoMock));
        when(cursoRepository.save(any(Curso.class))).thenReturn(cursoActualizado);

        // Act
        Optional<Curso> resultado = cursoService.actualizar(1L, cursoActualizado);

        // Assert
        assertTrue(resultado.isPresent());
        assertEquals("Java Avanzado", resultado.get().getNombre());
        assertEquals(50, resultado.get().getDuracion());
        
        // Verificar que se llamaron los métodos del repositorio
        verify(cursoRepository, times(1)).findById(1L);
        verify(cursoRepository, times(1)).save(any(Curso.class));
    }

    @Test
    void testActualizar_CursoNoExistente() {
        // Arrange
        Curso cursoActualizado = new Curso();
        cursoActualizado.setNombre("Java Avanzado");
        
        when(cursoRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        Optional<Curso> resultado = cursoService.actualizar(999L, cursoActualizado);

        // Assert
        assertFalse(resultado.isPresent());
        
        // Verificar que se llamó findById pero no save
        verify(cursoRepository, times(1)).findById(999L);
        verify(cursoRepository, never()).save(any(Curso.class));
    }

    @Test
    void testEliminar_CursoExistente() {
        // Arrange
        when(cursoRepository.existsById(1L)).thenReturn(true);
        doNothing().when(cursoRepository).deleteById(1L);

        // Act
        boolean resultado = cursoService.eliminar(1L);

        // Assert
        assertTrue(resultado);
        
        // Verificar que se llamaron los métodos del repositorio
        verify(cursoRepository, times(1)).existsById(1L);
        verify(cursoRepository, times(1)).deleteById(1L);
    }

    @Test
    void testEliminar_CursoNoExistente() {
        // Arrange
        when(cursoRepository.existsById(999L)).thenReturn(false);

        // Act
        boolean resultado = cursoService.eliminar(999L);

        // Assert
        assertFalse(resultado);
        
        // Verificar que se llamó existsById pero no deleteById
        verify(cursoRepository, times(1)).existsById(999L);
        verify(cursoRepository, never()).deleteById(anyLong());
    }

    @Test
    void testCrear_ConDatosNulos() {
        // Arrange
        Curso cursoNulo = null;

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            cursoService.crear(cursoNulo);
        });
        
        // Verificar que no se llamó al repositorio
        verify(cursoRepository, never()).save(any(Curso.class));
    }

    @Test
    void testCrear_ConNombreVacio() {
        // Arrange
        Curso cursoInvalido = new Curso();
        cursoInvalido.setNombre("");
        cursoInvalido.setDescripcion("Descripción válida");
        cursoInvalido.setDuracion(40);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            cursoService.crear(cursoInvalido);
        });
        
        // Verificar que no se llamó al repositorio
        verify(cursoRepository, never()).save(any(Curso.class));
    }

    @Test
    void testCrear_ConDuracionInvalida() {
        // Arrange
        Curso cursoInvalido = new Curso();
        cursoInvalido.setNombre("Curso válido");
        cursoInvalido.setDescripcion("Descripción válida");
        cursoInvalido.setDuracion(0); // Duración inválida

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            cursoService.crear(cursoInvalido);
        });
        
        // Verificar que no se llamó al repositorio
        verify(cursoRepository, never()).save(any(Curso.class));
    }
}
