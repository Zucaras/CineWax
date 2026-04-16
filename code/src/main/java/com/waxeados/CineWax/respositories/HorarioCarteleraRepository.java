package com.waxeados.CineWax.respositories;

import com.waxeados.CineWax.entity.HorarioCartelera;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface HorarioCarteleraRepository extends JpaRepository<HorarioCartelera, Integer> {

    List<HorarioCartelera> findByPelicula_IdPelicula(Integer idPelicula);
    List<HorarioCartelera> findByFechaProyeccionOrderByHoraInicioAsc(LocalDate fechaProyeccion);

    @Query("SELECT h FROM HorarioCartelera h WHERE h.sala.idSala = :idSala AND h.fechaProyeccion = :fecha")
    List<HorarioCartelera> findBySalaAndFecha(@Param("idSala") Integer idSala,
                                               @Param("fecha") LocalDate fecha);
    @Query("SELECT h FROM HorarioCartelera h WHERE h.sala.municipio.idMunicipio = :idMunicipio")
    List<HorarioCartelera> findByMunicipio(@Param("idMunicipio") String idMunicipio);

    @Query("SELECT h FROM HorarioCartelera h WHERE h.sala.municipio.idMunicipio = :idMunicipio " +
           "ORDER BY h.fechaProyeccion ASC, h.horaInicio ASC")
    List<HorarioCartelera> findCarteleraAsc(@Param("idMunicipio") String idMunicipio);

    @Query("SELECT h FROM HorarioCartelera h WHERE h.sala.municipio.idMunicipio = :idMunicipio " +
           "ORDER BY h.fechaProyeccion DESC, h.horaInicio DESC")
    List<HorarioCartelera> findCarteleraDesc(@Param("idMunicipio") String idMunicipio);
 
    void deleteByPelicula_IdPelicula(Integer idPelicula);
}