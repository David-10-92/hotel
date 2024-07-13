package com.hotel.room.controller;

import com.hotel.room.errors.RoomNotFoundException;
import com.hotel.room.errors.RoomServiceException;
import com.hotel.reservation.service.ReservationService;
import com.hotel.room.dtos.DateRangeDTO;
import com.hotel.room.dtos.RoomDTO;
import com.hotel.room.model.Room;
import com.hotel.room.service.RoomService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/rooms")
public class RoomController {

    @Autowired
    RoomService roomService;
    @Autowired
    ReservationService reservationService;
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/createRoom")
    public String newRoomForm(Model model){
        model.addAttribute("roomDTO",new RoomDTO());
        return "createRoom";
    }
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/createRoom")
    public String createRoom(@Valid @ModelAttribute("roomDTO")RoomDTO roomDTO,BindingResult bindingResult,
                             @RequestParam("image") MultipartFile mainImage,RedirectAttributes redirectAttributes,Model model){
        if (bindingResult.hasErrors()) {
            return "createRoom";
        }
        try {
            roomService.createRoom(roomDTO, mainImage);
            redirectAttributes.addFlashAttribute("successMessage", "La habitación se creó exitosamente");
            return "redirect:/home";
        } catch (IllegalArgumentException | RoomServiceException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "createRoom";
        }
    }

    @GetMapping("/{id}")
    public String getRoomDetails(@PathVariable Long id,Model model,RedirectAttributes redirectAttributes) {
        try {
            Optional<Room> room = roomService.getRoomById(id);

            model.addAttribute("room", room.get());

            List<String> landscapeImages = roomService.getImagesByType(room.get().getId(), room.get().getTypeRoom());
            model.addAttribute("landscapeImages", landscapeImages);

            return "roomDetails";

        } catch (RoomNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/home";
        }
    }

    @GetMapping()
    public String searchRooms(@ModelAttribute DateRangeDTO dateRange, RedirectAttributes redirectAttributes,
                              Model model) {

        if (dateRange.getCheckInDate() == null) {
            dateRange.setCheckInDate(LocalDate.now());
        }
        if (dateRange.getCheckOutDate() == null) {
            dateRange.setCheckOutDate(LocalDate.now().plusDays(1));
        }

        LocalDate checkInDate = dateRange.getCheckInDate();
        LocalDate checkOutDate = dateRange.getCheckOutDate();

        if (checkOutDate.isBefore(checkInDate) || checkOutDate.isEqual(checkInDate)) {
            redirectAttributes.addFlashAttribute("message", "La fecha de check-out debe ser posterior a la fecha de check-in.");
            return "redirect:/rooms";
        }

        List<Room> availableRooms = roomService.findAvailableRooms(checkInDate, checkOutDate);

        if (availableRooms.isEmpty()) {
            model.addAttribute("messageRooms", "No hay habitaciones disponibles para estas fechas");
        }

        Map<Long, Integer> availableRoomCounts = new HashMap<>();
        for (Room room : availableRooms) {
            int availableRoomCount = roomService.countAvailableRooms(room, checkInDate, checkOutDate);
            availableRoomCounts.put(room.getId(), availableRoomCount);
        }

        model.addAttribute("rooms", availableRooms);
        model.addAttribute("availableRoomCounts", availableRoomCounts);
        model.addAttribute("dateRange", dateRange);
        model.addAttribute("today", LocalDate.now());
        model.addAttribute("tomorrow", LocalDate.now().plusDays(1));
        return "rooms";
    }
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/editRoom/{id}")
    public String showEditForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Room room = roomService.getRoomById(id).orElseThrow(() -> new RoomNotFoundException("No se encontró la habitación con ID: " + id));
            model.addAttribute("room", room);
            return "editRoom";
        } catch (RoomNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/home";
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/editRoom/{id}")
    public String updateRoom(@PathVariable Long id,
                             @Valid @ModelAttribute("room") RoomDTO roomDTO,
                             BindingResult bindingResult,
                             @RequestParam("image") MultipartFile file,
                             RedirectAttributes redirectAttributes,
                             Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("room", roomDTO);
            return "editRoom";
        }
        try {
            roomService.editRoom(id, roomDTO, file);
            redirectAttributes.addFlashAttribute("successMessage", "La habitación se editó correctamente.");
            return "redirect:/home";
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("room", roomDTO);
            return "editRoom";
        }
    }
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/deleteRoom/{id}")
    public String deleteRoom(@PathVariable Long id,RedirectAttributes redirectAttributes) {
        try {
            roomService.deleteRoom(id);
            redirectAttributes.addFlashAttribute("successMessage", "Habitación eliminada exitosamente.");
        } catch (RoomNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/home";
    }
}
