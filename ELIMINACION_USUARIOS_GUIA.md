# Integración con API Externa - Eliminación de Usuarios

## Descripción General

La aplicación ahora está completamente integrada con la API externa para operaciones CRUD de usuarios/alumnos. Esto incluye la funcionalidad de eliminación.

## Endpoints de la API Externa

Basándose en la documentación Swagger de la API externa (`44.212.238.69:8080/swagger-ui/index.html`), los endpoints principales son:

### Usuarios (API Externa)
- **GET** `/api/usuarios/listar` - Obtiene todos los usuarios (formato HATEOAS)
- **GET** `/api/usuarios/encontrar/{id}` - Obtiene un usuario por ID
- **POST** `/api/usuarios/crear` - Crea un nuevo usuario
- **PUT** `/api/usuarios/actualizar/{id}` - Actualiza un usuario existente
- **DELETE** `/api/usuarios/delete/{id}` - Elimina un usuario

### Alumnos (API Local)
- **GET** `/api/alumnos` - Lista todos los alumnos (prioriza API externa)
- **GET** `/api/alumnos/{id}` - Obtiene un alumno por ID
- **POST** `/api/alumnos` - Crea un nuevo alumno
- **PUT** `/api/alumnos/{id}` - Actualiza un alumno (sincroniza con API externa)
- **DELETE** `/api/alumnos/{id}` - Elimina un alumno (sincroniza con API externa)

## Flujo de Eliminación

### 1. Eliminación de Alumno
Cuando se elimina un alumno a través de `DELETE /api/alumnos/{id}`:

1. **Paso 1**: Intenta eliminar el usuario de la API externa
   - Endpoint: `DELETE http://44.212.238.69:8080/api/usuarios/delete/{id}`
   - Si es exitoso, continúa al paso 2
   - Si falla, registra el error pero continúa

2. **Paso 2**: Elimina el registro de la base de datos local
   - Elimina el alumno de la tabla local
   - Si no existe localmente pero se eliminó de la API externa, se considera exitoso

3. **Respuesta**: 
   - `204 No Content` - Eliminación exitosa
   - `404 Not Found` - Usuario no encontrado en ningún lado
   - `500 Internal Server Error` - Error del servidor

### 2. Tolerancia a Fallos
El sistema está diseñado para ser resiliente:

- **API Externa Caída**: Si la API externa no está disponible, solo elimina localmente
- **Usuario No Existe Externamente**: Si el usuario no existe en la API externa, solo elimina localmente
- **Datos Inconsistentes**: El sistema intenta mantener sincronía pero no falla completamente

## Implementación Técnica

### UserApiService
```java
public boolean deleteUser(Long id) {
    try {
        webClient.delete()
                .uri("/api/usuarios/delete/{id}", id)
                .retrieve()
                .bodyToMono(Void.class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5)))
                .timeout(Duration.ofSeconds(30))
                .block();
        return true;
    } catch (WebClientResponseException.NotFound e) {
        log.warn("Usuario ID {} no encontrado en la API externa", id);
        return false;
    } catch (Exception e) {
        log.error("Error al eliminar usuario ID {}: {}", id, e.getMessage());
        return false;
    }
}
```

### AlumnoService
```java
public void eliminar(Long id) {
    // Intenta eliminar de la API externa
    boolean eliminadoExterno = userApiService.deleteUser(id);
    
    // Elimina de la base de datos local
    if (alumnoRepository.existsById(id)) {
        alumnoRepository.deleteById(id);
    } else if (!eliminadoExterno) {
        throw new RuntimeException("Alumno no encontrado para eliminar");
    }
}
```

## Configuración de Timeouts

Los timeouts están configurados para manejar la latencia de la API externa:

```properties
# application.properties
external.api.users.base-url=http://44.212.238.69:8080
external.api.users.timeout.connection=10000
external.api.users.timeout.read=60000
external.api.users.timeout.write=30000
```

## Logging y Monitoreo

El sistema genera logs detallados para facilitar el debugging:

```log
INFO  - Eliminando alumno ID 5
INFO  - Usuario ID 5 eliminado exitosamente en la API externa
INFO  - Alumno ID 5 eliminado de la base de datos local
```

## Pruebas

### Prueba Manual
```bash
# Eliminar un alumno
curl -X DELETE http://localhost:8081/api/alumnos/5

# Verificar que se eliminó
curl http://localhost:8081/api/alumnos/5
# Debería devolver 404 Not Found
```

### Verificar Estado de la API Externa
```bash
curl http://localhost:8081/api/alumnos/status/api-externa
```

## Consideraciones de Seguridad

1. **Autorización**: La API externa maneja su propia autorización
2. **Validación**: Se validan los IDs antes de enviar solicitudes
3. **Rate Limiting**: Se implementan reintentos con backoff exponencial
4. **Timeouts**: Se configuran timeouts apropiados para evitar bloqueos

## Troubleshooting

### Error: "Retries exhausted"
- **Causa**: La API externa está respondiendo lentamente
- **Solución**: Aumentar timeouts en `application.properties`

### Error: "Usuario no encontrado"
- **Causa**: El usuario no existe en la API externa
- **Solución**: Verificar que el ID sea correcto

### Error: "Connection timeout"
- **Causa**: Problemas de conectividad de red
- **Solución**: Verificar conectividad con `test-conectividad.bat`

## Monitoreo Recomendado

1. **Logs de Aplicación**: Monitorear errores de conexión a la API externa
2. **Métricas de Latencia**: Medir tiempo de respuesta de la API externa
3. **Health Checks**: Verificar regularmente el estado de la API externa
4. **Alertas**: Configurar alertas para cuando la API externa esté caída

## Próximos Pasos

1. Implementar caché para reducir llamadas a la API externa
2. Agregar métricas de rendimiento
3. Implementar circuit breaker para mayor resilencia
4. Agregar validación de esquemas para las respuestas de la API externa
