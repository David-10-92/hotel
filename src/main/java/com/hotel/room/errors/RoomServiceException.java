package com.hotel.room.errors;

public class RoomServiceException extends RuntimeException{
    public RoomServiceException(String message) {
        super(message);
    }

    public RoomServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
