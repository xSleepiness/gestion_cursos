# Solución de Problemas de Timeout con API Externa

## Problema Detectado

Tu aplicación puede conectarse a la API externa (IP: `44.212.238.69:8080`) pero experimenta timeouts al obtener datos.

## Configuraciones Aplicadas

### 1. Timeouts Aumentados
- **Timeout de conexión**: 30 segundos (aumentado desde 10s)
- **Timeout de respuesta**: 30 segundos
- **Reintentos**: 2 intentos con 3 segundos de espera entre cada uno

### 2. Buffer de Memoria
- **Tamaño de buffer**: 2MB (aumentado desde 1MB)
- Permite manejar respuestas más grandes sin problemas de memoria

### 3. Configuración del Servidor
- **Timeout de request async**: 60 segundos
- **Timeout de conexión Tomcat**: 30 segundos

## Diagnóstico de Problemas

### Usar el Endpoint de Diagnóstico

```bash
# Ejecutar diagnóstico de conectividad
curl http://localhost:8081/api/alumnos/diagnostico/conectividad
```

Este endpoint ejecuta 3 pruebas:
1. **Conexión rápida (5s)**: Para detectar problemas inmediatos
2. **Conexión media (15s)**: Para problemas de latencia normal
3. **Conexión lenta (30s)**: Para problemas de red lentos

### Verificar Estado de API

```bash
# Verificar si la API está disponible
curl http://localhost:8081/api/alumnos/status/api-externa
```

## Posibles Causas de Timeout

### 1. **Latencia de Red**
- **Problema**: La conexión a AWS desde tu ubicación tiene alta latencia
- **Solución**: Los timeouts aumentados deberían resolver esto

### 2. **Carga del Servidor Externo**
- **Problema**: El servidor de tu compañero está sobrecargado
- **Solución**: Los reintentos automáticos ayudan con problemas temporales

### 3. **Tamaño de Respuesta**
- **Problema**: La respuesta tiene muchos usuarios y es muy grande
- **Solución**: Buffer aumentado a 2MB

### 4. **Firewall/Proxy**
- **Problema**: Tu red tiene restricciones para conexiones externas
- **Solución**: Verificar con administrador de red

## Pasos de Troubleshooting

### Paso 1: Verificar Conectividad Básica

```bash
# Desde tu máquina, verificar si puedes alcanzar la API
curl -v http://44.212.238.69:8080/api/usuarios/listar
```

### Paso 2: Probar con Timeout Manual

```bash
# Probar con timeout de 30 segundos
curl --max-time 30 http://44.212.238.69:8080/api/usuarios/listar
```

### Paso 3: Usar el Diagnóstico Integrado

```bash
# Ejecutar nuestra aplicación
./mvnw.cmd spring-boot:run

# En otra terminal
curl http://localhost:8081/api/alumnos/diagnostico/conectividad
```

### Paso 4: Revisar Logs

```bash
# Ver logs de la aplicación
grep "API externa" logs/application.log
grep "timeout" logs/application.log
grep "ERROR" logs/application.log
```

## Configuración de Emergencia

Si los problemas persisten, puedes configurar un timeout aún mayor:

### En `application.properties`:

```properties
# Timeouts más largos para conexiones muy lentas
spring.mvc.async.request-timeout=120000
server.tomcat.connection-timeout=60000
```

### Variables de Entorno Temporales:

```bash
# Ejecutar con timeouts extendidos
export EXTERNAL_API_TIMEOUT=60
./mvnw.cmd spring-boot:run
```

## Modo Fallback

Si la API externa continúa fallando, la aplicación automáticamente:

1. **Detecta el timeout**
2. **Registra el error en logs**
3. **Cambia a datos locales**
4. **Carga datos de prueba con DataFaker**

Esto garantiza que tu aplicación siempre funcione, incluso sin la API externa.

## Monitoreo Continuo

### Endpoint de Salud Personalizado

```bash
# Verificar estado cada 30 segundos
watch -n 30 curl -s http://localhost:8081/api/alumnos/status/api-externa
```

### Logs en Tiempo Real

```bash
# Monitorear logs de conectividad
tail -f logs/application.log | grep -E "(API externa|timeout|ERROR)"
```

## Resultados Esperados

Después de estos cambios:

1. **Timeouts reducidos**: Menos errores de timeout
2. **Mejor manejo de errores**: Fallback automático
3. **Diagnóstico claro**: Información detallada sobre problemas
4. **Logs informativos**: Mayor visibilidad del estado de conexión

## Próximos Pasos

1. **Ejecutar la aplicación** con las nuevas configuraciones
2. **Probar el endpoint de diagnóstico**
3. **Verificar que los datos de alumnos se obtengan correctamente**
4. **Monitorear logs** para detectar patrones de error

Si los problemas persisten, considera:
- Contactar al administrador de la red de tu compañero
- Verificar si hay restricciones de firewall
- Probar desde una red diferente
- Implementar un proxy/cache local
