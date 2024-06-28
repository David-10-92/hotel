package com.hotel.room.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ImageDTO {
    private Long id;
    @NotBlank
    private String imageUrl;
    @NotBlank
    private String typeImage;
}
