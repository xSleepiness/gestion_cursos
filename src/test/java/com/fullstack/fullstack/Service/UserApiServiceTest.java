package com.fullstack.fullstack.Service;

import com.fullstack.fullstack.DTO.UserDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserApiServiceTest {

    private UserApiService userApiService;

    @BeforeEach
    void setUp() {
        userApiService = new UserApiService("http://localhost:8080");
    }

    @Test
    void testGetAllUsers_WhenApiNotAvailable_ReturnsEmptyList() {
        // Act
        List<UserDTO> result = userApiService.getAllUsers();

        // Assert
        assertNotNull(result);
        // En caso de error o API no disponible, debería retornar lista vacía
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetUserById_WhenApiNotAvailable_ReturnsEmpty() {
        // Act
        Optional<UserDTO> result = userApiService.getUserById(999L);

        // Assert
        assertNotNull(result);
        // En caso de error o API no disponible, debería retornar Optional.empty()
        assertFalse(result.isPresent());
    }

    @Test
    void testGetUserByEmail_WhenApiNotAvailable_ReturnsEmpty() {
        // Act
        Optional<UserDTO> result = userApiService.getUserByEmail("nonexistent@example.com");

        // Assert
        assertNotNull(result);
        // En caso de error o API no disponible, debería retornar Optional.empty()
        assertFalse(result.isPresent());
    }

    @Test
    void testIsApiAvailable_WhenApiNotRunning_ReturnsFalse() {
        // Act
        boolean result = userApiService.isApiAvailable();

        // Assert
        // Como la API externa no está corriendo en las pruebas, debería retornar false
        assertFalse(result);
    }

    @Test
    void testUserApiService_Constructor() {
        // Act
        UserApiService service = new UserApiService("http://test.com");

        // Assert
        assertNotNull(service);
    }
}
