# Scripts SQL para crear las bases de datos

## Conectarse a MariaDB como administrador y ejecutar:

```sql
-- Crear bases de datos para cada perfil
CREATE DATABASE IF NOT EXISTS gestion_cursos_dev CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS gestion_cursos_test CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS gestion_cursos_staging CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS gestion_cursos_prod CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Otorgar permisos al usuario admin_cu para todas las bases de datos
GRANT ALL PRIVILEGES ON gestion_cursos_dev.* TO 'admin_cu'@'%';
GRANT ALL PRIVILEGES ON gestion_cursos_test.* TO 'admin_cu'@'%';
GRANT ALL PRIVILEGES ON gestion_cursos_staging.* TO 'admin_cu'@'%';
GRANT ALL PRIVILEGES ON gestion_cursos_prod.* TO 'admin_cu'@'%';

-- Aplicar cambios
FLUSH PRIVILEGES;

-- Verificar las bases de datos creadas
SHOW DATABASES LIKE 'gestion_cursos_%';
```

## Comando para ejecutar desde terminal:

```bash
# Conectar a MariaDB y ejecutar el script
mysql -h 198.98.50.208 -u admin_cu -p'DuocUc..2025' < database_setup.sql
```

## Verificación de conexión por perfil:

```bash
# Desarrollo
mysql -h 198.98.50.208 -u admin_cu -p'DuocUc..2025' gestion_cursos_dev

# Testing
mysql -h 198.98.50.208 -u admin_cu -p'DuocUc..2025' gestion_cursos_test

# Staging
mysql -h 198.98.50.208 -u admin_cu -p'DuocUc..2025' gestion_cursos_staging

# Producción
mysql -h 198.98.50.208 -u admin_cu -p'DuocUc..2025' gestion_cursos_prod
```
