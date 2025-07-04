package com.example.bibliotheque.repository;

import com.example.bibliotheque.model.Prolongement;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProlongementRepository extends JpaRepository<Prolongement, Long> {
    List<Prolongement> findByPretId(Long pretId);
    List<Prolongement> findByPretIdAndStatut(Long pretId, String statut);
    List<Prolongement> findByStatut(String statut);
}