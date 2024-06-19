package com.hotel.service;

import com.hotel.dtos.UserDTO;
import com.hotel.model.User;

import java.util.Optional;

public interface UserService {
    User createUser(UserDTO input);
    Optional<User> editUser(Long id, UserDTO input);
    boolean deleteUser(Long id);
}
