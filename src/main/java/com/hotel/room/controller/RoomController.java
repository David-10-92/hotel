package com.hotel.room.controller;

import com.hotel.reservation.service.ReservationService;
import com.hotel.room.dtos.RoomDTO;
import com.hotel.room.model.Room;
import com.hotel.room.service.RoomService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;

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
    public String getRoomDetails(
            @PathVariable Long id,
            @RequestParam(value = "checkInDate", required = false) String checkInDate,
            @RequestParam(value = "checkOutDate", required = false) String checkOutDate,
            Model model) {

        Optional<Room> room = roomService.getRoomById(id);

        // Convertir las fechas de texto a LocalDate
        LocalDate parsedCheckInDate = LocalDate.now(); // Valor por defecto hoy
        LocalDate parsedCheckOutDate = LocalDate.now().plusDays(1); // Valor por defecto: mañana

        if (checkInDate != null && !checkInDate.isEmpty()) {
            try {
                parsedCheckInDate = LocalDate.parse(checkInDate);
            } catch (DateTimeParseException e) {
                // Manejo de error: asignar valor predeterminado u otro manejo adecuado
                parsedCheckInDate = LocalDate.now(); // Valor por defecto hoy
            }
        }

        if (checkOutDate != null && !checkOutDate.isEmpty()) {
            try {
                parsedCheckOutDate = LocalDate.parse(checkOutDate);
            } catch (DateTimeParseException e) {
                // Manejo de error: asignar valor predeterminado u otro manejo adecuado
                parsedCheckOutDate = LocalDate.now().plusDays(1); // Valor por defecto: mañana
            }
        }

        boolean isAvailable = reservationService.isRoomAvailable(id, parsedCheckInDate, parsedCheckOutDate, 1);

        model.addAttribute("room", room.get());
        model.addAttribute("checkInDate", parsedCheckInDate);
        model.addAttribute("checkOutDate", parsedCheckOutDate);
        model.addAttribute("isAvailable", isAvailable);

        return "roomDetails";
    }

    @GetMapping("/listRooms")
    public String searchRooms(@RequestParam(value = "checkInDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkInDate,
                              @RequestParam(value = "checkOutDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOutDate,
                              Model model) {
        List<Room> availableRooms = roomService.findAvailableRooms(checkInDate, checkOutDate);
        model.addAttribute("rooms", availableRooms);
        model.addAttribute("checkInDate", checkInDate);
        model.addAttribute("checkOutDate", checkOutDate);
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
