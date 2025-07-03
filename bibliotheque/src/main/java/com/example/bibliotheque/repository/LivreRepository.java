package com.example.bibliotheque.repository;

import com.example.bibliotheque.model.Livre;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface LivreRepository extends JpaRepository<Livre, Long> {
    Optional<Livre> findById(Long id);
}