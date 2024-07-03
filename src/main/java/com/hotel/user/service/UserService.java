package com.hotel.user.service;

import com.hotel.user.dtos.UserDTO;
import com.hotel.user.model.User;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface UserService {
    User createUser(UserDTO input);
    Optional<User> editUser(Long id, UserDTO input);
    boolean deleteUser(Long id);
    boolean isEmailAlreadyInUse(String email);
    Optional<User> findUserById(Long id);
    UserDTO getCurrentUserDTO();
    Optional<User> findByUsername(String username);
    Page<User> getAllUsers(int page, int size);
}
