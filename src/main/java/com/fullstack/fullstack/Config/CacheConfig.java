package com.fullstack.fullstack.Config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración para habilitar el caché
 */
@Configuration
@EnableCaching
public class CacheConfig {
    // Spring Boot autoconfigura el caché por defecto
    // Podemos agregar configuraciones específicas aquí si es necesario
}
