package com.waxeados.CineWax.respositories;

import com.waxeados.CineWax.entity.Municipio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

// Repository interface for Municipio entity
@Repository
public interface MunicipioRepository extends JpaRepository<Municipio, String> {
    
    // Retrieves all municipalities that belong to a specific state ID
    List<Municipio> findByEstado_IdEstado(String idEstado);
}