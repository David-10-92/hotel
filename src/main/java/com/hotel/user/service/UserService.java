package com.hotel.user.service;

import com.hotel.user.dtos.UserDTO;
import com.hotel.user.model.User;

import java.util.Optional;

public interface UserService {
    User createUser(UserDTO input);
    Optional<User> editUser(Long id, UserDTO input);
    boolean deleteUser(Long id);
    boolean isEmailAlreadyInUse(String email);
    Optional<User> findUserById(Long id);
    UserDTO getCurrentUserDTO();
}
