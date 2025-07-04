package com.example.bibliotheque.repository;

import com.example.bibliotheque.model.Utilisateur;
import com.example.bibliotheque.model.TypeUtilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {
    Optional<Utilisateur> findByNom(String nom);

    long countByType(TypeUtilisateur type);
}