package com.waxeados.CineWax.respositories;

import com.waxeados.CineWax.entity.Genero;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// Repository interface for Genero entity
@Repository
public interface GeneroRepository extends JpaRepository<Genero, Integer> {
}