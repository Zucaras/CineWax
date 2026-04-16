package com.waxeados.CineWax.respositories;

import com.waxeados.CineWax.entity.Municipio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MunicipioRepository extends JpaRepository<Municipio, String> {

    List<Municipio> findByEstado_IdEstado(String idEstado);
}