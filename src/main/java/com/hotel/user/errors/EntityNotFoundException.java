package com.hotel.user.errors;

public class EntityNotFoundException extends  RuntimeException{
    public EntityNotFoundException(String message) {
        super(message);
    }
}
