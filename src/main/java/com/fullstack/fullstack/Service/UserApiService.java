package com.fullstack.fullstack.Service;

import com.fullstack.fullstack.DTO.HateoasResponse;
import com.fullstack.fullstack.DTO.UserDTO;
import com.fullstack.fullstack.DTO.UserListResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

/**
 * Servicio para consumir la API externa de usuarios
 */
@Service
@Slf4j
public class UserApiService {

    private final WebClient webClient;
    private final String apiBaseUrl;

    public UserApiService(@Value("${external.api.users.base-url}") String apiBaseUrl) {
        this.apiBaseUrl = apiBaseUrl;
        this.webClient = WebClient.builder()
                .baseUrl(apiBaseUrl)
                .build();
    }

    /**
     * Obtiene todos los usuarios de la API externa
     * @return Lista de usuarios o lista vacía si hay error
     */
    @Cacheable(value = "users", key = "'all'")
    public List<UserDTO> getAllUsers() {
        try {
            log.info("Obteniendo todos los usuarios de la API externa: {}", apiBaseUrl);
            
            UserListResponse response = webClient.get()
                    .uri("/api/usuarios/listar")
                    .retrieve()
                    .bodyToMono(UserListResponse.class)
                    .retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(2)))
                    .timeout(Duration.ofSeconds(10))
                    .block();

            if (response != null && response.get_embedded() != null) {
                List<UserDTO> users = response.get_embedded().stream()
                        .map(HateoasResponse::getContent)
                        .toList();
                
                log.info("Obtenidos {} usuarios de la API externa", users.size());
                return users;
            }
            
            log.warn("Respuesta vacía de la API externa de usuarios");
            return List.of();
            
        } catch (Exception e) {
            log.error("Error al obtener usuarios de la API externa: {}", e.getMessage());
            return List.of();
        }
    }

    /**
     * Obtiene un usuario por ID de la API externa
     * @param id ID del usuario
     * @return Usuario encontrado o empty si no existe/hay error
     */
    @Cacheable(value = "users", key = "#id")
    public Optional<UserDTO> getUserById(Long id) {
        try {
            log.info("Obteniendo usuario ID {} de la API externa", id);
            
            @SuppressWarnings("unchecked")
            HateoasResponse<UserDTO> response = webClient.get()
                    .uri("/api/usuarios/encontrar/{id}", id)
                    .retrieve()
                    .bodyToMono(HateoasResponse.class)
                    .retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(2)))
                    .timeout(Duration.ofSeconds(10))
                    .block();

            if (response != null && response.getContent() != null) {
                UserDTO user = (UserDTO) response.getContent();
                log.info("Usuario encontrado: {}", user.getName());
                return Optional.of(user);
            }
            
            log.warn("Usuario ID {} no encontrado en la API externa", id);
            return Optional.empty();
            
        } catch (WebClientResponseException.NotFound e) {
            log.warn("Usuario ID {} no encontrado en la API externa", id);
            return Optional.empty();
        } catch (Exception e) {
            log.error("Error al obtener usuario ID {} de la API externa: {}", id, e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Busca un usuario por email en la API externa
     * @param email Email del usuario
     * @return Usuario encontrado o empty si no existe/hay error
     */
    @Cacheable(value = "users", key = "#email")
    public Optional<UserDTO> getUserByEmail(String email) {
        try {
            log.info("Buscando usuario por email {} en la API externa", email);
            
            // Como la API externa no tiene endpoint directo por email, 
            // obtenemos todos y filtramos localmente
            List<UserDTO> users = getAllUsers();
            
            Optional<UserDTO> user = users.stream()
                    .filter(u -> u.getEmail().equalsIgnoreCase(email))
                    .findFirst();
            
            if (user.isPresent()) {
                log.info("Usuario encontrado por email: {}", user.get().getName());
            } else {
                log.warn("Usuario con email {} no encontrado", email);
            }
            
            return user;
            
        } catch (Exception e) {
            log.error("Error al buscar usuario por email {} en la API externa: {}", email, e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Verifica si la API externa está disponible
     * @return true si está disponible, false en caso contrario
     */
    public boolean isApiAvailable() {
        try {
            webClient.get()
                    .uri("/api/usuarios/listar")
                    .retrieve()
                    .bodyToMono(String.class)
                    .timeout(Duration.ofSeconds(5))
                    .block();
            return true;
        } catch (Exception e) {
            log.warn("API externa no está disponible: {}", e.getMessage());
            return false;
        }
    }
}
