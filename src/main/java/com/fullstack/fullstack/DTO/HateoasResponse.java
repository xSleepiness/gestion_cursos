package com.fullstack.fullstack.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO para mapear las respuestas HATEOAS de la API externa
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class HateoasResponse<T> {
    @JsonProperty("content")
    private T content;
    
    @JsonProperty("_links")
    private Links _links;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Links {
        @JsonProperty("self")
        private Link self;
        
        @JsonProperty("actualizar")
        private Link actualizar;
        
        @JsonProperty("eliminar")
        private Link eliminar;
        
        @JsonProperty("usuarios")
        private Link usuarios;
        
        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Link {
            @JsonProperty("href")
            private String href;
        }
    }
}
