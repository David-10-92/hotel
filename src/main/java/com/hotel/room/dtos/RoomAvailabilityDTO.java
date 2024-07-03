package com.hotel.room.dtos;

import com.hotel.room.model.Room;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
public class RoomAvailabilityDTO {
    private Room room;
    private int availableRoomsCount;

    public RoomAvailabilityDTO(Room room, int availableRoomsCount) {
        this.room = room;
        this.availableRoomsCount = availableRoomsCount;
    }
}
