package com.waxeados.CineWax.respositories;

import com.waxeados.CineWax.entity.Pelicula;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PeliculaRepository extends JpaRepository<Pelicula, Integer> {

    boolean existsByNombreIgnoreCase(String nombre);
    List<Pelicula> findByNombreContainingIgnoreCase(String nombre);
    List<Pelicula> findByClasificacionIgnoreCase(String clasificacion);
    List<Pelicula> findByGenero_IdGenero(Integer idGenero);
}