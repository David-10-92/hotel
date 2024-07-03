package com.hotel.user.controller;


import com.hotel.erros.EmailAlreadyInUseException;
import com.hotel.reservation.model.Reservation;
import com.hotel.room.service.RoomService;
import com.hotel.user.dtos.UserDTO;
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


@Controller
@RequestMapping("/users")
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    RoomService roomService;

    @GetMapping("/registerUser")
    public String newUserForm(Model model){
        model.addAttribute("userDTO",new UserDTO());
        return "registerUser";
    }

    @PostMapping("/registerUser")
    public String createUser(@Valid @ModelAttribute("userDTO") UserDTO userDTO,
                             BindingResult bindingResult,Model model){

        if (bindingResult.hasErrors()) {
            return "registerUser";
        }

        try {
            userService.createUser(userDTO);
        } catch (EmailAlreadyInUseException e) {
            model.addAttribute("emailError", e.getMessage());
            return "registerUser";
        }

        return "login";
    }

    @GetMapping("/login")
    public String loginPage(Model model,@RequestParam(value = "error", required = false) String error) {
        return "login";
    }

    @GetMapping("/home")
    public String home(Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        model.addAttribute("username", username);
        model.addAttribute("rooms", roomService.getAllRooms());
        return "home";
    }

    @GetMapping("/admin")
    public String admin() {
        return "admin";
    }

    @GetMapping("/editUser")
    public String showEditProfileForm(Model model) {
        UserDTO userDTO = userService.getCurrentUserDTO();
        model.addAttribute("userDTO", userDTO);
        return "editUser";
    }

    @PostMapping("/editUser/{id}")
    public String updateUser(@PathVariable Long id, @Valid @ModelAttribute("userDTO") UserDTO userDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "editUser";
        }
        userService.editUser(id, userDTO);
        return "redirect:/users/home";
    }

    @GetMapping("/deleteUser")
    public String deleteUser() {
        return "deleteUser";
    }

    @PostMapping("/deleteUser")
    public String deleteUser(@RequestParam Long id) {
        userService.deleteUser(id);
        return "redirect:/users/home";
    }

    @GetMapping("/listUsers")
    public String searchReservations(Model model,
                                     @RequestParam(name = "page", defaultValue = "0") int page,
                                     @RequestParam(name = "size", defaultValue = "5") int size){
        Page<User> usersPage = userService.getAllUsers(page,size);
        model.addAttribute("users", usersPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", usersPage.getTotalPages());
        model.addAttribute("totalItems", usersPage.getTotalElements());
        return "listUsers";
    }
}
