package com.hotel.repository;

import com.hotel.dtos.UserDTO;
import com.hotel.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

}
