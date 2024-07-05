package com.hotel.reservation.service.impl;

import com.hotel.reservation.dtos.ReservationDTO;
import com.hotel.reservation.dtos.ReservationParamsDTO;
import com.hotel.reservation.model.Reservation;
import com.hotel.reservation.repository.ReservationRepository;
import com.hotel.reservation.service.ReservationService;
import com.hotel.room.model.Room;
import com.hotel.room.repository.RoomRepository;
import com.hotel.user.model.User;
import com.hotel.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
public class ReservationServiceImpl implements ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private UserRepository userRepository;

    @Override
    public Reservation createReservation(ReservationDTO reservationDTO) {
        Reservation reservation = new Reservation();
        reservation.setRoom(getRoomById(reservationDTO.getRoomId()));
        reservation.setUser( getUserById(reservationDTO.getUserId()));
        reservation.setCheckInDate(reservationDTO.getCheckInDate());
        reservation.setCheckOutDate(reservationDTO.getCheckOutDate());
        reservation.setNumberOfRooms(reservationDTO.getNumberOfRooms());
        reservation.setName(reservationDTO.getName());
        reservation.setUsername(reservationDTO.getUsername());
        reservation.setDni(reservationDTO.getDni());
        reservation.setTotalPrice(reservationDTO.getTotalPrice());
        reservation.setNightPrice(reservationDTO.getNightPrice());
        return reservationRepository.save(reservation);
    }

    @Override
    public ReservationDTO prepareReservation(ReservationParamsDTO paramsDTO, User currentUser, Double totalPrice) {
        ReservationDTO reservationDTO = new ReservationDTO();
        reservationDTO.setRoomId(paramsDTO.getRoomId());
        reservationDTO.setUserId(currentUser.getId());
        reservationDTO.setCheckInDate(paramsDTO.getCheckInDate());
        reservationDTO.setCheckOutDate(paramsDTO.getCheckOutDate());
        reservationDTO.setTotalPrice(totalPrice);
        reservationDTO.setNumberOfRooms(paramsDTO.getNumbersRoom());
        return reservationDTO;
    }

    @Override
    public Optional<Reservation> getReservationById(Long id) {
        return reservationRepository.findById(id);
    }

    @Override
    public Page<Reservation> getAllReservations(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Reservation> reservationPage = reservationRepository.findAll(pageable);
        List<Reservation> reservationList = reservationRepository.findAll();
        return new PageImpl<>(reservationList, pageable, reservationPage.getTotalElements());
    }

    @Override
    public Page<Reservation> getAllReservation(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return reservationRepository.findAll(pageable);
    }

    @Override
    public Optional<Reservation> updateReservation(Long id, ReservationDTO reservationDTO) {
        return reservationRepository.findById(id).map(reservation -> {
            reservation.setRoom(getRoomById(reservationDTO.getRoomId()));
            reservation.setUser(getUserById(reservationDTO.getUserId()));
            reservation.setCheckInDate(reservationDTO.getCheckInDate());
            reservation.setCheckOutDate(reservationDTO.getCheckOutDate());
            reservation.setNumberOfRooms(reservationDTO.getNumberOfRooms());
            reservation.setName(reservationDTO.getName());
            reservation.setUsername(reservationDTO.getUsername());
            reservation.setDni(reservationDTO.getDni());
            reservation.setTotalPrice(reservationDTO.getTotalPrice());
            reservation.setNightPrice(reservationDTO.getNightPrice());
            return reservationRepository.save(reservation);
        });
    }

    @Override
    public void deleteReservation(Long id) {
        Optional<Reservation> reservationOptional = reservationRepository.findById(id);
        if (reservationOptional.isPresent()) {
            Reservation reservation = reservationOptional.get();
            if (reservation.getCheckInDate().isAfter(LocalDate.now())) {
                reservationRepository.delete(reservation);
            } else {
                throw new IllegalArgumentException("Cannot delete reservation with a start date that is today or in the past.");
            }
        } else {
            throw new IllegalArgumentException("Reservation not found");
        }
    }

    @Override
    public Double totalPrice(LocalDate checkInDate, LocalDate checkOutDate, int numbersRoom, Double nightPrice) {
        long daysDifference = ChronoUnit.DAYS.between(checkInDate, checkOutDate);

        // Calcular el precio total
        Double totalPrice = nightPrice * numbersRoom * (daysDifference);

        // Redondear el precio a dos decimales
        totalPrice = Math.round(totalPrice * 100.0) / 100.0;

        return totalPrice;
    }

    private Room getRoomById(Long roomId) {
        return roomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Habitacion no encontrada con id: " + roomId));
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con id: " + userId));
    }

    @Override
    public Page<Reservation> getReservationsByUser(String username, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            return reservationRepository.findByUser(user, pageable);
        } else {
            // Retorna una página vacía, pero no Page.empty(pageable), ya que no es lo mismo
            return Page.empty();
        }
    }

}
