package com.example.bibliotheque.controller;

import com.example.bibliotheque.model.Exemplaire;
import com.example.bibliotheque.model.Reservation;
import com.example.bibliotheque.model.Utilisateur;
import com.example.bibliotheque.repository.ExemplaireRepository;
import com.example.bibliotheque.repository.LivreRepository;
import com.example.bibliotheque.repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.time.LocalDate;
import java.util.List;

@Controller
public class ReservationController {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private LivreRepository livreRepository;

    @Autowired
    private ExemplaireRepository exemplaireRepository;

    @GetMapping("/reservations/nouveau")
    public String afficherFormulaireReservation(@RequestParam("livreId") Long livreId, Model model, Authentication authentication) {
        if (!livreRepository.existsById(livreId)) {
            model.addAttribute("error", "Livre non trouvé...");
            return "redirect:/livres";
        }
        Utilisateur utilisateur = (Utilisateur) authentication.getPrincipal();
        if (!utilisateur.getType().equals("Adherent")) {
            model.addAttribute("error", "Seuls les adhérents peuvent réserver.");
            return "redirect:/livres";
        }
        List<Exemplaire> exemplairesDisponibles = exemplaireRepository.findByLivreIdAndStatut(livreId, "disponible");
        if (exemplairesDisponibles.isEmpty()) {
            model.addAttribute("error", "Aucun exemplaire disponible pour ce livre.");
            return "redirect:/livres";
        }
        model.addAttribute("livre", livreRepository.findById(livreId).get());
        model.addAttribute("exemplaires", exemplairesDisponibles);
        return "reservation";
    }

    @PostMapping("/reservations/nouveau")
    public String soumettreReservation(@RequestParam("livreId") Long livreId,
                                      @RequestParam("exemplaireId") Long exemplaireId,
                                      @RequestParam("dateReservation") String dateReservation,
                                      @RequestParam("dateExpiration") String dateExpiration,
                                      Authentication authentication,
                                      Model model) {
        if (!livreRepository.existsById(livreId) || !exemplaireRepository.existsById(exemplaireId)) {
            model.addAttribute("error", "Livre ou exemplaire non trouvé...");
            return "redirect:/livres";
        }
        Utilisateur utilisateur = (Utilisateur) authentication.getPrincipal();
        if (!utilisateur.getType().equals("Adherent")) {
            model.addAttribute("error", "Seuls les adhérents peuvent réserver.");
            return "redirect:/livres";
        }
        Exemplaire exemplaire = exemplaireRepository.findById(exemplaireId).get();
        if (!exemplaire.getStatut().equals("disponible")) {
            model.addAttribute("error", "Cet exemplaire n'est plus disponible.");
            return "redirect:/livres";
        }
        Reservation reservation = new Reservation();
        reservation.setUtilisateur(utilisateur);
        reservation.setExemplaire(exemplaire);
        reservation.setDateReservation(LocalDate.parse(dateReservation));
        reservation.setDateExpiration(LocalDate.parse(dateExpiration));
        reservation.setStatut(Reservation.Statut.en_attente);
        reservationRepository.save(reservation);
        return "redirect:/home";
    }

    @GetMapping("/reservations/gestion")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String afficherGestionReservations(Model model) {
        model.addAttribute("reservations", reservationRepository.findAll());
        return "gestion-reservations";
    }

    @PostMapping("/reservations/approuver")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String approuverReservation(@RequestParam("reservationId") Long reservationId, Model model) {
        if (!reservationRepository.existsById(reservationId)) {
            model.addAttribute("error", "Réservation non trouvée...");
            return "redirect:/reservations/gestion";
        }
        Reservation reservation = reservationRepository.findById(reservationId).get();
        if (reservation.getStatut() != Reservation.Statut.en_attente) {
            model.addAttribute("error", "Seules les réservations en attente peuvent être approuvées.");
            return "redirect:/reservations/gestion";
        }
        reservation.setStatut(Reservation.Statut.approuve);
        reservation.getExemplaire().setStatut("reserve");
        reservationRepository.save(reservation);
        exemplaireRepository.save(reservation.getExemplaire());
        return "redirect:/reservations/gestion";
    }

    @PostMapping("/reservations/rejeter")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String rejeterReservation(@RequestParam("reservationId") Long reservationId, Model model) {
        if (!reservationRepository.existsById(reservationId)) {
            model.addAttribute("error", "Réservation non trouvée...");
            return "redirect:/reservations/gestion";
        }
        Reservation reservation = reservationRepository.findById(reservationId).get();
        if (reservation.getStatut() != Reservation.Statut.en_attente) {
            model.addAttribute("error", "Seules les réservations en attente peuvent être rejetées.");
            return "redirect:/reservations/gestion";
        }
        reservation.setStatut(Reservation.Statut.rejete);
        reservationRepository.save(reservation);
        return "redirect:/reservations/gestion";
    }
}