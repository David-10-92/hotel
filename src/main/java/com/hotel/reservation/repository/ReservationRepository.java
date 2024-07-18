package com.hotel.reservation.repository;

import com.hotel.reservation.model.Reservation;
import com.hotel.room.model.Room;
import com.hotel.user.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    @Query("SELECT COUNT(r) FROM Reservation r WHERE r.room = :room AND " +
            "(r.checkInDate < :checkOutDate AND r.checkOutDate > :checkInDate)")
    int countReservationsByRoomAndDateRange(@Param("room") Room room,
                                            @Param("checkInDate") LocalDate checkInDate,
                                            @Param("checkOutDate") LocalDate checkOutDate);

    Page<Reservation> findByUser(User user, Pageable pageable);
}
