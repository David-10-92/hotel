package com.hotel.reservation.controller;

import com.hotel.reservation.dtos.ReservationDTO;
import com.hotel.reservation.dtos.ReservationParamsDTO;
import com.hotel.reservation.model.Reservation;
import com.hotel.reservation.service.ReservationService;
import com.hotel.room.model.Room;
import com.hotel.room.service.RoomService;
import com.hotel.user.errors.InvalidUserException;
import com.hotel.user.model.User;
import com.hotel.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

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
        try{
            User currentUser = reservationService.getCurrentUser();
            model.addAttribute("idUser", currentUser.getId());

            Room room = reservationService.validateRoom(paramsDTO.getRoomId());

            roomService.checkRoomAvailability(room, paramsDTO.getCheckInDate(), paramsDTO.getCheckOutDate(), paramsDTO.getNumbersRoom());

            Double totalPrice = reservationService.totalPrice(paramsDTO.getCheckInDate(), paramsDTO.getCheckOutDate(), paramsDTO.getNumbersRoom(), paramsDTO.getNightPrice());

            ReservationDTO reservationDTO = reservationService.prepareReservation(paramsDTO, currentUser, totalPrice);
            model.addAttribute("reservationDTO", reservationDTO);
            return "createReservation";

        }catch (InvalidUserException e) {
            return "redirect:/users/login";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/rooms";
        }
    }

    @PostMapping("/createReservation")
    public String createReservation(@Valid @ModelAttribute("reservationDTO")ReservationDTO reservationDTO,
                                    BindingResult bindingResult,
                                    RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "createReservation";
        }
        reservationService.createReservation(reservationDTO);
        redirectAttributes.addFlashAttribute("successMessage","Reserva creada exitosamente");
        return "redirect:/rooms";
    }

    @GetMapping()
    public String searchReservations(Model model,
                                     @RequestParam(name = "page", defaultValue = "0") int page,
                                     @RequestParam(name = "size", defaultValue = "5") int size) {
        Page<Reservation> reservationPage = reservationService.getAllReservation(page, size);

        model.addAttribute("reservations", reservationPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", reservationPage.getTotalPages());
        model.addAttribute("totalItems", reservationPage.getTotalElements());
        model.addAttribute("size", size);

        return "reservations";
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
        model.addAttribute("size", size);

        return "listUserReservation";
    }

    @PostMapping("/deleteReservation/{id}")
    public String deleteReservation(@PathVariable Long id,RedirectAttributes redirectAttributes){
        try {
            reservationService.deleteReservation(id);
            redirectAttributes.addFlashAttribute("successMessage", "Se ha eliminado la reserva correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/home";
    }
}
