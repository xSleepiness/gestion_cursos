# Ejemplos de Uso de la API con HATEOAS

## Ejecutar la Aplicación

```bash
./mvnw.cmd spring-boot:run
```

La aplicación se ejecutará en: http://localhost:8081

## Swagger UI

Accede a la documentación interactiva: http://localhost:8081/swagger-ui.html

## Ejemplos con curl

### 1. Listar todos los cursos

```bash
curl -X GET http://localhost:8081/api/cursos \
  -H "Accept: application/json" | jq '.'
```

**Respuesta esperada:**
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
          "self": {"href": "http://localhost:8081/api/cursos/1"},
          "actualizar": {"href": "http://localhost:8081/api/cursos/1"},
          "eliminar": {"href": "http://localhost:8081/api/cursos/1"}
        }
      }
    ]
  },
  "_links": {
    "self": {"href": "http://localhost:8081/api/cursos"},
    "crear": {"href": "http://localhost:8081/api/cursos"}
  }
}
```

### 2. Obtener curso específico

```bash
curl -X GET http://localhost:8081/api/cursos/1 \
  -H "Accept: application/json" | jq '.'
```

### 3. Crear nuevo curso

```bash
curl -X POST http://localhost:8081/api/cursos \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -d '{
    "nombre": "Machine Learning",
    "descripcion": "Curso de inteligencia artificial",
    "duracion": 80
  }' | jq '.'
```

### 4. Actualizar curso

```bash
curl -X PUT http://localhost:8081/api/cursos/1 \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -d '{
    "nombre": "Java Avanzado",
    "descripcion": "Curso avanzado de Java",
    "duracion": 50
  }' | jq '.'
```

### 5. Eliminar curso

```bash
curl -X DELETE http://localhost:8081/api/cursos/1 \
  -H "Accept: application/json"
```

## Navegación Automática con HATEOAS

### Script de navegación automática

```bash
#!/bin/bash

echo "=== 1. Obtener lista de cursos ==="
RESPONSE=$(curl -s http://localhost:8081/api/cursos)
echo "$RESPONSE" | jq '.'

echo "=== 2. Extraer enlace del primer curso ==="
FIRST_COURSE_LINK=$(echo "$RESPONSE" | jq -r '._embedded.cursoList[0]._links.self.href')
echo "Enlace del primer curso: $FIRST_COURSE_LINK"

echo "=== 3. Obtener detalles del primer curso ==="
curl -s "$FIRST_COURSE_LINK" | jq '.'

echo "=== 4. Extraer enlace de actualización ==="
UPDATE_LINK=$(curl -s "$FIRST_COURSE_LINK" | jq -r '._links.actualizar.href')
echo "Enlace de actualización: $UPDATE_LINK"
```

## Ejemplos con JavaScript (Fetch API)

### Cliente JavaScript que usa HATEOAS

```javascript
class CursoApiClient {
  constructor(baseUrl) {
    this.baseUrl = baseUrl;
  }

  async getCursos() {
    const response = await fetch(`${this.baseUrl}/api/cursos`);
    return await response.json();
  }

  async getCursoById(id) {
    const response = await fetch(`${this.baseUrl}/api/cursos/${id}`);
    return await response.json();
  }

  async followLink(link) {
    const response = await fetch(link.href);
    return await response.json();
  }

  async createCurso(curso) {
    const response = await fetch(`${this.baseUrl}/api/cursos`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(curso)
    });
    return await response.json();
  }

  async updateCursoViaLink(link, curso) {
    const response = await fetch(link.href, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(curso)
    });
    return await response.json();
  }

  async deleteCursoViaLink(link) {
    const response = await fetch(link.href, {
      method: 'DELETE'
    });
    return response.ok;
  }
}

// Uso del cliente
async function ejemplo() {
  const client = new CursoApiClient('http://localhost:8081');
  
  // 1. Obtener lista de cursos
  const cursos = await client.getCursos();
  console.log('Cursos:', cursos);
  
  // 2. Seguir enlace del primer curso
  if (cursos._embedded?.cursoList?.length > 0) {
    const primerCurso = cursos._embedded.cursoList[0];
    const detalles = await client.followLink(primerCurso._links.self);
    console.log('Detalles del curso:', detalles);
    
    // 3. Actualizar usando el enlace HATEOAS
    const cursoActualizado = {
      ...detalles,
      nombre: detalles.nombre + ' - Actualizado'
    };
    
    const resultado = await client.updateCursoViaLink(
      detalles._links.actualizar, 
      cursoActualizado
    );
    console.log('Curso actualizado:', resultado);
  }
}
```

## Validación de Enlaces

### Script para validar que todos los enlaces funcionan

```bash
#!/bin/bash

BASE_URL="http://localhost:8081"

echo "=== Validando API HATEOAS ==="

# 1. Obtener lista de cursos
echo "1. Probando GET /api/cursos"
CURSOS_RESPONSE=$(curl -s $BASE_URL/api/cursos)
echo "✓ Lista obtenida"

# 2. Validar enlaces de la lista
SELF_LINK=$(echo "$CURSOS_RESPONSE" | jq -r '._links.self.href')
CREATE_LINK=$(echo "$CURSOS_RESPONSE" | jq -r '._links.crear.href')

echo "2. Validando enlace self de la lista: $SELF_LINK"
curl -s "$SELF_LINK" > /dev/null && echo "✓ Self link funciona"

# 3. Validar enlaces de cada curso
echo "3. Validando enlaces de cursos individuales"
echo "$CURSOS_RESPONSE" | jq -r '._embedded.cursoList[]._links.self.href' | while read CURSO_LINK; do
  echo "  Probando: $CURSO_LINK"
  curl -s "$CURSO_LINK" > /dev/null && echo "  ✓ Enlace funciona"
done

echo "=== Validación completada ==="
```

## Beneficios Observables

1. **Descubrimiento dinámico**: El cliente no necesita saber las URLs de antemano
2. **Navegación fluida**: Puede seguir enlaces sin construir URLs manualmente
3. **Flexibilidad**: Si cambias la estructura de URLs, los clientes siguen funcionando
4. **Estado contextual**: Los enlaces reflejan qué acciones están disponibles

## Próximos Pasos

1. **Implementar HATEOAS en AlumnoController**
2. **Agregar paginación con enlaces prev/next**
3. **Crear enlaces entre recursos relacionados (curso ↔ alumnos)**
4. **Implementar caché de enlaces para mejor rendimiento**
