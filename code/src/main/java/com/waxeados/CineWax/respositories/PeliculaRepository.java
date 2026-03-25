package com.waxeados.CineWax.respositories;

import com.waxeados.CineWax.entity.Pelicula;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

// Repository interface for Movie entity to handle database operations
@Repository
public interface PeliculaRepository extends JpaRepository<Pelicula, Integer> {
    
    // Spring Data JPA will automatically implement this query based on the method name
    boolean existsByNameIgnoreCase(String name);

    List<Pelicula> findByNameContainingIgnoreCase(String nombre);
 
    List<Pelicula> findByClasificacionIgnoreCase(String clasificacion);
 
    List<Pelicula> findByGenero_IdGenero(Integer idGenero);
}