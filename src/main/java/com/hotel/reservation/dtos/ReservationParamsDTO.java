package com.hotel.reservation.dtos;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class ReservationParamsDTO {
    private Long roomId;
    private int numbersRoom;
    private Double nightPrice;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
}
