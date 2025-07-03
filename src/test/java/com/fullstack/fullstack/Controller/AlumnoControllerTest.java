package com.fullstack.fullstack.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fullstack.fullstack.Model.Alumno;
import com.fullstack.fullstack.Repository.AlumnoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Pruebas unitarias para AlumnoController usando Mockito y MockMvc
 */
@WebMvcTest(AlumnoController.class)
class AlumnoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AlumnoRepository alumnoRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Alumno alumnoMock;
    private List<Alumno> alumnosMock;

    @BeforeEach
    void setUp() {
        // Crear datos de prueba
        alumnoMock = new Alumno();
        alumnoMock.setId(1L);
        alumnoMock.setNombre("Juan Pérez");
        alumnoMock.setEmail("juan.perez@email.com");

        Alumno alumno2 = new Alumno();
        alumno2.setId(2L);
        alumno2.setNombre("María González");
        alumno2.setEmail("maria.gonzalez@email.com");

        alumnosMock = Arrays.asList(alumnoMock, alumno2);
    }

    @Test
    void testListar() throws Exception {
        // Arrange
        when(alumnoRepository.findAll()).thenReturn(alumnosMock);

        // Act & Assert
        mockMvc.perform(get("/api/alumnos")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].nombre", is("Juan Pérez")))
                .andExpect(jsonPath("$[0].email", is("juan.perez@email.com")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].nombre", is("María González")));

        // Verificar que se llamó al repositorio
        verify(alumnoRepository, times(1)).findAll();
    }

    @Test
    void testObtener_AlumnoExistente() throws Exception {
        // Arrange
        when(alumnoRepository.findById(1L)).thenReturn(Optional.of(alumnoMock));

        // Act & Assert
        mockMvc.perform(get("/api/alumnos/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.nombre", is("Juan Pérez")))
                .andExpect(jsonPath("$.email", is("juan.perez@email.com")));

        // Verificar que se llamó al repositorio
        verify(alumnoRepository, times(1)).findById(1L);
    }

    @Test
    void testObtener_AlumnoNoExistente() throws Exception {
        // Arrange
        when(alumnoRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/alumnos/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        // Verificar que se llamó al repositorio
        verify(alumnoRepository, times(1)).findById(999L);
    }

    @Test
    void testCrear_AlumnoValido() throws Exception {
        // Arrange
        Alumno nuevoAlumno = new Alumno();
        nuevoAlumno.setNombre("Carlos Rodríguez");
        nuevoAlumno.setEmail("carlos.rodriguez@email.com");

        Alumno alumnoCreado = new Alumno();
        alumnoCreado.setId(3L);
        alumnoCreado.setNombre("Carlos Rodríguez");
        alumnoCreado.setEmail("carlos.rodriguez@email.com");

        when(alumnoRepository.existsByEmail("carlos.rodriguez@email.com")).thenReturn(false);
        when(alumnoRepository.save(any(Alumno.class))).thenReturn(alumnoCreado);

        // Act & Assert
        mockMvc.perform(post("/api/alumnos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nuevoAlumno)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(3)))
                .andExpect(jsonPath("$.nombre", is("Carlos Rodríguez")))
                .andExpect(jsonPath("$.email", is("carlos.rodriguez@email.com")));

        // Verificar que se llamaron los métodos del repositorio
        verify(alumnoRepository, times(1)).existsByEmail("carlos.rodriguez@email.com");
        verify(alumnoRepository, times(1)).save(any(Alumno.class));
    }

    @Test
    void testCrear_EmailDuplicado() throws Exception {
        // Arrange
        Alumno alumnoConEmailDuplicado = new Alumno();
        alumnoConEmailDuplicado.setNombre("Juan Duplicado");
        alumnoConEmailDuplicado.setEmail("juan.perez@email.com");

        when(alumnoRepository.existsByEmail("juan.perez@email.com")).thenReturn(true);

        // Act & Assert
        mockMvc.perform(post("/api/alumnos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(alumnoConEmailDuplicado)))
                .andExpect(status().isBadRequest());

        // Verificar que se llamó existsByEmail pero no save
        verify(alumnoRepository, times(1)).existsByEmail("juan.perez@email.com");
        verify(alumnoRepository, never()).save(any(Alumno.class));
    }

    @Test
    void testActualizar_AlumnoExistente() throws Exception {
        // Arrange
        Alumno alumnoActualizado = new Alumno();
        alumnoActualizado.setNombre("Juan Carlos Pérez");
        alumnoActualizado.setEmail("juan.carlos.perez@email.com");

        Alumno alumnoGuardado = new Alumno();
        alumnoGuardado.setId(1L);
        alumnoGuardado.setNombre("Juan Carlos Pérez");
        alumnoGuardado.setEmail("juan.carlos.perez@email.com");

        when(alumnoRepository.findById(1L)).thenReturn(Optional.of(alumnoMock));
        when(alumnoRepository.save(any(Alumno.class))).thenReturn(alumnoGuardado);

        // Act & Assert
        mockMvc.perform(put("/api/alumnos/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(alumnoActualizado)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.nombre", is("Juan Carlos Pérez")))
                .andExpect(jsonPath("$.email", is("juan.carlos.perez@email.com")));

        // Verificar que se llamaron los métodos del repositorio
        verify(alumnoRepository, times(1)).findById(1L);
        verify(alumnoRepository, times(1)).save(any(Alumno.class));
    }

    @Test
    void testActualizar_AlumnoNoExistente() throws Exception {
        // Arrange
        Alumno alumnoActualizado = new Alumno();
        alumnoActualizado.setNombre("No Existe");

        when(alumnoRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(put("/api/alumnos/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(alumnoActualizado)))
                .andExpect(status().isNotFound());

        // Verificar que se llamó findById pero no save
        verify(alumnoRepository, times(1)).findById(999L);
        verify(alumnoRepository, never()).save(any(Alumno.class));
    }

    @Test
    void testEliminar_AlumnoExistente() throws Exception {
        // Arrange
        when(alumnoRepository.existsById(1L)).thenReturn(true);
        doNothing().when(alumnoRepository).deleteById(1L);

        // Act & Assert
        mockMvc.perform(delete("/api/alumnos/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        // Verificar que se llamaron los métodos del repositorio
        verify(alumnoRepository, times(1)).existsById(1L);
        verify(alumnoRepository, times(1)).deleteById(1L);
    }

    @Test
    void testEliminar_AlumnoNoExistente() throws Exception {
        // Arrange
        when(alumnoRepository.existsById(999L)).thenReturn(false);

        // Act & Assert
        mockMvc.perform(delete("/api/alumnos/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        // Verificar que se llamó existsById pero no deleteById
        verify(alumnoRepository, times(1)).existsById(999L);
        verify(alumnoRepository, never()).deleteById(anyLong());
    }

    @Test
    void testBuscarPorEmail_AlumnoExistente() throws Exception {
        // Arrange
        when(alumnoRepository.findByEmail("juan.perez@email.com")).thenReturn(Optional.of(alumnoMock));

        // Act & Assert
        mockMvc.perform(get("/api/alumnos/email/juan.perez@email.com")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.nombre", is("Juan Pérez")))
                .andExpect(jsonPath("$.email", is("juan.perez@email.com")));

        // Verificar que se llamó al repositorio
        verify(alumnoRepository, times(1)).findByEmail("juan.perez@email.com");
    }

    @Test
    void testBuscarPorEmail_AlumnoNoExistente() throws Exception {
        // Arrange
        when(alumnoRepository.findByEmail("noexiste@email.com")).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/alumnos/email/noexiste@email.com")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        // Verificar que se llamó al repositorio
        verify(alumnoRepository, times(1)).findByEmail("noexiste@email.com");
    }
}
