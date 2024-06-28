package com.hotel.room.repository;

import com.hotel.room.model.Image;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ImageRepository extends JpaRepository<Image,Long> {
    List<Image> findByTypeImage(String type);
    Optional<Image> findByImageUrl(String input);
}
