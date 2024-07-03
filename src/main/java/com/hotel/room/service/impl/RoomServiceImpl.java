package com.hotel.room.service.impl;

import com.hotel.reservation.repository.ReservationRepository;
import com.hotel.room.dtos.RoomDTO;
import com.hotel.room.model.Image;
import com.hotel.room.model.Room;
import com.hotel.room.repository.ImageRepository;
import com.hotel.room.repository.RoomRepository;
import com.hotel.room.service.RoomService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    public Room createRoom(RoomDTO input) {
        Room room = new Room();
        room.setName(input.getName());
        room.setNightPrice(input.getNightPrice());
        room.setNumbersRoom(input.getNumbersRoom());

        imageRepository.findByImageUrl("/uploads/" + input.getImage() + ".jpg").ifPresentOrElse(image -> {
            room.setImage(image.getImageUrl());
        }, () -> {
            throw new EntityNotFoundException("Imagen no encontrada");
        });

        room.setTypeRoom(input.getTypeRoom());

        List<String> imageUrls = imageRepository.findByTypeImage(room.getTypeRoom())
                .stream()
                .map(Image::getImageUrl)
                .toList();
        room.setImages(imageUrls);

        return roomRepository.save(room);
    }

    @Override
    public Optional<Room> editRoom(Long id, RoomDTO input) {
        return roomRepository.findById(id).map(room -> {
            updateRoomFields(room, input);
            return roomRepository.save(room);
        });
    }

    @Override
    public boolean deleteRoom(Long id) {
        return roomRepository.findById(id).map(room -> {
                    roomRepository.delete(room);
                    return true;
                }).orElse(false);
    }

    @Override
    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    @Override
    public Optional<Room> getRoomById(Long id) {
        return roomRepository.findById(id);
    }

    @Override
    public List<Room> findAvailableRooms(LocalDate checkInDate, LocalDate checkOutDate) {
        List<Room> allRooms = roomRepository.findAll();
        return allRooms.stream()
                .filter(room -> isRoomAvailable(room, checkInDate, checkOutDate))
                .collect(Collectors.toList());
    }

    private void updateRoomFields(Room room, RoomDTO input) {
        room.setName(input.getName());
        room.setNightPrice(input.getNightPrice());
        room.setNumbersRoom(input.getNumbersRoom());
        room.setTypeRoom(input.getTypeRoom());
        room.setImage(input.getImage());
    }

    private boolean isRoomAvailable(Room room, LocalDate checkInDate, LocalDate checkOutDate) {
        int reservedCount = reservationRepository.countReservationsByRoomAndDateRange(room, checkInDate, checkOutDate);
        return reservedCount < room.getNumbersRoom();
    }

    @Override
    public int countAvailableRooms(Room room, LocalDate checkInDate, LocalDate checkOutDate) {
        int reservedCount = reservationRepository.countReservationsByRoomAndDateRange(room, checkInDate, checkOutDate);
        return room.getNumbersRoom() - reservedCount;
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
