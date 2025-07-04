package com.fullstack.fullstack.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * DTO para mapear la respuesta de lista de usuarios de la API externa
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserListResponse {
    @JsonProperty("_embedded")
    private List<HateoasResponse<UserDTO>> _embedded;
    
    @JsonProperty("_links")
    private HateoasResponse.Links _links;
}
