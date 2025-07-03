# Guía de Configuración Rápida - Integración API Externa

## Paso 1: Configurar Variables de Entorno

Agregar al archivo `.env`:

```env
# API externa de usuarios (del compañero)
EXTERNAL_API_USERS_BASE_URL=http://localhost:8080
```

## Paso 2: Ejecutar API del Compañero

1. Clonar el repositorio del compañero:
```bash
git clone https://github.com/demianpulgar/FullStack_I.git
cd FullStack_I
```

2. Ejecutar la aplicación del compañero:
```bash
./mvnw spring-boot:run
```

3. Verificar que esté funcionando:
```bash
curl http://localhost:8080/api/usuarios/listar
```

## Paso 3: Ejecutar Nuestra Aplicación

1. En una nueva terminal, ir a nuestro proyecto:
```bash
cd gestion_cursos
```

2. Compilar y ejecutar:
```bash
./mvnw.cmd clean compile
./mvnw.cmd spring-boot:run
```

3. La aplicación se ejecutará en el puerto 8081 (perfil test por defecto)

## Paso 4: Verificar Integración

1. **Swagger UI**: http://localhost:8081/swagger-ui.html

2. **Verificar estado de API externa**:
```bash
curl http://localhost:8081/api/alumnos/status/api-externa
```

3. **Listar alumnos** (debería mostrar datos de la API externa):
```bash
curl http://localhost:8081/api/alumnos
```

4. **Obtener alumno por ID**:
```bash
curl http://localhost:8081/api/alumnos/1
```

## Paso 5: Probar Modo Offline

1. Detener la API del compañero (Ctrl+C)

2. Reiniciar nuestra aplicación:
```bash
./mvnw.cmd spring-boot:run
```

3. Verificar que funciona con datos locales:
```bash
curl http://localhost:8081/api/alumnos/status/api-externa
# Debería responder: "API externa no disponible"

curl http://localhost:8081/api/alumnos
# Debería mostrar datos locales generados por DataFaker
```

## Configuración de Puertos

Si la API del compañero usa un puerto diferente, actualizar `.env`:

```env
# Ejemplo: si el compañero usa puerto 8090
EXTERNAL_API_USERS_BASE_URL=http://localhost:8090
```

## Resultado Esperado

- **Con API Externa**: Datos de usuarios del compañero mostrados como alumnos
- **Sin API Externa**: Datos locales generados automáticamente
- **Endpoints**: Todos funcionando con documentación Swagger
- **Logs**: Información sobre el estado de la integración

## Comandos Útiles

```bash
# Compilar sin ejecutar
./mvnw.cmd clean compile

# Ejecutar tests
./mvnw.cmd test

# Generar JAR
./mvnw.cmd clean package

# Ejecutar JAR
java -jar target/fullstack-0.0.1-SNAPSHOT.jar

# Ver logs en tiempo real
tail -f logs/application.log
```

## Notas Importantes

1. **Puerto por defecto**: Nuestra app usa puerto 8081 (perfil test)
2. **API Externa**: Debe estar en puerto 8080 por defecto
3. **Base de Datos**: Configurada para MariaDB según requerimientos
4. **Caché**: Datos se cachean para mejor performance
5. **Fallback**: Funciona sin API externa automáticamente
