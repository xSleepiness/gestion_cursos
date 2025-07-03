package com.fullstack.fullstack.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para mapear las respuestas HATEOAS de la API externa
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HateoasResponse<T> {
    private T content;
    private Links _links;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Links {
        private Link self;
        private Link actualizar;
        private Link eliminar;
        private Link usuarios;
        
        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class Link {
            private String href;
        }
    }
}
