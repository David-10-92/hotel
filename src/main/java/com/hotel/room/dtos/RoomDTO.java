package com.hotel.room.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
public class RoomDTO {
    private Long id;
    @NotBlank(message = "El nombre de la habitación no puede estar en blanco. Por favor, proporcione un nombre válido.")
    private String name;
    @NotBlank(message = "El tipo de la habitación no puede estar en blanco. Por favor, especifique el tipo de la habitación.")
    private String typeRoom;
    @NotNull(message = "El precio por noche no puede ser nulo. Por favor, proporcione un precio válido.")
    private Double nightPrice;
    @NotNull(message = "El número de habitaciones no puede ser nulo. Por favor, proporcione un número válido de habitaciones.")
    private int numbersRoom;
}
