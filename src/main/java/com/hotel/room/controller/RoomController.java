package com.hotel.room.controller;

import com.hotel.reservation.service.ReservationService;
import com.hotel.room.dtos.DateRangeDTO;
import com.hotel.room.dtos.RoomAvailabilityDTO;
import com.hotel.room.dtos.RoomDTO;
import com.hotel.room.model.Room;
import com.hotel.room.service.RoomService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Controller
@RequestMapping("/rooms")
public class RoomController {

    @Autowired
    RoomService roomService;
    @Autowired
    ReservationService reservationService;

    @GetMapping("/createRoom")
    public String newRoomForm(Model model){
        model.addAttribute("roomDTO",new RoomDTO());
        return "createRoom";
    }
    @PostMapping("/createRoom")
    public String createRoom(@Valid @ModelAttribute("roomDTO")RoomDTO roomDTO,
                             Model model, BindingResult bindingResult){
        if (bindingResult.hasErrors()) {
            return "createRoom";
        }
        roomService.createRoom(roomDTO);
        return "redirect:/users/home";
    }

    @GetMapping("/{id}")
    public String getRoomDetails(@PathVariable Long id,Model model) {
        Optional<Room> roomOptional = roomService.getRoomById(id);
        if (roomOptional.isPresent()) {
            Room room = roomOptional.get();
            model.addAttribute("room", room);

            List<String> landscapeImages = roomService.getImagesByType(room.getId(), room.getTypeRoom());
            model.addAttribute("landscapeImages", landscapeImages);

            return "roomDetails";
        }
        return "roomDetails";
    }

    @GetMapping("/listRooms")
    public String searchRooms(@ModelAttribute DateRangeDTO dateRange,
                              Model model) {

        if (dateRange.getCheckInDate() == null) {
            dateRange.setCheckInDate(LocalDate.now());
        }
        if (dateRange.getCheckOutDate() == null) {
            dateRange.setCheckOutDate(LocalDate.now().plusDays(1));
        }

        LocalDate checkInDate = dateRange.getCheckInDate();
        LocalDate checkOutDate = dateRange.getCheckOutDate();

        List<Room> availableRooms = roomService.findAvailableRooms(checkInDate, checkOutDate);

        Map<Long, Integer> availableRoomCounts = new HashMap<>();
        for (Room room : availableRooms) {
            int availableRoomCount = roomService.countAvailableRooms(room, checkInDate, checkOutDate);
            availableRoomCounts.put(room.getId(), availableRoomCount);
        }

        model.addAttribute("rooms", availableRooms);
        model.addAttribute("availableRoomCounts", availableRoomCounts);
        model.addAttribute("dateRange", dateRange);
        return "listRooms";
    }

    @GetMapping("/editRoom/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Optional<Room> room = roomService.getRoomById(id);
        if (room.isPresent()) {
            model.addAttribute("room", room.get());
            return "editRoom";
        } else {
            return "redirect:/rooms/listRooms";
        }
    }

    @PostMapping("/editRoom/{id}")
    public String updateRoom(@PathVariable Long id, @ModelAttribute RoomDTO roomDTO) {
        roomService.editRoom(id, roomDTO);
        return "redirect:/rooms/listRooms";
    }

    @PostMapping("/deleteRoom/{id}")
    public String deleteRoom(@PathVariable Long id) {
        roomService.deleteRoom(id);
        return "redirect:/rooms/listRooms";
    }

}
