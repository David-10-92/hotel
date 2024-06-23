package com.hotel.room.service.impl;

import com.hotel.room.dtos.RoomDTO;
import com.hotel.room.model.Room;
import com.hotel.room.repository.RoomRepository;
import com.hotel.room.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RoomServiceImpl implements RoomService {

    @Autowired
    RoomRepository roomRepository;

    @Override
    public Room createRoom(RoomDTO input) {
        Room room = new Room();
        room.setName(input.getName());
        room.setNightPrice(input.getNightPrice());
        room.setNumberRoom(input.getNumbersRoom());
        room.setTypeRoom(input.getTypeRoom());
        room.setImages(input.getImages());
        roomRepository.save(room);
        return room;
    }

    @Override
    public Optional<Room> editRoom(Long id, RoomDTO input) {
        return roomRepository.findById(id).map(room -> {
            room.setName(input.getName());
            room.setNightPrice(input.getNightPrice());
            room.setNumberRoom(input.getNumbersRoom());
            room.setTypeRoom(input.getTypeRoom());
            room.setImages(input.getImages());
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
}
