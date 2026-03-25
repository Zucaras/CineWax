package com.waxeados.CineWax.exceptions;

/**
 * Exception thrown when a new schedule overlaps with an existing one
 * in the same theater room and date.
 */
public class ScheduleOverlapException extends RuntimeException {
    public ScheduleOverlapException(String message) {
        super(message);
    }
}