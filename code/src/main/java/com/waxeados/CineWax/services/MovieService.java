package com.waxeados.CineWax.services;

import com.waxeados.CineWax.entity.Pelicula;
import com.waxeados.CineWax.respositories.PeliculaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

// Direct service class containing the business logic for Movies
@Service
@RequiredArgsConstructor
public class MovieService {

    // Lombok's @RequiredArgsConstructor automatically injects this dependency
    private final PeliculaRepository movieRepository;

    // Retrieves all movies from the database
    public List<Pelicula> getAllMovies() {
        return movieRepository.findAll();
    }

    // Example method to save a movie with some basic validation
    public Pelicula saveMovie(Pelicula pelicula) {
        if (movieRepository.existsByNameIgnoreCase(pelicula.getNombre())) {
            throw new IllegalArgumentException("A movie with this name already exists.");
        }
        return movieRepository.save(pelicula);
    }
}