package com.hotel.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserDTO {

    @NotBlank(message = "El username es obligatorio")
    @Size(min = 3, max = 15, message = "El username debe tener entre 3 y 15 caracteres")
    private String username;
    @Email
    @NotBlank
    private String email;
    @NotBlank(message = "La password es obligatoria")
    @Size(min = 6, message = "La password debe tener al menos 6 caracteres")
    private String password;
    @NotBlank
    private String rol;
}
