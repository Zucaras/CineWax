package com.waxeados.CineWax.exceptions;

public class ScheduleOverlapException extends RuntimeException {
    public ScheduleOverlapException(String message) {
        super(message);
    }
}