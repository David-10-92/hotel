package com.hotel.room.dtos;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
public class DateRangeDTO {
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate checkInDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate checkOutDate;

    public DateRangeDTO() {
        this.checkInDate = LocalDate.now();
        this.checkOutDate = LocalDate.now().plusDays(1);
    }
}
