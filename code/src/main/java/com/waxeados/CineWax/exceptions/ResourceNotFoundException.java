package com.waxeados.CineWax.exceptions;

/**
 * Exception thrown when a requested entity (Movie, Schedule, etc.) 
 * is not found in the database.
 */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}