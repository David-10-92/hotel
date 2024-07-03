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

    @NotBlank
    private String name;

    @NotBlank
    private String username;

    @NotBlank
    private String dni;

    @NotNull
    @FutureOrPresent
    private LocalDate checkInDate;

    @NotNull
    @Future
    private LocalDate checkOutDate;
    private Double nightPrice;

    @Min(1)
    private int numberOfRooms;
    @NotNull
    private Double totalPrice;
}
