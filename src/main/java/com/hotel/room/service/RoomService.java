package com.hotel.room.service;

import com.hotel.room.dtos.RoomDTO;
import com.hotel.room.model.Room;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface RoomService {
    Room createRoom(RoomDTO input);
    Optional<Room> editRoom(Long id, RoomDTO input);
    boolean deleteRoom(Long id);
    List<Room> getAllRooms();
    Optional<Room> getRoomById(Long id);
    List<Room> findAvailableRooms(LocalDate checkInDate, LocalDate checkOutDate);
}
