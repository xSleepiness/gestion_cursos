package com.fullstack.fullstack.Service;

import com.fullstack.fullstack.DTO.UserDTO;
import com.fullstack.fullstack.Model.Alumno;
import com.fullstack.fullstack.Repository.AlumnoRepository;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlumnoServiceTest {

    @Mock
    private UserApiService userApiService;

    @Mock
    private AlumnoRepository alumnoRepository;

    @InjectMocks
    private AlumnoService alumnoService;

    private UserDTO userDTO;
    private Alumno alumno;

    @BeforeEach
    void setUp() {
        // Configurar datos de prueba
        userDTO = UserDTO.builder()
                .id(1L)
                .name("Juan Pérez")
                .email("juan.perez@example.com")
                .telefono("123456789")
                .rol("STUDENT")
                .ciudad("Santiago")
                .activo(true)
                .build();

        alumno = new Alumno();
        alumno.setId(1L);
        alumno.setNombre("Juan Pérez");
        alumno.setEmail("juan.perez@example.com");
    }

    @Test
    void testObtenerTodos_ApiExternaDisponible() {
        // Arrange
        List<UserDTO> users = Arrays.asList(userDTO);
        when(userApiService.getAllUsers()).thenReturn(users);

        // Act
        List<Alumno> result = alumnoService.obtenerTodos();

        // Assert
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals("Juan Pérez", result.get(0).getNombre());
        assertEquals("juan.perez@example.com", result.get(0).getEmail());
        verify(userApiService, times(1)).getAllUsers();
    }

    @Test
    void testObtenerTodos_ApiExternaNoDisponible() {
        // Arrange
        when(userApiService.getAllUsers()).thenReturn(Arrays.asList());
        when(alumnoRepository.findAll()).thenReturn(Arrays.asList(alumno));

        // Act
        List<Alumno> result = alumnoService.obtenerTodos();

        // Assert
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals("Juan Pérez", result.get(0).getNombre());
        verify(userApiService, times(1)).getAllUsers();
        verify(alumnoRepository, times(1)).findAll();
    }

    @Test
    void testObtenerPorId_ApiExternaDisponible() {
        // Arrange
        when(userApiService.getUserById(1L)).thenReturn(Optional.of(userDTO));

        // Act
        Optional<Alumno> result = alumnoService.obtenerPorId(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Juan Pérez", result.get().getNombre());
        assertEquals("juan.perez@example.com", result.get().getEmail());
        verify(userApiService, times(1)).getUserById(1L);
    }

    @Test
    void testObtenerPorId_ApiExternaNoDisponible() {
        // Arrange
        when(userApiService.getUserById(1L)).thenReturn(Optional.empty());
        when(alumnoRepository.findById(1L)).thenReturn(Optional.of(alumno));

        // Act
        Optional<Alumno> result = alumnoService.obtenerPorId(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Juan Pérez", result.get().getNombre());
        verify(userApiService, times(1)).getUserById(1L);
        verify(alumnoRepository, times(1)).findById(1L);
    }

    @Test
    void testObtenerPorEmail_ApiExternaDisponible() {
        // Arrange
        when(userApiService.getUserByEmail("juan.perez@example.com")).thenReturn(Optional.of(userDTO));

        // Act
        Optional<Alumno> result = alumnoService.obtenerPorEmail("juan.perez@example.com");

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Juan Pérez", result.get().getNombre());
        verify(userApiService, times(1)).getUserByEmail("juan.perez@example.com");
    }

    @Test
    void testCrear_EmailNoExiste() {
        // Arrange
        when(userApiService.getUserByEmail(anyString())).thenReturn(Optional.empty());
        when(alumnoRepository.existsByEmail(anyString())).thenReturn(false);
        when(alumnoRepository.save(any(Alumno.class))).thenReturn(alumno);

        // Act
        Alumno result = alumnoService.crear(alumno);

        // Assert
        assertNotNull(result);
        assertEquals("Juan Pérez", result.getNombre());
        verify(alumnoRepository, times(1)).save(alumno);
    }

    @Test
    void testCrear_EmailExisteEnApiExterna() {
        // Arrange
        when(userApiService.getUserByEmail(anyString())).thenReturn(Optional.of(userDTO));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            alumnoService.crear(alumno);
        });

        assertTrue(exception.getMessage().contains("Ya existe un usuario con ese email en el sistema externo"));
        verify(alumnoRepository, never()).save(any(Alumno.class));
    }

    @Test
    void testActualizar_AlumnoExiste() {
        // Arrange
        when(alumnoRepository.findById(1L)).thenReturn(Optional.of(alumno));
        when(alumnoRepository.save(any(Alumno.class))).thenReturn(alumno);

        // Act
        Alumno result = alumnoService.actualizar(1L, alumno);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(alumnoRepository, times(1)).save(alumno);
    }

    @Test
    void testActualizar_AlumnoNoExiste() {
        // Arrange
        when(alumnoRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            alumnoService.actualizar(1L, alumno);
        });

        assertTrue(exception.getMessage().contains("Alumno no encontrado para actualizar"));
        verify(alumnoRepository, never()).save(any(Alumno.class));
    }

    @Test
    void testEliminar_AlumnoExiste() {
        // Arrange
        when(alumnoRepository.existsById(1L)).thenReturn(true);

        // Act
        alumnoService.eliminar(1L);

        // Assert
        verify(alumnoRepository, times(1)).deleteById(1L);
    }

    @Test
    void testEliminar_AlumnoNoExiste() {
        // Arrange
        when(alumnoRepository.existsById(1L)).thenReturn(false);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            alumnoService.eliminar(1L);
        });

        assertTrue(exception.getMessage().contains("Alumno no encontrado para eliminar"));
        verify(alumnoRepository, never()).deleteById(anyLong());
    }

    @Test
    void testExistePorEmail_ExisteEnApiExterna() {
        // Arrange
        when(userApiService.getUserByEmail("juan.perez@example.com")).thenReturn(Optional.of(userDTO));

        // Act
        boolean result = alumnoService.existePorEmail("juan.perez@example.com");

        // Assert
        assertTrue(result);
        verify(userApiService, times(1)).getUserByEmail("juan.perez@example.com");
    }

    @Test
    void testExistePorEmail_ExisteEnDatosLocales() {
        // Arrange
        when(userApiService.getUserByEmail("juan.perez@example.com")).thenReturn(Optional.empty());
        when(alumnoRepository.existsByEmail("juan.perez@example.com")).thenReturn(true);

        // Act
        boolean result = alumnoService.existePorEmail("juan.perez@example.com");

        // Assert
        assertTrue(result);
        verify(userApiService, times(1)).getUserByEmail("juan.perez@example.com");
        verify(alumnoRepository, times(1)).existsByEmail("juan.perez@example.com");
    }

    @Test
    void testExistePorEmail_NoExiste() {
        // Arrange
        when(userApiService.getUserByEmail("juan.perez@example.com")).thenReturn(Optional.empty());
        when(alumnoRepository.existsByEmail("juan.perez@example.com")).thenReturn(false);

        // Act
        boolean result = alumnoService.existePorEmail("juan.perez@example.com");

        // Assert
        assertFalse(result);
        verify(userApiService, times(1)).getUserByEmail("juan.perez@example.com");
        verify(alumnoRepository, times(1)).existsByEmail("juan.perez@example.com");
    }

    @Test
    void testIsApiExternaDisponible() {
        // Arrange
        when(userApiService.isApiAvailable()).thenReturn(true);

        // Act
        boolean result = alumnoService.isApiExternaDisponible();

        // Assert
        assertTrue(result);
        verify(userApiService, times(1)).isApiAvailable();
    }
}
