package com.hotel.home.service.impl;

import com.hotel.home.service.HomeService;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
public class HomeServiceImpl implements HomeService {
    @Override
    public List<String> getImageNames() {
        List<String> imageNames = new ArrayList<>();
        try {
            Path uploadPath = Paths.get("src/main/resources/static/uploads");
            Files.walk(uploadPath, 1).filter(Files::isRegularFile).forEach(file -> {
                imageNames.add("/uploads/" + file.getFileName().toString());
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return imageNames;
    }
}
