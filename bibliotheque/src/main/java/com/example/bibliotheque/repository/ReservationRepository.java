package com.example.bibliotheque.repository;

import com.example.bibliotheque.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByUtilisateurId(Long utilisateurId);
    Reservation findByExemplaireIdAndStatut(Long exemplaireId, Reservation.Statut statut);
}