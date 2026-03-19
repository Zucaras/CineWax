package com.waxeados.CineWax.respositories;

import com.waxeados.CineWax.entity.Sala;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

// Repository interface for Sala entity
@Repository
public interface SalaRepository extends JpaRepository<Sala, Integer> {
    
    // Finds all theater rooms available in a specific municipality
    List<Sala> findByMunicipio_IdMunicipio(String idMunicipio);
}