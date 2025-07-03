package com.fullstack.fullstack.Config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuración de Swagger/OpenAPI para documentación de la API
 */
@Configuration
public class SwaggerConfig {

    @Value("${spring.profiles.active:dev}")
    private String activeProfile;

    @Bean
    public OpenAPI customOpenAPI() {
        // Configurar servidor según el perfil activo
        Server server = new Server();
        switch (activeProfile) {
            case "prod":
                server.setUrl("http://localhost:8080");
                server.setDescription("Servidor de Producción");
                break;
            case "staging":
                server.setUrl("http://localhost:8082");
                server.setDescription("Servidor de Staging");
                break;
            case "test":
                server.setUrl("http://localhost:8081");
                server.setDescription("Servidor de Testing");
                break;
            default: // dev
                server.setUrl("http://localhost:8080");
                server.setDescription("Servidor de Desarrollo");
                break;
        }

        return new OpenAPI()
                .servers(List.of(server))
                .info(new Info()
                        .title("Gestión de Cursos API")
                        .description("API REST para la gestión de cursos y alumnos. " +
                                "Esta API permite crear, leer, actualizar y eliminar cursos y alumnos, " +
                                "así como gestionar las inscripciones de alumnos en cursos.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Equipo de Desarrollo")
                                .email("desarrollo@gestioncursos.com")
                                .url("https://github.com/xSleepiness/gestion-cursos"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT"))
                );
    }
}
