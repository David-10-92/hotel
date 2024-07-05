package com.hotel.reservation.service;

import com.hotel.reservation.dtos.ReservationDTO;
import com.hotel.reservation.dtos.ReservationParamsDTO;
import com.hotel.reservation.model.Reservation;
import com.hotel.user.model.User;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.util.Optional;

public interface ReservationService {
    Reservation createReservation(ReservationDTO reservationDTO);
    Optional<Reservation> getReservationById(Long id);
    Page<Reservation> getAllReservations(int page, int size);
    Optional<Reservation> updateReservation(Long id, ReservationDTO reservationDTO);
    void deleteReservation(Long id);
    Double totalPrice(LocalDate checkInDate, LocalDate checkOutDate,int numberOfRooms,Double nightPrice);
    Page<Reservation> getAllReservation(int page, int size);
    Page<Reservation> getReservationsByUser(String username, int page, int size);
    ReservationDTO prepareReservation(ReservationParamsDTO paramsDTO, User currentUser, Double totalPrice);
}