package com.rocketseat.trip_planner.exception;

public class InvalidDateException extends RuntimeException{
    public InvalidDateException(String message) {
        super(message);
    }
}
