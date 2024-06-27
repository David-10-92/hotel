package com.hotel.user.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserDTO {

    private Long id;
    @NotBlank
    @Size(min = 3, max = 15)
    private String username;
    @Email
    @NotBlank
    private String email;
    @NotBlank(message = "La password es obligatoria, debe tener al menos 6 caracteres")
    @Size(min = 6)
    private String password;
    private String rol = "ROLE_CLIENT";
}
