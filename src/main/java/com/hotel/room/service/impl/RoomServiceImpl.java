package com.hotel.room.service.impl;

import com.hotel.reservation.repository.ReservationRepository;
import com.hotel.room.dtos.RoomDTO;
import com.hotel.room.model.Image;
import com.hotel.room.model.Room;
import com.hotel.room.repository.ImageRepository;
import com.hotel.room.repository.RoomRepository;
import com.hotel.room.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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
        Optional<Image> image = imageRepository.findByImageUrl("/uploads/" + input.getImage() + ".jpg");
        String url = image.get().getImageUrl();
        room.setImage(url);
        room.setTypeRoom(input.getTypeRoom());
        List<Image> images = imageRepository.findByTypeImage(room.getTypeRoom());
        List<String> imageUrls = images.stream()
                .map(Image::getImageUrl)
                .toList();
        room.setImages(imageUrls);
        roomRepository.save(room);
        return room;
    }

    @Override
    public Optional<Room> editRoom(Long id, RoomDTO input) {
        return roomRepository.findById(id).map(room -> {
            room.setName(input.getName());
            room.setNightPrice(input.getNightPrice());
            room.setNumbersRoom(input.getNumbersRoom());
            room.setTypeRoom(input.getTypeRoom());
            room.setImage(input.getImage());
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
        return allRooms.stream().filter(room -> {
            int reservedCount = reservationRepository.countReservationsByRoomAndDateRange(room, checkInDate, checkOutDate);
            return reservedCount < room.getNumbersRoom();
        }).collect(Collectors.toList());
    }
}
