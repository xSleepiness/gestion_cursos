package com.fullstack.fullstack.Service;

import com.fullstack.fullstack.DTO.UserDTO;
import com.fullstack.fullstack.DTO.UserListResponse;
import com.fullstack.fullstack.DTO.UserListHateoasResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.util.retry.Retry;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

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
        
        // Configurar HttpClient con timeouts más largos para conexiones lentas
        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000) // 10 segundos para conectar
                .doOnConnected(conn -> conn
                        .addHandlerLast(new ReadTimeoutHandler(60, TimeUnit.SECONDS))  // 60 segundos para leer
                        .addHandlerLast(new WriteTimeoutHandler(30, TimeUnit.SECONDS))); // 30 segundos para escribir
        
        this.webClient = WebClient.builder()
                .baseUrl(apiBaseUrl)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(2 * 1024 * 1024)) // 2MB buffer
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
            
            // Intentar parsear como respuesta HATEOAS con la estructura correcta
            UserListHateoasResponse response = webClient.get()
                    .uri("/api/usuarios/listar")
                    .retrieve()
                    .bodyToMono(UserListHateoasResponse.class)
                    .retryWhen(Retry.backoff(3, Duration.ofSeconds(5)).maxBackoff(Duration.ofSeconds(15)))
                    .timeout(Duration.ofSeconds(60))
                    .block();
            
            if (response != null && response.getEmbedded() != null && response.getEmbedded().getUserList() != null) {
                log.info("Obtenidos {} usuarios de la API externa (HATEOAS)", response.getEmbedded().getUserList().size());
                return response.getEmbedded().getUserList();
            }
            
            log.warn("Respuesta HATEOAS vacía o inválida de la API externa");
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
            
            // La API externa devuelve un objeto individual UserDTO con enlaces HATEOAS
            // Parsear directamente como UserDTO ya que Jackson ignora los campos desconocidos
            UserDTO user = webClient.get()
                    .uri("/api/usuarios/encontrar/{id}", id)
                    .retrieve()
                    .bodyToMono(UserDTO.class)
                    .retryWhen(Retry.backoff(3, Duration.ofSeconds(5)).maxBackoff(Duration.ofSeconds(15)))
                    .timeout(Duration.ofSeconds(60))
                    .block();
            
            if (user != null) {
                log.info("Usuario encontrado: {} (ID: {})", user.getName(), user.getId());
                return Optional.of(user);
            }
            
            log.warn("Usuario ID {} no encontrado en la API externa", id);
            return Optional.empty();
            
        } catch (WebClientResponseException.NotFound e) {
            log.warn("Usuario ID {} no encontrado en la API externa (404)", id);
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
            log.info("Verificando disponibilidad de API externa: {}", apiBaseUrl);
            webClient.get()
                    .uri("/api/usuarios/listar")
                    .retrieve()
                    .bodyToMono(String.class)
                    .timeout(Duration.ofSeconds(10))
                    .block();
            log.info("API externa está disponible");
            return true;
        } catch (Exception e) {
            log.warn("API externa no está disponible: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Método de diagnóstico para probar conectividad con diferentes timeouts
     * @return información de diagnóstico
     */
    public String diagnosticarConectividad() {
        StringBuilder diagnostico = new StringBuilder();
        diagnostico.append("=== DIAGNÓSTICO DE CONECTIVIDAD ===\n");
        diagnostico.append("API Base URL: ").append(apiBaseUrl).append("\n");
        diagnostico.append("Endpoint: /api/usuarios/listar\n\n");
        
        // Prueba 1: Verificación ultra-rápida (3 segundos)
        try {
            long startTime = System.currentTimeMillis();
            webClient.get()
                    .uri("/api/usuarios/listar")
                    .retrieve()
                    .bodyToMono(String.class)
                    .timeout(Duration.ofSeconds(3))
                    .block();
            long elapsed = System.currentTimeMillis() - startTime;
            diagnostico.append("✅ Conexión ultra-rápida (3s): OK - Tiempo: ").append(elapsed).append("ms\n");
        } catch (Exception e) {
            diagnostico.append("❌ Conexión ultra-rápida (3s): FALLÓ - ").append(e.getClass().getSimpleName()).append(": ").append(e.getMessage()).append("\n");
        }
        
        // Prueba 2: Verificación rápida (10 segundos)
        try {
            long startTime = System.currentTimeMillis();
            webClient.get()
                    .uri("/api/usuarios/listar")
                    .retrieve()
                    .bodyToMono(String.class)
                    .timeout(Duration.ofSeconds(10))
                    .block();
            long elapsed = System.currentTimeMillis() - startTime;
            diagnostico.append("✅ Conexión rápida (10s): OK - Tiempo: ").append(elapsed).append("ms\n");
        } catch (Exception e) {
            diagnostico.append("❌ Conexión rápida (10s): FALLÓ - ").append(e.getClass().getSimpleName()).append(": ").append(e.getMessage()).append("\n");
        }
        
        // Prueba 3: Verificación media (30 segundos)
        try {
            long startTime = System.currentTimeMillis();
            webClient.get()
                    .uri("/api/usuarios/listar")
                    .retrieve()
                    .bodyToMono(String.class)
                    .timeout(Duration.ofSeconds(30))
                    .block();
            long elapsed = System.currentTimeMillis() - startTime;
            diagnostico.append("✅ Conexión media (30s): OK - Tiempo: ").append(elapsed).append("ms\n");
        } catch (Exception e) {
            diagnostico.append("❌ Conexión media (30s): FALLÓ - ").append(e.getClass().getSimpleName()).append(": ").append(e.getMessage()).append("\n");
        }
        
        // Prueba 4: Verificación lenta (60 segundos)
        try {
            long startTime = System.currentTimeMillis();
            webClient.get()
                    .uri("/api/usuarios/listar")
                    .retrieve()
                    .bodyToMono(String.class)
                    .timeout(Duration.ofSeconds(60))
                    .block();
            long elapsed = System.currentTimeMillis() - startTime;
            diagnostico.append("✅ Conexión lenta (60s): OK - Tiempo: ").append(elapsed).append("ms\n");
        } catch (Exception e) {
            diagnostico.append("❌ Conexión lenta (60s): FALLÓ - ").append(e.getClass().getSimpleName()).append(": ").append(e.getMessage()).append("\n");
        }
        
        diagnostico.append("\n=== RECOMENDACIONES ===\n");
        diagnostico.append("- Si todas las pruebas fallan: Verificar conectividad de red\n");
        diagnostico.append("- Si solo fallan las rápidas: La API externa es lenta, usar timeouts largos\n");
        diagnostico.append("- Si fallan esporádicamente: Implementar reintentos con backoff\n");
        
        return diagnostico.toString();
    }

    /**
     * Método de prueba para verificar el formato de respuesta de la API externa
     * Este método debe ser llamado para diagnosticar el problema de mapeo
     */
    public void testApiResponse() {
        try {
            log.info("=== INICIANDO PRUEBA DE API EXTERNA ===");
            
            // Obtener respuesta como String crudo
            String rawResponse = webClient.get()
                    .uri("/api/usuarios/listar")
                    .retrieve()
                    .bodyToMono(String.class)
                    .timeout(Duration.ofSeconds(30))
                    .block();
            
            log.info("Respuesta cruda de la API externa:");
            log.info("Longitud: {} caracteres", rawResponse != null ? rawResponse.length() : 0);
            if (rawResponse != null && rawResponse.length() > 0) {
                // Mostrar los primeros 500 caracteres para debugging
                String preview = rawResponse.length() > 500 ? rawResponse.substring(0, 500) + "..." : rawResponse;
                log.info("Contenido: {}", preview);
                
                // Verificar si es un array JSON
                if (rawResponse.trim().startsWith("[")) {
                    log.info("La respuesta es un ARRAY JSON");
                    testArrayResponse();
                } else if (rawResponse.trim().startsWith("{")) {
                    log.info("La respuesta es un OBJETO JSON");
                    testObjectResponse();
                } else {
                    log.error("La respuesta no es JSON válido");
                }
            } else {
                log.error("Respuesta vacía o nula");
            }
            
            log.info("=== FIN PRUEBA DE API EXTERNA ===");
            
        } catch (Exception e) {
            log.error("Error en prueba de API externa: {}", e.getMessage(), e);
        }
    }
    
    private void testArrayResponse() {
        try {
            log.info("Intentando parsear como array de UserDTO...");
            UserDTO[] users = webClient.get()
                    .uri("/api/usuarios/listar")
                    .retrieve()
                    .bodyToMono(UserDTO[].class)
                    .timeout(Duration.ofSeconds(30))
                    .block();
            
            if (users != null) {
                log.info("✅ Array parsing exitoso! Usuarios encontrados: {}", users.length);
                for (int i = 0; i < Math.min(users.length, 3); i++) {
                    UserDTO user = users[i];
                    log.info("Usuario {}: ID={}, Name={}, Email={}", i+1, user.getId(), user.getName(), user.getEmail());
                }
            } else {
                log.error("❌ Array parsing falló - respuesta nula");
            }
        } catch (Exception e) {
            log.error("❌ Error parseando como array: {}", e.getMessage());
        }
    }
    
    private void testObjectResponse() {
        try {
            log.info("Intentando parsear como objeto HATEOAS...");
            UserListResponse response = webClient.get()
                    .uri("/api/usuarios/listar")
                    .retrieve()
                    .bodyToMono(UserListResponse.class)
                    .timeout(Duration.ofSeconds(30))
                    .block();
            
            if (response != null) {
                log.info("✅ Object parsing exitoso!");
                log.info("_embedded: {}", response.get_embedded() != null ? response.get_embedded().size() : "null");
                log.info("_links: {}", response.get_links() != null ? "presente" : "null");
            } else {
                log.error("❌ Object parsing falló - respuesta nula");
            }
        } catch (Exception e) {
            log.error("❌ Error parseando como objeto: {}", e.getMessage());
        }
    }

    /**
     * Elimina un usuario por ID en la API externa
     * @param id ID del usuario a eliminar
     * @return true si la eliminación fue exitosa, false si no
     */
    public boolean deleteUser(Long id) {
        try {
            log.info("Eliminando usuario ID {} en la API externa", id);
            
            webClient.delete()
                    .uri("/api/usuarios/delete/{id}", id)
                    .retrieve()
                    .bodyToMono(Void.class)
                    .retryWhen(Retry.backoff(3, Duration.ofSeconds(5)).maxBackoff(Duration.ofSeconds(15)))
                    .timeout(Duration.ofSeconds(30))
                    .block();
            
            log.info("Usuario ID {} eliminado exitosamente en la API externa", id);
            return true;
            
        } catch (WebClientResponseException.NotFound e) {
            log.warn("Usuario ID {} no encontrado en la API externa para eliminar", id);
            return false;
        } catch (Exception e) {
            log.error("Error al eliminar usuario ID {} en la API externa: {}", id, e.getMessage());
            return false;
        }
    }

    /**
     * Actualiza un usuario en la API externa
     * @param id ID del usuario a actualizar
     * @param userDTO Datos del usuario actualizado
     * @return Usuario actualizado o empty si hay error
     */
    public Optional<UserDTO> updateUser(Long id, UserDTO userDTO) {
        try {
            log.info("Actualizando usuario ID {} en la API externa", id);
            
            UserDTO updatedUser = webClient.put()
                    .uri("/api/usuarios/actualizar/{id}", id)
                    .bodyValue(userDTO)
                    .retrieve()
                    .bodyToMono(UserDTO.class)
                    .retryWhen(Retry.backoff(3, Duration.ofSeconds(5)).maxBackoff(Duration.ofSeconds(15)))
                    .timeout(Duration.ofSeconds(30))
                    .block();
            
            if (updatedUser != null) {
                log.info("Usuario ID {} actualizado exitosamente en la API externa", id);
                return Optional.of(updatedUser);
            } else {
                log.warn("Respuesta vacía al actualizar usuario ID {} en la API externa", id);
                return Optional.empty();
            }
            
        } catch (WebClientResponseException.NotFound e) {
            log.warn("Usuario ID {} no encontrado en la API externa para actualizar", id);
            return Optional.empty();
        } catch (Exception e) {
            log.error("Error al actualizar usuario ID {} en la API externa: {}", id, e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Crea un nuevo usuario en la API externa
     * @param userDTO Datos del usuario a crear
     * @return Usuario creado o empty si hay error
     */
    public Optional<UserDTO> createUser(UserDTO userDTO) {
        try {
            log.info("Creando nuevo usuario en la API externa: {}", userDTO.getName());
            
            UserDTO createdUser = webClient.post()
                    .uri("/api/usuarios/crear")
                    .bodyValue(userDTO)
                    .retrieve()
                    .bodyToMono(UserDTO.class)
                    .retryWhen(Retry.backoff(3, Duration.ofSeconds(5)).maxBackoff(Duration.ofSeconds(15)))
                    .timeout(Duration.ofSeconds(30))
                    .block();
            
            if (createdUser != null) {
                log.info("Usuario creado exitosamente en la API externa con ID: {}", createdUser.getId());
                return Optional.of(createdUser);
            } else {
                log.warn("Respuesta vacía al crear usuario en la API externa");
                return Optional.empty();
            }
            
        } catch (Exception e) {
            log.error("Error al crear usuario en la API externa: {}", e.getMessage());
            return Optional.empty();
        }
    }
}
