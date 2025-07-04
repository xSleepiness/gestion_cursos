@echo off
echo ============================================
echo   PRUEBAS DE CONECTIVIDAD API EXTERNA
echo ============================================
echo.

echo [1/5] Verificando conectividad básica...
ping -n 1 44.212.238.69 > nul
if %errorlevel% == 0 (
    echo ✅ Ping al servidor: OK
) else (
    echo ❌ Ping al servidor: FALLO
)

echo.
echo [2/5] Verificando puerto 8080...
timeout /t 1 /nobreak > nul
telnet 44.212.238.69 8080 > nul 2>&1
if %errorlevel% == 0 (
    echo ✅ Puerto 8080: ACCESIBLE
) else (
    echo ❌ Puerto 8080: NO ACCESIBLE
)

echo.
echo [3/5] Verificando conectividad HTTP rápida...
curl -s --max-time 5 http://44.212.238.69:8080/api/usuarios/listar > nul
if %errorlevel% == 0 (
    echo ✅ HTTP rápido (5s): OK
) else (
    echo ❌ HTTP rápido (5s): TIMEOUT
)

echo.
echo [4/5] Verificando conectividad HTTP lenta...
curl -s --max-time 30 http://44.212.238.69:8080/api/usuarios/listar > nul
if %errorlevel% == 0 (
    echo ✅ HTTP lento (30s): OK
) else (
    echo ❌ HTTP lento (30s): TIMEOUT
)

echo.
echo [5/5] Iniciando aplicación Spring Boot...
echo.
echo ============================================
echo   INSTRUCCIONES DE PRUEBA
echo ============================================
echo.
echo Una vez que la aplicación esté ejecutándose, probar:
echo.
echo   1. Estado API externa:
echo      curl http://localhost:8081/api/alumnos/status/api-externa
echo.
echo   2. Diagnóstico completo:
echo      curl http://localhost:8081/api/alumnos/diagnostico/conectividad
echo.
echo   3. Listar alumnos (con fallback):
echo      curl http://localhost:8081/api/alumnos
echo.
echo   4. Verificar logs en tiempo real:
echo      Observar la consola de Spring Boot para ver timeouts
echo.
echo ============================================
echo Presiona cualquier tecla para continuar...
pause > nul
echo.
echo Iniciando aplicación...
