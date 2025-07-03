package com.fullstack.fullstack.Service;

import com.fullstack.fullstack.DTO.UserDTO;
import com.fullstack.fullstack.Model.Alumno;
import com.fullstack.fullstack.Repository.AlumnoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Servicio híbrido para gestionar alumnos
 * Integra datos de la API externa de usuarios con el modelo local de alumnos
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AlumnoService {

    private final UserApiService userApiService;
    private final AlumnoRepository alumnoRepository;

    /**
     * Obtiene todos los alumnos, priorizando la API externa
     * Si la API externa no está disponible, fallback a datos locales
     * @return Lista de alumnos
     */
    @Cacheable(value = "alumnos", key = "'all'")
    public List<Alumno> obtenerTodos() {
        try {
            log.info("Obteniendo todos los alumnos desde la API externa");
            
            // Intentar obtener desde la API externa
            List<UserDTO> users = userApiService.getAllUsers();
            
            if (!users.isEmpty()) {
                log.info("Obtenidos {} usuarios de la API externa, convirtiendo a alumnos", users.size());
                return users.stream()
                        .filter(user -> user.isActivo()) // Solo usuarios activos
                        .map(this::convertirUserAAlumno)
                        .collect(Collectors.toList());
            }
            
            // Fallback a datos locales si API externa no está disponible
            log.warn("API externa no disponible, usando datos locales");
            return alumnoRepository.findAll();
            
        } catch (Exception e) {
            log.error("Error al obtener alumnos: {}", e.getMessage());
            // Fallback a datos locales en caso de error
            return alumnoRepository.findAll();
        }
    }

    /**
     * Obtiene un alumno por ID, priorizando la API externa
     * @param id ID del alumno
     * @return Alumno encontrado o empty
     */
    @Cacheable(value = "alumnos", key = "#id")
    public Optional<Alumno> obtenerPorId(Long id) {
        try {
            log.info("Obteniendo alumno ID {} desde la API externa", id);
            
            // Intentar obtener desde la API externa
            Optional<UserDTO> user = userApiService.getUserById(id);
            
            if (user.isPresent() && user.get().isActivo()) {
                log.info("Alumno encontrado en API externa: {}", user.get().getName());
                return Optional.of(convertirUserAAlumno(user.get()));
            }
            
            // Fallback a datos locales
            log.warn("Alumno ID {} no encontrado en API externa, buscando en datos locales", id);
            return alumnoRepository.findById(id);
            
        } catch (Exception e) {
            log.error("Error al obtener alumno ID {}: {}", id, e.getMessage());
            // Fallback a datos locales
            return alumnoRepository.findById(id);
        }
    }

    /**
     * Busca un alumno por email, priorizando la API externa
     * @param email Email del alumno
     * @return Alumno encontrado o empty
     */
    @Cacheable(value = "alumnos", key = "#email")
    public Optional<Alumno> obtenerPorEmail(String email) {
        try {
            log.info("Buscando alumno por email {} en la API externa", email);
            
            // Intentar obtener desde la API externa
            Optional<UserDTO> user = userApiService.getUserByEmail(email);
            
            if (user.isPresent() && user.get().isActivo()) {
                log.info("Alumno encontrado por email en API externa: {}", user.get().getName());
                return Optional.of(convertirUserAAlumno(user.get()));
            }
            
            // Fallback a datos locales
            log.warn("Alumno con email {} no encontrado en API externa, buscando en datos locales", email);
            return alumnoRepository.findByEmail(email);
            
        } catch (Exception e) {
            log.error("Error al buscar alumno por email {}: {}", email, e.getMessage());
            // Fallback a datos locales
            return alumnoRepository.findByEmail(email);
        }
    }

    /**
     * Crea un nuevo alumno en la base de datos local
     * Nota: Para crear usuarios en la API externa, se debería usar directamente la API del compañero
     * @param alumno Alumno a crear
     * @return Alumno creado
     */
    public Alumno crear(Alumno alumno) {
        try {
            log.info("Creando alumno local: {}", alumno.getNombre());
            
            // Verificar si ya existe un usuario con ese email en la API externa
            Optional<UserDTO> userExterno = userApiService.getUserByEmail(alumno.getEmail());
            if (userExterno.isPresent()) {
                log.warn("Ya existe un usuario con email {} en la API externa", alumno.getEmail());
                throw new RuntimeException("Ya existe un usuario con ese email en el sistema externo");
            }
            
            // Verificar en datos locales
            if (alumnoRepository.existsByEmail(alumno.getEmail())) {
                log.warn("Ya existe un alumno con email {} en la base de datos local", alumno.getEmail());
                throw new RuntimeException("Ya existe un alumno con ese email en la base de datos local");
            }
            
            return alumnoRepository.save(alumno);
            
        } catch (Exception e) {
            log.error("Error al crear alumno: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Actualiza un alumno en la base de datos local
     * @param id ID del alumno
     * @param alumno Datos actualizados
     * @return Alumno actualizado
     */
    public Alumno actualizar(Long id, Alumno alumno) {
        try {
            log.info("Actualizando alumno ID {}", id);
            
            Optional<Alumno> existente = alumnoRepository.findById(id);
            if (existente.isPresent()) {
                alumno.setId(id);
                return alumnoRepository.save(alumno);
            } else {
                log.warn("Alumno ID {} no encontrado para actualizar", id);
                throw new RuntimeException("Alumno no encontrado para actualizar");
            }
            
        } catch (Exception e) {
            log.error("Error al actualizar alumno ID {}: {}", id, e.getMessage());
            throw e;
        }
    }

    /**
     * Elimina un alumno de la base de datos local
     * @param id ID del alumno
     */
    public void eliminar(Long id) {
        try {
            log.info("Eliminando alumno ID {}", id);
            
            if (alumnoRepository.existsById(id)) {
                alumnoRepository.deleteById(id);
            } else {
                log.warn("Alumno ID {} no encontrado para eliminar", id);
                throw new RuntimeException("Alumno no encontrado para eliminar");
            }
            
        } catch (Exception e) {
            log.error("Error al eliminar alumno ID {}: {}", id, e.getMessage());
            throw e;
        }
    }

    /**
     * Verifica si existe un alumno por email
     * @param email Email a verificar
     * @return true si existe, false en caso contrario
     */
    public boolean existePorEmail(String email) {
        // Verificar tanto en API externa como en datos locales
        Optional<UserDTO> userExterno = userApiService.getUserByEmail(email);
        return userExterno.isPresent() || alumnoRepository.existsByEmail(email);
    }

    /**
     * Convierte un UserDTO de la API externa a un Alumno local
     * @param user Usuario de la API externa
     * @return Alumno convertido
     */
    private Alumno convertirUserAAlumno(UserDTO user) {
        Alumno alumno = new Alumno();
        alumno.setId(user.getId());
        alumno.setNombre(user.getName());
        alumno.setEmail(user.getEmail());
        // Nota: Los cursos se mantienen como una lista vacía ya que vienen de la API externa
        return alumno;
    }

    /**
     * Verifica si la API externa está disponible
     * @return true si está disponible, false en caso contrario
     */
    public boolean isApiExternaDisponible() {
        return userApiService.isApiAvailable();
    }
}
