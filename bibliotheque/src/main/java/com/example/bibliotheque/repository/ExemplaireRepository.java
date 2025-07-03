package com.example.bibliotheque.repository;

import com.example.bibliotheque.model.Exemplaire;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ExemplaireRepository extends JpaRepository<Exemplaire, Long> {
    List<Exemplaire> findByLivreIdAndStatut(Long livreId, String statut);
}