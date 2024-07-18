package com.hotel.room.repository;

import com.hotel.room.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Long> {
    @Query("SELECT r FROM Room r LEFT JOIN Reservation res ON r.id = res.room.id AND " +
            "(res.checkInDate < :checkOutDate AND res.checkOutDate > :checkInDate) " +
            "GROUP BY r.id HAVING COUNT(res.id) < r.numbersRoom")
    List<Room> findAvailableRooms(@Param("checkInDate") LocalDate checkInDate,
                                  @Param("checkOutDate") LocalDate checkOutDate);
}
