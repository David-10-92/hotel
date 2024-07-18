package com.hotel.room.service.impl;

import com.hotel.room.errors.RoomNotFoundException;
import com.hotel.room.errors.RoomServiceException;
import com.hotel.reservation.repository.ReservationRepository;
import com.hotel.room.dtos.RoomDTO;
import com.hotel.room.model.Image;
import com.hotel.room.model.Room;
import com.hotel.room.repository.ImageRepository;
import com.hotel.room.repository.RoomRepository;
import com.hotel.room.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RoomServiceImpl implements RoomService {

    @Autowired
    RoomRepository roomRepository;

    @Autowired
    ImageRepository imageRepository;

    @Autowired
    ReservationRepository reservationRepository;

    @Override
    public Room createRoom(RoomDTO input,MultipartFile mainImage) {
        if (mainImage == null || mainImage.isEmpty()) {
            throw new IllegalArgumentException("La imagen principal no fue proporcionada.");
        }
        try {
            Room room = new Room();
            String mainImagePath = "/uploads/" + mainImage.getOriginalFilename();
            room.setImage(mainImagePath);
            room.setName(input.getName());
            room.setNightPrice(input.getNightPrice());
            room.setNumbersRoom(input.getNumbersRoom());
            room.setTypeRoom(input.getTypeRoom());
            return roomRepository.save(room);
        } catch (DataAccessException e) {
            throw new RoomServiceException("Error al crear la habitación: " + e.getMessage());
        }
    }

    @Override
    public Optional<Room> editRoom(Long id, RoomDTO input,MultipartFile file) {
        Optional<Room> optionalRoom = roomRepository.findById(id);
        if (!optionalRoom.isPresent()) {
            throw new RoomNotFoundException("No se encontró la habitación con ID: " + id);
        }

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("La imagen para editar la habitación no fue proporcionada.");
        }

        return optionalRoom.map(room -> {
            updateRoomFields(room, input, file);
            return roomRepository.save(room);
        });
    }

    @Override
    public boolean deleteRoom(Long id) {
        Optional<Room> optionalRoom = roomRepository.findById(id);
        if (optionalRoom.isPresent()) {
            roomRepository.delete(optionalRoom.get());
            return true;
        } else {
            throw new RoomNotFoundException("No se encontró la habitación con ID: " + id);
        }
    }

    @Override
    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    @Override
    public Optional<Room> getRoomById(Long id) {
        return Optional.ofNullable(roomRepository.findById(id)
                .orElseThrow(() -> new RoomNotFoundException("No se encontró la habitación con ID: " + id)));
    }

    @Override
    public List<Room> findAvailableRooms(LocalDate checkInDate, LocalDate checkOutDate) {
        return roomRepository.findAvailableRooms(checkInDate, checkOutDate);
    }

    private void updateRoomFields(Room room, RoomDTO input,MultipartFile file) {
        room.setName(input.getName());
        room.setNightPrice(input.getNightPrice());
        room.setNumbersRoom(input.getNumbersRoom());
        room.setTypeRoom(input.getTypeRoom());
        String fileUrl = "/uploads/" + file.getOriginalFilename();
        room.setImage(fileUrl);
    }

    @Override
    public int countAvailableRooms(Room room, LocalDate checkInDate, LocalDate checkOutDate) {
        int reservedCount = reservationRepository.countReservationsByRoomAndDateRange(room, checkInDate, checkOutDate);
        return room.getNumbersRoom() - reservedCount;
    }

    @Override
    public void checkRoomAvailability(Room room, LocalDate checkInDate, LocalDate checkOutDate, int requestedRooms) {
        int availableRooms = countAvailableRooms(room, checkInDate, checkOutDate);
        if (requestedRooms > availableRooms) {
            throw new IllegalArgumentException("No quedan suficientes habitaciones disponibles para las fechas seleccionadas.");
        }
    }

    @Override
    public List<String> getImagesByType(Long roomId, String type) {
        Optional<Room> roomOptional = roomRepository.findById(roomId);
        if (roomOptional.isPresent()) {
            Room room = roomOptional.get();
            List<String> filteredImages = new ArrayList<>();
            for (Image image : imageRepository.findAll()) {
                if (image.getTypeImage().equals(type)) {
                    filteredImages.add(image.getImageUrl());
                }
            }
            return filteredImages;
        }
        return Collections.emptyList();
    }
}
