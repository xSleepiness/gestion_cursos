package com.fullstack.fullstack.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO para mapear el modelo User de la API externa
 * Basado en el modelo User del compañero en:
 * https://github.com/demianpulgar/FullStack_I/blob/main/src/main/java/com/FullStack/GestionUsuarios/Model/User.java
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserDTO {
    @JsonProperty("id")
    private Long id;
    
    @JsonProperty("name")
    private String name;
    
    @JsonProperty("email")
    private String email;
    
    @JsonProperty("telefono")
    private String telefono;
    
    @JsonProperty("rol")
    private String rol;
    
    @JsonProperty("ciudad")
    private String ciudad;
    
    @JsonProperty("activo")
    private boolean activo;
    
    @JsonProperty("userPassword")
    private String userPassword;
}
