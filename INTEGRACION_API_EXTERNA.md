# Integración con API Externa de Usuarios

## Descripción

Este proyecto ahora integra los datos de alumnos con una API externa de usuarios desarrollada por un compañero. La integración permite:

- Obtener datos de usuarios desde la API externa
- Mapear usuarios externos a alumnos locales
- Mantener un sistema híbrido con fallback a datos locales
- Caché de datos para mejorar la performance

## API Externa

### Repositorio del Compañero
- **URL**: https://github.com/demianpulgar/FullStack_I
- **Modelo User**: [User.java](https://github.com/demianpulgar/FullStack_I/blob/main/src/main/java/com/FullStack/GestionUsuarios/Model/User.java)
- **Controlador**: [UserController.java](https://github.com/demianpulgar/FullStack_I/blob/main/src/main/java/com/FullStack/GestionUsuarios/Controller/UserController.java)

### Endpoints Utilizados

| Endpoint | Método | Descripción |
|----------|--------|-------------|
| `/api/usuarios/listar` | GET | Obtiene todos los usuarios |
| `/api/usuarios/encontrar/{id}` | GET | Obtiene un usuario por ID |

## Configuración

### Variables de Entorno

Agregar al archivo `.env`:

```env
# API externa de usuarios (del compañero)
EXTERNAL_API_USERS_BASE_URL=http://localhost:8080
```

### Propiedades de Aplicación

En `application.properties`:

```properties
# Configuración de API externa de usuarios
external.api.users.base-url=${EXTERNAL_API_USERS_BASE_URL:http://localhost:8080}
```

## Arquitectura de la Integración

### 1. UserApiService

Servicio responsable de consumir la API externa:

- **Ubicación**: `com.fullstack.fullstack.Service.UserApiService`
- **Funcionalidades**:
  - Obtener todos los usuarios
  - Buscar usuario por ID
  - Buscar usuario por email
  - Verificar disponibilidad de la API
- **Características**:
  - Retry automático (3 intentos)
  - Timeout de 10 segundos
  - Caché de resultados
  - Manejo de errores robusto

### 2. AlumnoService

Servicio híbrido que integra datos externos y locales:

- **Ubicación**: `com.fullstack.fullstack.Service.AlumnoService`
- **Estrategia**:
  1. Intentar obtener datos de la API externa
  2. Si falla, utilizar datos locales como fallback
  3. Para creación/actualización, verificar ambas fuentes

### 3. DTOs

#### UserDTO
- Mapea el modelo User de la API externa
- Campos: id, name, email, telefono, rol, ciudad, activo, userPassword

#### HateoasResponse
- Maneja las respuestas HATEOAS de la API externa
- Incluye enlaces de navegación

#### UserListResponse
- Maneja listas de usuarios con formato HATEOAS

## Funcionalidades

### Obtener Alumnos

```java
// Prioriza API externa, fallback a datos locales
List<Alumno> alumnos = alumnoService.obtenerTodos();
```

### Buscar Alumno

```java
// Por ID
Optional<Alumno> alumno = alumnoService.obtenerPorId(1L);

// Por Email
Optional<Alumno> alumno = alumnoService.obtenerPorEmail("usuario@example.com");
```

### Verificar Disponibilidad

```java
boolean disponible = alumnoService.isApiExternaDisponible();
```

## Mapeo de Datos

### User (API Externa) → Alumno (Local)

```java
UserDTO user = // ... desde API externa
Alumno alumno = new Alumno();
alumno.setId(user.getId());
alumno.setNombre(user.getName());
alumno.setEmail(user.getEmail());
// Los cursos se mantienen vacíos para usuarios externos
```

## Caché

### Configuración

- **Habilitado**: `@EnableCaching` en `CacheConfig`
- **Cachés**:
  - `users`: Para datos de usuarios externos
  - `alumnos`: Para datos de alumnos procesados

### Claves de Caché

- `users::all`: Todos los usuarios
- `users::{id}`: Usuario por ID
- `users::{email}`: Usuario por email
- `alumnos::all`: Todos los alumnos
- `alumnos::{id}`: Alumno por ID
- `alumnos::{email}`: Alumno por email

## DataLoader

### Comportamiento

1. **API Externa Disponible**: Solo carga datos de cursos
2. **API Externa No Disponible**: Carga cursos y alumnos locales
3. **Datos Ya Existentes**: No carga nada

### Configuración

- **Perfiles**: Solo ejecuta en `dev` y `test`
- **Verificación**: Comprueba disponibilidad de API externa

## Pruebas

### AlumnoServiceTest

- Pruebas unitarias para el servicio híbrido
- Mocks para UserApiService y AlumnoRepository
- Cobertura de escenarios de éxito y error

### UserApiServiceTest

- Pruebas básicas del servicio de API externa
- Verificación de manejo de errores
- Pruebas de constructor y configuración

## Endpoints Adicionales

### Verificar Estado de API Externa

```http
GET /api/alumnos/status/api-externa
```

**Respuesta**:
```json
"API externa disponible"
```
o
```json
"API externa no disponible"
```

## Consideraciones

### Ventajas

1. **Resiliencia**: Fallback automático a datos locales
2. **Performance**: Caché de datos frecuentemente accedidos
3. **Flexibilidad**: Funciona con o sin API externa
4. **Mantenibilidad**: Servicios separados y bien definidos

### Limitaciones

1. **Dependencia Externa**: Requiere que la API del compañero esté disponible
2. **Sincronización**: Los datos externos no se sincronizan automáticamente
3. **Relaciones**: Los usuarios externos no tienen relaciones con cursos

### Recomendaciones

1. **Monitoreo**: Implementar monitoreo de la API externa
2. **Logs**: Revisar logs para detectar problemas de conectividad
3. **Configuración**: Ajustar timeouts según la latencia de la red
4. **Pruebas**: Probar tanto con API disponible como no disponible

## Ejemplo de Uso

### Ejecutar el Proyecto

1. **Con API Externa**:
   ```bash
   # Asegurarse de que la API del compañero esté ejecutándose
   # Configurar EXTERNAL_API_USERS_BASE_URL en .env
   ./mvnw.cmd spring-boot:run
   ```

2. **Sin API Externa**:
   ```bash
   # La aplicación funcionará con datos locales
   ./mvnw.cmd spring-boot:run
   ```

### Verificar Integración

1. **Swagger UI**: http://localhost:8081/swagger-ui.html
2. **Listar Alumnos**: GET /api/alumnos
3. **Verificar Estado**: GET /api/alumnos/status/api-externa

## Troubleshooting

### Problemas Comunes

1. **Connection Refused**: Verificar que la API externa esté ejecutándose
2. **Timeout**: Ajustar configuración de timeout en UserApiService
3. **Cache Issues**: Limpiar caché o reiniciar aplicación
4. **Datos Vacíos**: Verificar que la API externa tenga datos

### Logs Útiles

```bash
# Verificar conectividad
grep "API externa" logs/application.log

# Verificar caché
grep "cache" logs/application.log

# Verificar errores
grep "ERROR" logs/application.log
```
