package com.hotel.reservation.dtos;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class ReservationDTO {
    @NotNull
    private Long roomId;

    @NotNull
    private Long userId;

    @NotBlank(message = "El nombre del cliente no puede estar en blanco. Por favor, proporcione un nombre válido.")
    private String name;

    @NotBlank(message = "El apellido des usuario no puede estar en blanco. Por favor, proporcione un apellido de usuario válido.")
    private String username;

    @NotBlank(message = "El número de identificación (DNI) no puede estar en blanco. Por favor, proporcione un número válido.")
    private String dni;

    @NotNull
    @FutureOrPresent
    private LocalDate checkInDate;

    @NotNull
    @Future
    private LocalDate checkOutDate;
    @NotNull
    private Double nightPrice;

    @Min(value = 1, message = "El número de habitaciones debe ser como mínimo 1.")
    private int numberOfRooms;
    @NotNull
    private Double totalPrice;
}
