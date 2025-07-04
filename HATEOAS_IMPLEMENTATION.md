# HATEOAS - Hypermedia as the Engine of Application State

## Descripción

El proyecto ahora implementa HATEOAS en el `CursoController`, lo que significa que todas las respuestas incluyen enlaces de navegación (href) que permiten al cliente descubrir qué acciones están disponibles.

## Beneficios de HATEOAS

1. **Autodescubrimiento**: Los clientes pueden navegar por la API sin conocimiento previo de la estructura
2. **Evolución de la API**: Se pueden cambiar URLs sin romper clientes existentes
3. **Estado de la aplicación**: El servidor guía al cliente sobre qué acciones están disponibles
4. **Estándar REST**: Cumple con el nivel 3 del modelo de madurez REST de Richardson

## Ejemplos de Respuestas

### 1. Listar todos los cursos

**Request:**
```http
GET /api/cursos
Accept: application/json
```

**Response:**
```json
{
  "_embedded": {
    "cursoList": [
      {
        "id": 1,
        "nombre": "Programación Java",
        "descripcion": "Curso completo de Java",
        "duracion": 40,
        "_links": {
          "self": {
            "href": "http://localhost:8081/api/cursos/1"
          },
          "actualizar": {
            "href": "http://localhost:8081/api/cursos/1"
          },
          "eliminar": {
            "href": "http://localhost:8081/api/cursos/1"
          }
        }
      },
      {
        "id": 2,
        "nombre": "Desarrollo Web",
        "descripcion": "Curso de desarrollo web",
        "duracion": 60,
        "_links": {
          "self": {
            "href": "http://localhost:8081/api/cursos/2"
          },
          "actualizar": {
            "href": "http://localhost:8081/api/cursos/2"
          },
          "eliminar": {
            "href": "http://localhost:8081/api/cursos/2"
          }
        }
      }
    ]
  },
  "_links": {
    "self": {
      "href": "http://localhost:8081/api/cursos"
    },
    "crear": {
      "href": "http://localhost:8081/api/cursos"
    }
  }
}
```

### 2. Obtener curso por ID

**Request:**
```http
GET /api/cursos/1
Accept: application/json
```

**Response:**
```json
{
  "id": 1,
  "nombre": "Programación Java",
  "descripcion": "Curso completo de Java",
  "duracion": 40,
  "_links": {
    "self": {
      "href": "http://localhost:8081/api/cursos/1"
    },
    "actualizar": {
      "href": "http://localhost:8081/api/cursos/1"
    },
    "eliminar": {
      "href": "http://localhost:8081/api/cursos/1"
    },
    "cursos": {
      "href": "http://localhost:8081/api/cursos"
    }
  }
}
```

### 3. Crear nuevo curso

**Request:**
```http
POST /api/cursos
Content-Type: application/json

{
  "nombre": "Machine Learning",
  "descripcion": "Curso de inteligencia artificial",
  "duracion": 80
}
```

**Response:**
```json
{
  "id": 3,
  "nombre": "Machine Learning",
  "descripcion": "Curso de inteligencia artificial",
  "duracion": 80,
  "_links": {
    "self": {
      "href": "http://localhost:8081/api/cursos/3"
    },
    "actualizar": {
      "href": "http://localhost:8081/api/cursos/3"
    },
    "eliminar": {
      "href": "http://localhost:8081/api/cursos/3"
    },
    "cursos": {
      "href": "http://localhost:8081/api/cursos"
    }
  }
}
```

### 4. Actualizar curso

**Request:**
```http
PUT /api/cursos/1
Content-Type: application/json

{
  "nombre": "Java Avanzado",
  "descripcion": "Curso avanzado de Java",
  "duracion": 50
}
```

**Response:**
```json
{
  "id": 1,
  "nombre": "Java Avanzado",
  "descripcion": "Curso avanzado de Java",
  "duracion": 50,
  "_links": {
    "self": {
      "href": "http://localhost:8081/api/cursos/1"
    },
    "actualizar": {
      "href": "http://localhost:8081/api/cursos/1"
    },
    "eliminar": {
      "href": "http://localhost:8081/api/cursos/1"
    },
    "cursos": {
      "href": "http://localhost:8081/api/cursos"
    }
  }
}
```

## Enlaces Disponibles

### Enlaces de Recursos Individuales

- **self**: Enlace al propio recurso
- **actualizar**: Enlace para actualizar el recurso (PUT)
- **eliminar**: Enlace para eliminar el recurso (DELETE)
- **cursos**: Enlace para volver a la lista de cursos

### Enlaces de Colecciones

- **self**: Enlace a la propia colección
- **crear**: Enlace para crear un nuevo recurso (POST)

## Navegación por la API

Con HATEOAS, un cliente puede navegar por toda la API comenzando desde un solo endpoint:

1. **Inicio**: `GET /api/cursos` - Obtiene la lista y descubre enlaces
2. **Detalle**: Sigue enlace `self` de cualquier curso
3. **Actualización**: Sigue enlace `actualizar` para modificar
4. **Eliminación**: Sigue enlace `eliminar` para borrar
5. **Vuelta**: Sigue enlace `cursos` para volver a la lista
6. **Creación**: Sigue enlace `crear` para añadir nuevo curso

## Ventajas para el Cliente

1. **No necesita conocer URLs**: Los enlaces se proporcionan dinámicamente
2. **Descubrimiento automático**: Sabe qué acciones están disponibles
3. **Flexibilidad**: La API puede cambiar URLs sin romper el cliente
4. **Estado contextual**: Los enlaces reflejan el estado actual del recurso

## Integración con Swagger

HATEOAS se integra perfectamente con Swagger/OpenAPI:

- **Swagger UI**: http://localhost:8081/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8081/api-docs
- **Documentación**: Automáticamente actualizada con los nuevos tipos de respuesta

## Consideraciones de Rendimiento

- **Tamaño de respuesta**: Las respuestas son más grandes debido a los enlaces
- **Navegabilidad**: Los clientes pueden ser más eficientes al navegar
- **Caché**: Los enlaces pueden ser cacheados para mejorar rendimiento

## Compatibilidad

- **Clientes REST**: Pueden ignorar los enlaces `_links` si no los necesitan
- **Nuevos clientes**: Pueden aprovechar completamente HATEOAS
- **Evolución**: La API puede evolucionar manteniendo compatibilidad

## Testeo

Las pruebas unitarias ahora verifican:
- La presencia de enlaces `_links`
- La estructura correcta de `_embedded` para colecciones
- Los href correctos para cada tipo de enlace
- La navegabilidad entre recursos

## Próximos Pasos

1. **Alumnos HATEOAS**: Se puede implementar HATEOAS también en `AlumnoController`
2. **Relaciones**: Agregar enlaces entre cursos y alumnos
3. **Paginación**: Implementar enlaces de paginación para listas grandes
4. **Versionado**: Usar HATEOAS para manejar versionado de API
