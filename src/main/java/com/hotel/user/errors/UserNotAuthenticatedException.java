package com.hotel.user.errors;

public class UserNotAuthenticatedException extends RuntimeException{
    public UserNotAuthenticatedException(String message) {
        super(message);
    }
}
