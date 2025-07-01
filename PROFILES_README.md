# Configuración de Perfiles de Spring Boot

## Perfiles Disponibles

Este proyecto tiene configurados los siguientes perfiles:

### 1. Desarrollo (dev) - **Perfil por defecto**
- **Base de datos**: MariaDB en 198.98.50.208:3306/gestion_cursos_dev
- **Puerto**: 8080
- **Características**:
  - DDL auto-update activado
  - SQL logging habilitado
  - Logging detallado para debugging
  - Pool de conexiones pequeño (5 conexiones máximo)

### 2. Testing (test)
- **Base de datos**: MariaDB en 198.98.50.208:3306/gestion_cursos_test
- **Puerto**: 8081
- **Características**:
  - Base de datos dedicada para pruebas
  - Logging mínimo para pruebas rápidas
  - Pool de conexiones reducido (3 conexiones máximo)
  - DDL create-drop para pruebas limpias

### 3. Staging (staging)
- **Base de datos**: MariaDB en 198.98.50.208:3306/gestion_cursos_staging
- **Puerto**: 8082
- **Características**:
  - DDL auto-update activado
  - SQL logging habilitado para verificación
  - Configuración intermedia entre dev y prod
  - Pool de conexiones medio (10 conexiones máximo)

### 4. Producción (prod)
- **Base de datos**: MariaDB en 198.98.50.208:3306/gestion_cursos_prod
- **Puerto**: 8080 (configurable via variable PORT_PROD)
- **Características**:
  - DDL en modo validate (solo validación, no cambios)
  - SQL logging deshabilitado
  - Logging mínimo para performance
  - Pool de conexiones grande (20 conexiones máximo)
  - Compresión HTTP habilitada
  - Información de errores restringida por seguridad

## Cómo Usar los Perfiles

### 1. Cambiar el perfil por defecto
Edita `application.properties` y cambia:
```properties
spring.profiles.active=dev
```

### 2. Ejecutar con un perfil específico desde línea de comandos
```bash
# Desarrollo
java -jar target/fullstack-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev

# Testing
java -jar target/fullstack-0.0.1-SNAPSHOT.jar --spring.profiles.active=test

# Staging
java -jar target/fullstack-0.0.1-SNAPSHOT.jar --spring.profiles.active=staging

# Producción
java -jar target/fullstack-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```

### 3. Ejecutar con Maven
```bash
# Desarrollo
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# Testing
mvn spring-boot:run -Dspring-boot.run.profiles=test

# Staging
mvn spring-boot:run -Dspring-boot.run.profiles=staging

# Producción
mvn spring-boot:run -Dspring-boot.run.profiles=prod
```

### 4. Variables de entorno (recomendado)
Crea un archivo `.env` en la raíz del proyecto con:
```bash
# Variables de entorno para todos los perfiles
DB_HOST=198.98.50.208
DB_PORT=3306
DB_USER=admin_cu
DB_PASS=DuocUc..2025

# URLs específicas por base de datos
DB_URL_DEV=jdbc:mariadb://198.98.50.208:3306/gestion_cursos_dev
DB_URL_TEST=jdbc:mariadb://198.98.50.208:3306/gestion_cursos_test
DB_URL_STAGING=jdbc:mariadb://198.98.50.208:3306/gestion_cursos_staging
DB_URL_PROD=jdbc:mariadb://198.98.50.208:3306/gestion_cursos_prod

# Puertos por perfil
PORT_DEV=8080
PORT_TEST=8081
PORT_STAGING=8082
PORT_PROD=8080
```

### 5. IDE (Eclipse/IntelliJ)
En la configuración de ejecución, añade:
```
-Dspring.profiles.active=nombre_perfil
```

## Notas Importantes

- **Desarrollo**: Perfil activo por defecto, ideal para desarrollo local
- **Testing**: Usa H2 en memoria, perfecto para pruebas unitarias e integración
- **Staging**: Ambiente de pre-producción para validaciones finales
- **Producción**: Configuración optimizada para ambiente productivo

## Estructura de Archivos
```
src/main/resources/
├── application.properties          # Configuración común y perfil por defecto
├── application-dev.properties      # Configuración de desarrollo
├── application-test.properties     # Configuración de testing
├── application-staging.properties  # Configuración de staging
└── application-prod.properties     # Configuración de producción
```

## Configuración de Bases de Datos

### Bases de Datos Requeridas
Cada perfil utiliza una base de datos separada en el mismo servidor MariaDB (198.98.50.208):

- **Desarrollo**: `gestion_cursos_dev`
- **Testing**: `gestion_cursos_test`
- **Staging**: `gestion_cursos_staging`
- **Producción**: `gestion_cursos_prod`

### Crear las Bases de Datos
Ejecuta el siguiente script SQL en tu servidor MariaDB:

```sql
CREATE DATABASE IF NOT EXISTS gestion_cursos_dev CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS gestion_cursos_test CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS gestion_cursos_staging CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS gestion_cursos_prod CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

GRANT ALL PRIVILEGES ON gestion_cursos_dev.* TO 'admin_cu'@'%';
GRANT ALL PRIVILEGES ON gestion_cursos_test.* TO 'admin_cu'@'%';
GRANT ALL PRIVILEGES ON gestion_cursos_staging.* TO 'admin_cu'@'%';
GRANT ALL PRIVILEGES ON gestion_cursos_prod.* TO 'admin_cu'@'%';

FLUSH PRIVILEGES;
```

Consulta el archivo `DATABASE_SETUP.md` para más detalles.
