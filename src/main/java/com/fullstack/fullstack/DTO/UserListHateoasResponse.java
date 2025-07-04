package com.fullstack.fullstack.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * DTO para manejar la respuesta HATEOAS de la API externa de usuarios
 */
@Data
public class UserListHateoasResponse {
    
    @JsonProperty("_embedded")
    private Embedded embedded;
    
    @JsonProperty("_links")
    private Object links; // Puedes expandir esto si necesitas los links
    
    @Data
    public static class Embedded {
        @JsonProperty("userList")
        private List<UserDTO> userList;
    }
}
