package com.hotel.room.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ImageDTO {
    private Long id;
    @NotBlank(message = "La URL de la imagen no puede estar en blanco. Por favor, proporcione una URL válida.")
    private String imageUrl;
    @NotBlank(message = "La URL de la imagen no puede estar en blanco. Por favor, proporcione una URL válida.")
    private String typeImage;
}
