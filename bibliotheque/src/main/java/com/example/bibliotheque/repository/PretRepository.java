package com.example.bibliotheque.repository;

import com.example.bibliotheque.model.Pret;
import com.example.bibliotheque.model.Utilisateur;
import com.example.bibliotheque.model.TypeUtilisateur;
import java.time.LocalDate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PretRepository extends JpaRepository<Pret, Long> {
    long countByStatut(String statut);

    long countByStatutAndDateRetourPrevueBefore(String statut, LocalDate date);

    long countByUtilisateurAndStatut(Utilisateur utilisateur, String statut);

    long countByUtilisateur_TypeAndStatut(TypeUtilisateur type, String statut);

    java.util.List<Pret> findByUtilisateurId(Long utilisateurId);
}