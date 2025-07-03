package com.fullstack.fullstack.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;

/**
 * DTO para mapear el modelo User de la API externa
 * Basado en el modelo User del compañero en:
 * https://github.com/demianpulgar/FullStack_I/blob/main/src/main/java/com/FullStack/GestionUsuarios/Model/User.java
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {
    private Long id;
    private String name;
    private String email;
    private String telefono;
    private String rol;
    private String ciudad;
    private boolean activo;
    private String userPassword;
}
