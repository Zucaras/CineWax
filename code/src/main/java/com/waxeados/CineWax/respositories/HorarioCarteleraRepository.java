package com.waxeados.CineWax.respositories;

import com.waxeados.CineWax.entity.HorarioCartelera;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    // All schedules for a given room on a given date (for overlap validation)
    @Query("SELECT h FROM HorarioCartelera h WHERE h.sala.idSala = :idSala AND h.fechaProyeccion = :fecha")
    List<HorarioCartelera> findBySalaAndFecha(@Param("idSala") Integer idSala,
                                               @Param("fecha") LocalDate fecha);
 
    // All schedules in a municipality (through sala -> municipio)
    @Query("SELECT h FROM HorarioCartelera h WHERE h.sala.municipio.idMunicipio = :idMunicipio")
    List<HorarioCartelera> findByMunicipio(@Param("idMunicipio") String idMunicipio);
 
    // Billboard for a specific municipality ordered ascending
    @Query("SELECT h FROM HorarioCartelera h WHERE h.sala.municipio.idMunicipio = :idMunicipio " +
           "ORDER BY h.fechaProyeccion ASC, h.horaInicio ASC")
    List<HorarioCartelera> findCarteleraAsc(@Param("idMunicipio") String idMunicipio);
 
    // Billboard for a specific municipality ordered descending
    @Query("SELECT h FROM HorarioCartelera h WHERE h.sala.municipio.idMunicipio = :idMunicipio " +
           "ORDER BY h.fechaProyeccion DESC, h.horaInicio DESC")
    List<HorarioCartelera> findCarteleraDesc(@Param("idMunicipio") String idMunicipio);
 
    void deleteByPelicula_IdPelicula(Integer idPelicula);
}