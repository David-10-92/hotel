package com.hotel.reservation.controller;

import com.hotel.reservation.dtos.ReservationDTO;
import com.hotel.reservation.dtos.ReservationParamsDTO;
import com.hotel.reservation.model.Reservation;
import com.hotel.reservation.service.ReservationService;
import com.hotel.room.model.Room;
import com.hotel.room.service.RoomService;
import com.hotel.user.model.User;
import com.hotel.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/reservations")
public class ReservationController {

    @Autowired
    private ReservationService reservationService;
    @Autowired
    private UserService userService;
    @Autowired
    private RoomService roomService;

    @GetMapping("/createReservation")
    public String formCreateReservation(@ModelAttribute("reservationParamsDTO") ReservationParamsDTO paramsDTO,
                                        Model model,
                                        RedirectAttributes redirectAttributes) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        Optional<User> currentUser = userService.findByUsername(currentUsername);

        if (currentUser.isEmpty()) {
            // Manejar caso donde el usuario no está autenticado correctamente
            return "redirect:/users/login"; // Redirige al login si el usuario no está autenticado
        }

        LocalDate today = LocalDate.now();
        if (paramsDTO.getCheckInDate() == null || paramsDTO.getCheckOutDate() == null ||
                paramsDTO.getCheckInDate().isBefore(today) || paramsDTO.getCheckOutDate().isBefore(today)) {
            redirectAttributes.addFlashAttribute("mensajeError2", "Las fechas no son válidas. Debe seleccionar fechas a partir de hoy.");
            return "redirect:/rooms/listRooms";
        }

        Optional<Room> optionalRoom = roomService.getRoomById(paramsDTO.getRoomId());
        if (optionalRoom.isEmpty()) {
            redirectAttributes.addFlashAttribute("mensajeError2", "La habitación seleccionada no existe.");
            return "redirect:/rooms/listRooms";
        }

        Room room = optionalRoom.get();
        int availableRooms = roomService.countAvailableRooms(room, paramsDTO.getCheckInDate(), paramsDTO.getCheckOutDate());
        if (paramsDTO.getNumbersRoom() > availableRooms) {
            redirectAttributes.addFlashAttribute("mensajeError2", "Solo quedan " + availableRooms + " habitaciones disponibles para las fechas seleccionadas.");
            return "redirect:/rooms/listRooms";
        }

        Double totalPrice = reservationService.totalPrice(paramsDTO.getCheckInDate(), paramsDTO.getCheckOutDate(), paramsDTO.getNumbersRoom(), paramsDTO.getNightPrice());

        try {
            ReservationDTO reservationDTO = reservationService.prepareReservation(paramsDTO, currentUser.get(), totalPrice);
            model.addAttribute("reservationDTO", reservationDTO);
            return "createReservation";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("mensajeError", e.getMessage());
            return "redirect:/rooms/listRooms";
        }
    }

    @PostMapping("/createReservation")
    public String createReservation(@Valid @ModelAttribute("reservationDTO")ReservationDTO reservationDTO,
                                    BindingResult bindingResult,
                                    Model model) {
        if (bindingResult.hasErrors()) {
            return "createReservation";
        }
        reservationService.createReservation(reservationDTO);
        model.addAttribute("mesage","Reserva creada exitosamente");
        return "redirect:/rooms/listRooms";
    }

    @GetMapping("/listReservations")
    public String searchReservations(Model model,
                                     @RequestParam(name = "page", defaultValue = "0") int page,
                                     @RequestParam(name = "size", defaultValue = "5") int size) {
        Page<Reservation> reservationPage = reservationService.getAllReservation(page, size);

        model.addAttribute("reservations", reservationPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", reservationPage.getTotalPages());
        model.addAttribute("totalItems", reservationPage.getTotalElements());

        return "listReservations";
    }

    @GetMapping("/listUserReservation")
    public String searchReservation(Model model,
                                    @RequestParam(name = "page", defaultValue = "0") int page,
                                    @RequestParam(name = "size", defaultValue = "5") int size,
                                    Principal principal) {
        String username = principal.getName();
        Page<Reservation> reservationPage = reservationService.getReservationsByUser(username, page, size);

        model.addAttribute("reservations", reservationPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", reservationPage.getTotalPages());
        model.addAttribute("totalItems", reservationPage.getTotalElements());

        return "listUserReservation";
    }

    @PostMapping("/deleteReservation/{id}")
    public String deleteReservation(@PathVariable Long id,RedirectAttributes redirectAttributes){
        try {
            reservationService.deleteReservation(id);
            redirectAttributes.addFlashAttribute("successMessage", "Reservation deleted successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting reservation: " + e.getMessage());
        }
        return "redirect:/users/home";
    }
}
