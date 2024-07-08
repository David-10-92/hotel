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
    @NotBlank(message = "El nombre de usuario es obligatorio.")
    @Size(min = 3, max = 15, message = "El nombre de usuario debe tener entre {min} y {max} caracteres.")
    private String username;
    @Email(message = "Debe proporcionar un correo electrónico válido.")
    @NotBlank(message = "El correo electrónico es obligatorio.")
    private String email;
    @NotBlank(message = "La contraseña es obligatoria.")
    @Size(min = 6, message = "La contraseña debe tener al menos {min} caracteres.")
    private String password;
    private String rol = "ROLE_CLIENT";
}
