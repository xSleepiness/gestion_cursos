package com.fullstack.fullstack.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fullstack.fullstack.Model.Curso;
import com.fullstack.fullstack.Service.CursoService;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Pruebas unitarias para CursoController usando Mockito y MockMvc
 */
@WebMvcTest(CursoController.class)
class CursoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CursoService cursoService;

    @Autowired
    private ObjectMapper objectMapper;

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
        curso2.setDescripcion("Curso de desarrollo web");
        curso2.setDuracion(60);

        cursosMock = Arrays.asList(cursoMock, curso2);
    }

    @Test
    void testListar() throws Exception {
        // Arrange
        when(cursoService.obtenerTodos()).thenReturn(cursosMock);

        // Act & Assert
        mockMvc.perform(get("/api/cursos")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.cursoList", hasSize(2)))
                .andExpect(jsonPath("$._embedded.cursoList[0].id", is(1)))
                .andExpect(jsonPath("$._embedded.cursoList[0].nombre", is("Programación Java")))
                .andExpect(jsonPath("$._embedded.cursoList[0].duracion", is(40)))
                .andExpect(jsonPath("$._embedded.cursoList[0]._links.self.href").exists())
                .andExpect(jsonPath("$._embedded.cursoList[1].id", is(2)))
                .andExpect(jsonPath("$._embedded.cursoList[1].nombre", is("Desarrollo Web")))
                .andExpect(jsonPath("$._links.self.href").exists());

        // Verificar que se llamó al servicio
        verify(cursoService, times(1)).obtenerTodos();
    }

    @Test
    void testObtener_CursoExistente() throws Exception {
        // Arrange
        when(cursoService.obtenerPorId(1L)).thenReturn(Optional.of(cursoMock));

        // Act & Assert
        mockMvc.perform(get("/api/cursos/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.nombre", is("Programación Java")))
                .andExpect(jsonPath("$.descripcion", is("Curso completo de Java")))
                .andExpect(jsonPath("$.duracion", is(40)))
                .andExpect(jsonPath("$._links.self.href").exists())
                .andExpect(jsonPath("$._links.actualizar.href").exists())
                .andExpect(jsonPath("$._links.eliminar.href").exists())
                .andExpect(jsonPath("$._links.cursos.href").exists());

        // Verificar que se llamó al servicio
        verify(cursoService, times(1)).obtenerPorId(1L);
    }

    @Test
    void testObtener_CursoNoExistente() throws Exception {
        // Arrange
        when(cursoService.obtenerPorId(999L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/cursos/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        // Verificar que se llamó al servicio
        verify(cursoService, times(1)).obtenerPorId(999L);
    }

    @Test
    void testCrear_CursoValido() throws Exception {
        // Arrange
        Curso nuevoCurso = new Curso();
        nuevoCurso.setNombre("Machine Learning");
        nuevoCurso.setDescripcion("Curso de inteligencia artificial");
        nuevoCurso.setDuracion(80);

        Curso cursoCreado = new Curso();
        cursoCreado.setId(3L);
        cursoCreado.setNombre("Machine Learning");
        cursoCreado.setDescripcion("Curso de inteligencia artificial");
        cursoCreado.setDuracion(80);

        when(cursoService.crear(any(Curso.class))).thenReturn(cursoCreado);

        // Act & Assert
        mockMvc.perform(post("/api/cursos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nuevoCurso)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(3)))
                .andExpect(jsonPath("$.nombre", is("Machine Learning")))
                .andExpect(jsonPath("$.descripcion", is("Curso de inteligencia artificial")))
                .andExpect(jsonPath("$.duracion", is(80)))
                .andExpect(jsonPath("$._links.self.href").exists())
                .andExpect(jsonPath("$._links.actualizar.href").exists())
                .andExpect(jsonPath("$._links.eliminar.href").exists())
                .andExpect(jsonPath("$._links.cursos.href").exists());

        // Verificar que se llamó al servicio
        verify(cursoService, times(1)).crear(any(Curso.class));
    }

    @Test
    void testCrear_CursoInvalido() throws Exception {
        // Arrange
        when(cursoService.crear(any(Curso.class))).thenThrow(new RuntimeException("Error de validación"));

        Curso cursoInvalido = new Curso();
        cursoInvalido.setNombre("");
        cursoInvalido.setDuracion(-1);

        // Act & Assert
        mockMvc.perform(post("/api/cursos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cursoInvalido)))
                .andExpect(status().isBadRequest());

        // Verificar que se llamó al servicio
        verify(cursoService, times(1)).crear(any(Curso.class));
    }

    @Test
    void testActualizar_CursoExistente() throws Exception {
        // Arrange
        Curso cursoActualizado = new Curso();
        cursoActualizado.setId(1L);
        cursoActualizado.setNombre("Java Avanzado");
        cursoActualizado.setDescripcion("Curso avanzado de Java");
        cursoActualizado.setDuracion(50);

        when(cursoService.actualizar(eq(1L), any(Curso.class))).thenReturn(Optional.of(cursoActualizado));

        // Act & Assert
        mockMvc.perform(put("/api/cursos/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cursoActualizado)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.nombre", is("Java Avanzado")))
                .andExpect(jsonPath("$.descripcion", is("Curso avanzado de Java")))
                .andExpect(jsonPath("$.duracion", is(50)))
                .andExpect(jsonPath("$._links.self.href").exists())
                .andExpect(jsonPath("$._links.actualizar.href").exists())
                .andExpect(jsonPath("$._links.eliminar.href").exists())
                .andExpect(jsonPath("$._links.cursos.href").exists());

        // Verificar que se llamó al servicio
        verify(cursoService, times(1)).actualizar(eq(1L), any(Curso.class));
    }

    @Test
    void testActualizar_CursoNoExistente() throws Exception {
        // Arrange
        Curso cursoActualizado = new Curso();
        cursoActualizado.setNombre("Java Avanzado");

        when(cursoService.actualizar(eq(999L), any(Curso.class))).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(put("/api/cursos/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cursoActualizado)))
                .andExpect(status().isNotFound());

        // Verificar que se llamó al servicio
        verify(cursoService, times(1)).actualizar(eq(999L), any(Curso.class));
    }

    @Test
    void testEliminar_CursoExistente() throws Exception {
        // Arrange
        when(cursoService.eliminar(1L)).thenReturn(true);

        // Act & Assert
        mockMvc.perform(delete("/api/cursos/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        // Verificar que se llamó al servicio
        verify(cursoService, times(1)).eliminar(1L);
    }

    @Test
    void testEliminar_CursoNoExistente() throws Exception {
        // Arrange
        when(cursoService.eliminar(999L)).thenReturn(false);

        // Act & Assert
        mockMvc.perform(delete("/api/cursos/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        // Verificar que se llamó al servicio
        verify(cursoService, times(1)).eliminar(999L);
    }

    @Test
    void testEliminar_ErrorInterno() throws Exception {
        // Arrange
        when(cursoService.eliminar(1L)).thenThrow(new RuntimeException("Error de base de datos"));

        // Act & Assert
        mockMvc.perform(delete("/api/cursos/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());

        // Verificar que se llamó al servicio
        verify(cursoService, times(1)).eliminar(1L);
    }
}
