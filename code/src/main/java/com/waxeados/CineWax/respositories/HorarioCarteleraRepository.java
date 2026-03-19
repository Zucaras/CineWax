package com.waxeados.CineWax.respositories;

import com.waxeados.CineWax.entity.HorarioCartelera;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

// Repository interface for HorarioCartelera entity
@Repository
public interface HorarioCarteleraRepository extends JpaRepository<HorarioCartelera, Integer> {
    
    // Fetches all schedules for a specific movie
    List<HorarioCartelera> findByPelicula_IdPelicula(Integer idPelicula);
    
    // Fetches schedules for a specific date (useful for ordering the billboard)
    List<HorarioCartelera> findByFechaProyeccionOrderByHoraInicioAsc(LocalDate fechaProyeccion);
}