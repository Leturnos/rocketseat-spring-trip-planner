package com.rocketseat.trip_planner.exception;

import java.util.Date;

public record ExceptionResponse(Date timestamp, String message, String details) {
}
