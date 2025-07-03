# Fullstack Cursos - Gestión de Cursos y Alumnos

Este proyecto es un microservicio desarrollado con **Spring Boot** para la gestión de cursos y alumnos. Permite realizar operaciones CRUD (Crear, Leer, Actualizar y Eliminar) sobre las entidades `Curso` y `Alumno`, con integración a una API externa de usuarios, almacenando la información en una base de datos relacional (MariaDB/MySQL).

## 🌟 Características Principales

- **Gestión de Cursos**: CRUD completo para cursos
- **Gestión de Alumnos**: Integración híbrida con API externa + datos locales
- **Múltiples Perfiles**: Desarrollo, Testing, Staging y Producción
- **API Externa**: Integración con API de usuarios de compañero
- **Documentación**: Swagger/OpenAPI integrado
- **Datos de Prueba**: Generación automática con DataFaker
- **Pruebas**: Unitarias y de integración con Mockito
- **Caché**: Sistema de caché para optimización
- **Resiliencia**: Fallback automático si API externa no disponible

## 🔌 Integración con API Externa

Este proyecto integra datos de alumnos con una API externa de usuarios:

- **Repositorio del Compañero**: https://github.com/demianpulgar/FullStack_I
- **Estrategia Híbrida**: Prioriza API externa, fallback a datos locales
- **Mapeo Automático**: Convierte usuarios externos en alumnos locales
- **Verificación de Estado**: Endpoint para verificar disponibilidad de API externa

### Configuración Rápida

1. **Configurar API Externa**:
```env
EXTERNAL_API_USERS_BASE_URL=http://localhost:8080
```

2. **Ejecutar API del Compañero** (puerto 8080)

3. **Ejecutar Nuestra Aplicación** (puerto 8081)

📖 **Guía Completa**: [CONFIGURACION_RAPIDA.md](CONFIGURACION_RAPIDA.md)

---

## 📁 Estructura del Proyecto

```
gestion_cursos/
│
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── fullstack/
│   │   │           └── fullstack/
│   │   │               ├── FullstackApplication.java         # Clase principal Spring Boot
│   │   │               ├── Controller/
│   │   │               │   └── CursoController.java          # Controlador REST para cursos
│   │   │               ├── Model/
│   │   │               │   └── Curso.java                    # Entidad JPA Curso
│   │   │               ├── Repository/
│   │   │               │   └── CursoRepository.java          # Repositorio JPA para Curso
│   │   │               └── Service/
│   │   │                   └── CursoService.java             # Lógica de negocio para cursos
│   │   ├── resources/
│   │   │   ├── application.properties                        # Configuración de la aplicación
│   │   │   ├── static/                                       # Recursos estáticos (opcional)
│   │   │   └── templates/                                    # Plantillas (opcional)
│   └── test/
│       └── java/
│           └── com/
│               └── fullstack/
│                   └── fullstack/
│                       └── FullstackApplicationTests.java    # Pruebas unitarias básicas
│
├── .mvn/                                                     # Archivos de configuración Maven Wrapper
├── .gitignore
├── pom.xml                                                   # Archivo de dependencias Maven
└── README.md                                                 # Readme
```

---

## 🚀 Funcionalidad

El microservicio expone una API REST para gestionar cursos con los siguientes endpoints:

- **GET `/api/cursos`**  
  Lista todos los cursos.

- **GET `/api/cursos/{id}`**  
  Obtiene un curso por su ID.

- **POST `/api/cursos`**  
  Crea un nuevo curso.

- **PUT `/api/cursos/{id}`**  
  Actualiza un curso existente.

- **DELETE `/api/cursos/{id}`**  
  Elimina un curso por su ID.

Cada curso tiene los siguientes atributos:
- `id`: Identificador único (Long)
- `nombre`: Nombre del curso (String, obligatorio)
- `descripcion`: Descripción del curso (String, opcional)
- `duracion`: Duración en horas (int, mínimo 1)

---

## 🛠️ Tecnologías y Dependencias

- **spring-boot-starter-data-jpa** // Para integración con JPA y acceso a bases de datos relacionales. //
- **spring-boot-starter-web** // Para construir APIs REST y aplicaciones web. //
- **mariadb-java-client** // Driver para conectarse a bases de datos MariaDB/MySQL. //
- **lombok** // Nos otorga métodos que nos evita el código repetitivo (getters, setters, constructores, etc.). //
- **spring-boot-starter-test** // Librerías para pruebas unitarias y de integración. //
- **spring-dotenv** // Permite cargar variables de entorno desde archivos .env. //
- **jakarta.validation-api** // Provee las anotaciones de validación como @NotBlank, @Min, etc. //

---

## ⚙️ Configuración

La configuración de la base de datos se realiza mediante variables de entorno, que puedes definir en un archivo `.env` o en tu sistema:

```
DB_URL=jdbc:mariadb://localhost:3306/tu_basededatos
DB_USER=usuario
DB_PASS=contraseña
```

El archivo `application.properties` utiliza estas variables:

```properties
spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USER}
spring.datasource.password=${DB_PASS}
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MariaDBDialect
```

---