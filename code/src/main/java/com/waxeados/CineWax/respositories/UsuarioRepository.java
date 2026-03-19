package com.waxeados.CineWax.respositories;

import com.waxeados.CineWax.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

// Repository interface for Usuario entity
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    
    // Finds a user by their username (essential for authentication)
    Optional<Usuario> findByUsername(String username);
    
    // Checks if a username is already taken
    boolean existsByUsername(String username);
}