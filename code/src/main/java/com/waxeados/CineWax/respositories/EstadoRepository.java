package com.waxeados.CineWax.respositories;

import com.waxeados.CineWax.entity.Estado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// Repository interface for Estado entity to handle database operations
@Repository
public interface EstadoRepository extends JpaRepository<Estado, String> {
}