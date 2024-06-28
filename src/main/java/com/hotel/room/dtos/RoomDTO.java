package com.hotel.room.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RoomDTO {
    private Long id;
    @NotBlank
    private String name;
    @NotBlank
    private String typeRoom;
    @NotBlank
    private String image;
    private List<String> images;
    @NotNull
    private Double nightPrice;
    @NotNull
    private int numbersRoom;
}
