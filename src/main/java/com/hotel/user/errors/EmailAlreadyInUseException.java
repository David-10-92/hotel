package com.hotel.user.errors;

public class EmailAlreadyInUseException extends RuntimeException{
    public EmailAlreadyInUseException(String message) {
        super(message);
    }
}
