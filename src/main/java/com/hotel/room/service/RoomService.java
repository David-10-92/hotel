package com.hotel.room.service;

import com.hotel.room.dtos.RoomDTO;
import com.hotel.room.model.Room;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface RoomService {
    Room createRoom(RoomDTO input,MultipartFile mainImage);
    Optional<Room> editRoom(Long id, RoomDTO input,MultipartFile file);
    boolean deleteRoom(Long id);
    List<Room> getAllRooms();
    Optional<Room> getRoomById(Long id);
    List<Room> findAvailableRooms(LocalDate checkInDate, LocalDate checkOutDate);
    int countAvailableRooms(Room room, LocalDate checkInDate, LocalDate checkOutDate);
    List<String> getImagesByType(Long roomId, String type);
    void checkRoomAvailability(Room room, LocalDate checkInDate, LocalDate checkOutDate, int requestedRooms);
}
