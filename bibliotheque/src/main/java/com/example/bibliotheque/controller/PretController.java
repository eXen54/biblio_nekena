package com.example.bibliotheque.controller;

import com.example.bibliotheque.model.Exemplaire;
import com.example.bibliotheque.model.Pret;
import com.example.bibliotheque.model.Reservation;
import com.example.bibliotheque.model.Utilisateur;
import com.example.bibliotheque.repository.ExemplaireRepository;
import com.example.bibliotheque.repository.LivreRepository;
import com.example.bibliotheque.repository.PretRepository;
import com.example.bibliotheque.repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Controller
public class PretController {

    @Autowired
    private PretRepository pretRepository;

    @Autowired
    private LivreRepository livreRepository;

    @Autowired
    private ExemplaireRepository exemplaireRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @GetMapping("/prets")
    public String afficherPrets(Model model) {
        model.addAttribute("prets", pretRepository.findAll());
        return "prets";
    }

    @GetMapping("/prets/nouveau")
    public String afficherFormulairePret(@RequestParam("livreId") Long livreId, Model model, Authentication authentication) {
        if (!livreRepository.existsById(livreId)) {
            model.addAttribute("error", "Livre non trouvé...");
            return "redirect:/livres";
        }
        Utilisateur utilisateur = (Utilisateur) authentication.getPrincipal();
        if (!utilisateur.getType().equals("Adherent")) {
            model.addAttribute("error", "Seuls les adhérents peuvent emprunter.");
            return "redirect:/livres";
        }
        List<Exemplaire> exemplairesDisponibles = exemplaireRepository.findByLivreIdAndStatut(livreId, "disponible");
        List<Exemplaire> exemplairesReserves = exemplaireRepository.findByLivreIdAndStatut(livreId, "reserve");
        List<Exemplaire> exemplairesEligibles = new ArrayList<>(exemplairesDisponibles);
        for (Exemplaire exemplaire : exemplairesReserves) {
            Reservation reservation = reservationRepository.findByExemplaireIdAndStatut(exemplaire.getId(), Reservation.Statut.approuve);
            if (reservation != null && reservation.getUtilisateur().getId().equals(utilisateur.getId())) {
                exemplairesEligibles.add(exemplaire);
            }
        }
        if (exemplairesEligibles.isEmpty()) {
            model.addAttribute("error", "Aucun exemplaire disponible ou réservé pour vous.");
            return "redirect:/livres";
        }
        model.addAttribute("livre", livreRepository.findById(livreId).get());
        model.addAttribute("exemplaires", exemplairesEligibles);
        return "nouveau-pret";
    }

    @PostMapping("/prets/nouveau")
    public String creerPret(@RequestParam("livreId") Long livreId,
                           @RequestParam("exemplaireId") Long exemplaireId,
                           @RequestParam("datePret") String datePret,
                           @RequestParam("dateRetourPrevue") String dateRetourPrevue,
                           Authentication authentication,
                           Model model) {
        if (!livreRepository.existsById(livreId) || !exemplaireRepository.existsById(exemplaireId)) {
            model.addAttribute("error", "Livre ou exemplaire non trouvé...");
            return "redirect:/livres";
        }
        Utilisateur utilisateur = (Utilisateur) authentication.getPrincipal();
        if (!utilisateur.getType().equals("Adherent")) {
            model.addAttribute("error", "Seuls les adhérents peuvent emprunter.");
            return "redirect:/livres";
        }
        Exemplaire exemplaire = exemplaireRepository.findById(exemplaireId).get();
        if (exemplaire.getStatut().equals("reserve")) {
            Reservation reservation = reservationRepository.findByExemplaireIdAndStatut(exemplaireId, Reservation.Statut.approuve);
            if (reservation == null || !reservation.getUtilisateur().getId().equals(utilisateur.getId())) {
                model.addAttribute("error", "Cet exemplaire est réservé pour un autre utilisateur.");
                return "redirect:/livres";
            }
        } else if (!exemplaire.getStatut().equals("disponible")) {
            model.addAttribute("error", "Cet exemplaire n'est pas disponible pour l'emprunt.");
            return "redirect:/livres";
        }
        Pret pret = new Pret();
        pret.setUtilisateurId(utilisateur.getId());
        pret.setExemplaireId(exemplaire.getId());
        pret.setDatePret(LocalDate.parse(datePret));
        pret.setDateRetourPrevue(LocalDate.parse(dateRetourPrevue));
        pret.setStatut("en cours");
        exemplaire.setStatut("emprunte");
        pretRepository.save(pret);
        exemplaireRepository.save(exemplaire);
        if (exemplaire.getStatut().equals("reserve")) {
            Reservation reservation = reservationRepository.findByExemplaireIdAndStatut(exemplaireId, Reservation.Statut.approuve);
            if (reservation != null) {
                reservation.setStatut(Reservation.Statut.rejete);
                reservationRepository.save(reservation);
            }
        }
        return "redirect:/home";
    }

    @GetMapping("/prets/retour")
    public String afficherFormulaireRetour(@RequestParam("pretId") Long pretId, Model model) {
        Pret pret = pretRepository.findById(pretId)
                .orElseThrow(() -> new RuntimeException("Prêt non trouvé"));
        if (!pret.getStatut().equals("en cours")) {
            model.addAttribute("error", "Ce prêt est déjà terminé ou en retard.");
            return "retour-pret";
        }
        model.addAttribute("pretId", pretId);
        model.addAttribute("exemplaireId", pret.getExemplaireId());
        return "retour-pret";
    }

    @PostMapping("/prets/retour")
    public String retournerPret(@RequestParam("pretId") Long pretId,
                               @RequestParam("dateRetourEffective") String dateRetourEffective,
                               Model model) {
        Pret pret = pretRepository.findById(pretId)
                .orElseThrow(() -> new RuntimeException("Prêt non trouvé"));
        if (!pret.getStatut().equals("en cours")) {
            model.addAttribute("error", "Ce prêt est déjà terminé ou en retard.");
            return "retour-pret";
        }
        Exemplaire exemplaire = exemplaireRepository.findById(pret.getExemplaireId())
                .orElseThrow(() -> new RuntimeException("Exemplaire non trouvé"));
        pret.setDateRetourEffective(LocalDate.parse(dateRetourEffective));
        pret.setStatut("termine");
        exemplaire.setStatut("disponible");
        pretRepository.save(pret);
        exemplaireRepository.save(exemplaire);
        return "redirect:/prets";
    }
}