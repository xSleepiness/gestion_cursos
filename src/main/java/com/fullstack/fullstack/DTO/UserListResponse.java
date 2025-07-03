package com.fullstack.fullstack.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * DTO para mapear la respuesta de lista de usuarios de la API externa
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserListResponse {
    private List<HateoasResponse<UserDTO>> _embedded;
    private HateoasResponse.Links _links;
}
