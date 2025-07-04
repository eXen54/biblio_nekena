package com.example.bibliotheque.controller;

import com.example.bibliotheque.model.Exemplaire;
import com.example.bibliotheque.model.Livre;
import com.example.bibliotheque.model.Pret;
import com.example.bibliotheque.model.Prolongement;
import com.example.bibliotheque.model.Reservation;
import com.example.bibliotheque.model.TypeUtilisateur;
import com.example.bibliotheque.model.ProfilUtilisateur;
import com.example.bibliotheque.model.Utilisateur;
import com.example.bibliotheque.repository.ExemplaireRepository;
import com.example.bibliotheque.repository.LivreRepository;
import com.example.bibliotheque.repository.PretRepository;
import com.example.bibliotheque.repository.ProlongementRepository;
import com.example.bibliotheque.repository.ReservationRepository;
import com.example.bibliotheque.repository.UtilisateurRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @Autowired
    private ProlongementRepository prolongementRepository;

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @GetMapping("/prets")
    public String afficherPrets(Model model, Authentication authentication) {
        String username = authentication.getName();
        Utilisateur utilisateur = utilisateurRepository.findByNom(username)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        List<Pret> prets;
        if (utilisateur.getType() == TypeUtilisateur.Adherent) {
            prets = pretRepository.findByUtilisateurId(utilisateur.getId());
        } else {
            prets = pretRepository.findAll();
        }
        model.addAttribute("prets", prets);
        model.addAttribute("utilisateur", utilisateur);
        return "prets";
    }

    private static final Logger logger = LoggerFactory.getLogger(PretController.class);

    @GetMapping("/prets/nouveau")
    public String afficherFormulairePret(@RequestParam("exemplaireId") Long exemplaireId, Model model,
            Authentication authentication) {

        logger.debug("Processing /prets/nouveau with exemplaireId: {}", exemplaireId);

        // Get authenticated user
        String username = authentication.getName();
        Utilisateur utilisateur = utilisateurRepository.findByNom(username)
                .orElse(null);
        if (utilisateur == null) {

            logger.warn("Utilisateur not found for username: {}", username);
            model.addAttribute("error", "Utilisateur non trouvé.");
            return "nouveau-pret";
        }

        // Check user role
        if (utilisateur.getType() != TypeUtilisateur.Adherent) {

            logger.debug("Utilisateur {} is not an Adherent", username);
            model.addAttribute("error", "Seuls les adhérents peuvent emprunter.");
            return "nouveau-pret";
        }

        // Find exemplaire
        Exemplaire exemplaire = exemplaireRepository.findById(exemplaireId)
                .orElse(null);
        if (exemplaire == null) {

            logger.warn("Exemplaire not found for ID: {}", exemplaireId);
            model.addAttribute("error", "Exemplaire non trouvé pour l'ID : " + exemplaireId);
            return "nouveau-pret";
        }

        // Get associated livre
        Livre livre = exemplaire.getLivre();
        if (livre == null) {

            logger.warn("Livre not found for exemplaire ID: {}", exemplaireId);
            model.addAttribute("error", "Livre non trouvé pour cet exemplaire.");
            return "nouveau-pret";
        }

        // Verify livre exists in repository
        Long livreId = livre.getId();
        if (!livreRepository.existsById(livreId)) {

            logger.warn("Livre not found in repository for ID: {}", livreId);
            model.addAttribute("error", "Livre non trouvé.");
            return "nouveau-pret";
        }

        // Check eligible exemplaires
        List<Exemplaire> exemplairesDisponibles = exemplaireRepository.findByLivreIdAndStatut(livreId, "disponible");
        List<Exemplaire> exemplairesReserves = exemplaireRepository.findByLivreIdAndStatut(livreId, "reserve");
        List<Exemplaire> exemplairesEligibles = new ArrayList<>(exemplairesDisponibles);
        for (Exemplaire ex : exemplairesReserves) {
            Reservation reservation = reservationRepository.findByExemplaireIdAndStatut(ex.getId(),
                    Reservation.Statut.approuve);
            if (reservation != null && reservation.getUtilisateur().getId().equals(utilisateur.getId())) {
                exemplairesEligibles.add(ex);
            }
        }

        // Verify exemplaire is eligible
        if (!exemplairesEligibles.stream().anyMatch(ex -> ex.getId().equals(exemplaireId))) {

            logger.debug("Exemplaire {} is not eligible for utilisateur {}", exemplaireId, utilisateur.getId());
            model.addAttribute("error", "Cet exemplaire n'est pas disponible ou réservé pour vous.");
            return "nouveau-pret";
        }

        // Add attributes to model
        model.addAttribute("livre", livre);
        model.addAttribute("exemplaires", exemplairesEligibles);
        model.addAttribute("selectedExemplaireId", exemplaireId);
        model.addAttribute("utilisateur", utilisateur);

        logger.debug("Loaded livre ID: {} and exemplaireId: {} for utilisateur: {}", livreId, exemplaireId, username);
        return "nouveau-pret";
    }

    @PostMapping("/prets/nouveau")
    public String creerPret(
            @RequestParam("livreId") Long livreId,
            @RequestParam("exemplaireId") Long exemplaireId,
            @RequestParam("datePret") String datePret,
            @RequestParam("dateRetourPrevue") String dateRetourPrevue,
            Authentication authentication,
            Model model) {
        // Find the copy (Exemplaire) by ID
        Exemplaire exemplaire = exemplaireRepository.findById(exemplaireId)
                .orElse(null);
        if (exemplaire == null) {
            model.addAttribute("error", "Exemplaire non trouvé pour l'ID : " + exemplaireId);
            return "nouveau-pret";
        }

        // Get the associated book (Livre) from the copy
        Livre livre = exemplaire.getLivre();
        if (livre == null) {
            model.addAttribute("error", "Livre non trouvé pour cet exemplaire.");
            return "nouveau-pret";
        }

        String username = authentication.getName();
        Utilisateur utilisateur = utilisateurRepository.findByNom(username)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        // Check if user is an Adherent
        if (utilisateur.getType() != TypeUtilisateur.Adherent) {
            model.addAttribute("error", "Seuls les adhérents peuvent emprunter.");
            return "nouveau-pret";
        }

        // Check penalty status
        LocalDate penaliteFin = utilisateur.getPenaliteFin();
        LocalDate parsedDatePret = LocalDate.parse(datePret);
        LocalDate parsedDateRetourPrevue = LocalDate.parse(dateRetourPrevue);
        if (penaliteFin != null
                && (parsedDatePret.isBefore(penaliteFin) || parsedDateRetourPrevue.isBefore(penaliteFin))) {
            model.addAttribute("error", "Vous serez encore en pénalité jusqu'au " + penaliteFin);
            model.addAttribute("livre", livre);
            model.addAttribute("exemplaires", exemplaireRepository.findByLivreIdAndStatut(livreId, "disponible"));
            model.addAttribute("selectedExemplaireId", exemplaireId);
            model.addAttribute("utilisateur", utilisateur);
            return "nouveau-pret";
        }

        // Validate livre and exemplaire existence
        if (!livreRepository.existsById(livreId) || !exemplaireRepository.existsById(exemplaireId)) {
            model.addAttribute("error", "Livre ou exemplaire non trouvé.");
            return "nouveau-pret";
        }

        // Check if exemplaire is reserved or available
        boolean wasReserved = exemplaire.getStatut().equals("reserve");
        if (wasReserved) {
            Reservation reservation = reservationRepository.findByExemplaireIdAndStatut(exemplaireId,
                    Reservation.Statut.approuve);
            if (reservation == null || !reservation.getUtilisateur().getId().equals(utilisateur.getId())) {
                model.addAttribute("error", "Cet exemplaire est réservé pour un autre utilisateur.");
                return "nouveau-pret";
            }
        } else if (!exemplaire.getStatut().equals("disponible")) {
            model.addAttribute("error", "Cet exemplaire n'est pas disponible pour l'emprunt.");
            return "nouveau-pret";
        }

        // Quota validation for each profile
        long currentLoans = pretRepository.countByUtilisateurAndStatut(utilisateur, "en cours");
        if (utilisateur.getProfil() == ProfilUtilisateur.Etudiant && currentLoans >= 5) {
            model.addAttribute("error", "Vous avez atteint votre quota de prêts (5 pour Etudiant).");
            model.addAttribute("livre", livre);
            model.addAttribute("exemplaires", exemplaireRepository.findByLivreIdAndStatut(livreId, "disponible"));
            model.addAttribute("selectedExemplaireId", exemplaireId);
            model.addAttribute("utilisateur", utilisateur);
            return "nouveau-pret";
        }
        if (utilisateur.getProfil() == ProfilUtilisateur.Professionnel && currentLoans >= 10) {
            model.addAttribute("error", "Vous avez atteint votre quota de prêts (10 pour Professionnel).");
            model.addAttribute("livre", livre);
            model.addAttribute("exemplaires", exemplaireRepository.findByLivreIdAndStatut(livreId, "disponible"));
            model.addAttribute("selectedExemplaireId", exemplaireId);
            model.addAttribute("utilisateur", utilisateur);
            return "nouveau-pret";
        }
        if (utilisateur.getProfil() == ProfilUtilisateur.Professeur && currentLoans >= 15) {
            model.addAttribute("error", "Vous avez atteint votre quota de prêts (15 pour Professeur).");
            model.addAttribute("livre", livre);
            model.addAttribute("exemplaires", exemplaireRepository.findByLivreIdAndStatut(livreId, "disponible"));
            model.addAttribute("selectedExemplaireId", exemplaireId);
            model.addAttribute("utilisateur", utilisateur);
            return "nouveau-pret";
        }
        if (utilisateur.getProfil() == ProfilUtilisateur.Anonyme && currentLoans >= 2) {
            model.addAttribute("error", "Vous avez atteint votre quota de prêts (2 pour Anonyme).");
            model.addAttribute("livre", livre);
            model.addAttribute("exemplaires", exemplaireRepository.findByLivreIdAndStatut(livreId, "disponible"));
            model.addAttribute("selectedExemplaireId", exemplaireId);
            model.addAttribute("utilisateur", utilisateur);
            return "nouveau-pret";
        }

        // Create and save loan
        Pret pret = new Pret();
        pret.setUtilisateurId(utilisateur.getId());
        pret.setExemplaireId(exemplaireId);
        pret.setDatePret(parsedDatePret);
        pret.setDateRetourPrevue(parsedDateRetourPrevue);
        pret.setStatut("en cours");
        exemplaire.setStatut("emprunte");
        pretRepository.save(pret);
        exemplaireRepository.save(exemplaire);

        // Update reservation if applicable
        if (wasReserved) {
            Reservation reservation = reservationRepository.findByExemplaireIdAndStatut(exemplaireId,
                    Reservation.Statut.approuve);
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
        LocalDate retourDate = LocalDate.parse(dateRetourEffective);
        pret.setDateRetourEffective(retourDate);
        pret.setStatut("termine");
        // Check for late return and apply penalty
        if (retourDate.isAfter(pret.getDateRetourPrevue())) {
            Utilisateur utilisateur = utilisateurRepository.findById(pret.getUtilisateurId())
                    .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
            utilisateur.setPenaliteFin(retourDate.plusDays(10));
            utilisateurRepository.save(utilisateur);
            model.addAttribute("message",
                    "Livre rendu en retard. Vous êtes sous pénalité jusqu'au " + utilisateur.getPenaliteFin());
        } else {
            model.addAttribute("message", "Livre rendu avec succès.");
        }
        exemplaire.setStatut("disponible");
        pretRepository.save(pret);
        exemplaireRepository.save(exemplaire);
        return "retour-pret";
    }

    @GetMapping("/prets/prolongement")
    public String afficherFormulaireProlongement(@RequestParam("pretId") Long pretId, Model model,
            Authentication authentication) {
        Pret pret = pretRepository.findById(pretId)
                .orElseThrow(() -> new RuntimeException("Prêt non trouvé"));
        String username = authentication.getName();
        Utilisateur utilisateur = utilisateurRepository.findByNom(username)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        if (utilisateur.getType() != TypeUtilisateur.Adherent) {
            model.addAttribute("error", "Seuls les adhérents peuvent demander une prolongation.");
            return "redirect:/prets";
        }
        if (!pret.getUtilisateurId().equals(utilisateur.getId())) {
            model.addAttribute("error", "Vous ne pouvez pas prolonger ce prêt.");
            return "redirect:/prets";
        }
        if (!pret.getStatut().equals("en cours")) {
            model.addAttribute("error", "Ce prêt ne peut pas être prolongé.");
            return "redirect:/prets";
        }
        List<Prolongement> prolongements = prolongementRepository.findByPretId(pretId);
        for (Prolongement prolongement : prolongements) {
            if (prolongement.getStatut().equals("en_attente")) {
                model.addAttribute("error", "Une demande de prolongation est déjà en attente.");
                return "redirect:/prets";
            }
        }
        if (pret == null) {
            model.addAttribute("error", "null id.");
            return "redirect:/prets";
        }
        model.addAttribute("pretId", pretId);
        model.addAttribute("currentDateRetourPrevue", pret.getDateRetourPrevue());
        model.addAttribute("utilisateur", utilisateur);
        return "prolongement-pret";
    }

    @PostMapping("/prets/prolongement")
    public String soumettreProlongement(@RequestParam("pretId") Long pretId,
            @RequestParam("nouvelleDateRetour") String nouvelleDateRetour,
            Authentication authentication,
            Model model) {
        Pret pret = pretRepository.findById(pretId)
                .orElseThrow(() -> new RuntimeException("Prêt non trouvé"));
        String username = authentication.getName();
        Utilisateur utilisateur = utilisateurRepository.findByNom(username)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        // Check if user is an Adherent
        if (utilisateur.getType() != TypeUtilisateur.Adherent) {
            model.addAttribute("error", "Seuls les adhérents peuvent demander une prolongation.");
            return "redirect:/prets";
        }

        // Check if user owns the loan
        if (!pret.getUtilisateurId().equals(utilisateur.getId())) {
            model.addAttribute("error", "Vous ne pouvez pas prolonger ce prêt.");
            return "redirect:/prets";
        }

        // Check if loan is in progress
        if (!pret.getStatut().equals("en cours")) {
            model.addAttribute("error", "Ce prêt ne peut pas être prolongé.");
            return "redirect:/prets";
        }

        // Check for existing pending prolongation requests
        List<Prolongement> prolongements = prolongementRepository.findByPretId(pretId);
        for (Prolongement prolongement : prolongements) {
            if (prolongement.getStatut().equals("en_attente")) {
                model.addAttribute("error", "Une demande de prolongation est déjà en attente.");
                return "redirect:/prets";
            }
        }

        // Check penalty status
        LocalDate datePret = pret.getDatePret();
        LocalDate nouvelleDateRetourParsed = LocalDate.parse(nouvelleDateRetour);
        LocalDate penaliteFin = utilisateur.getPenaliteFin();
        if (penaliteFin != null && (datePret.isBefore(penaliteFin) || nouvelleDateRetourParsed.isBefore(penaliteFin))) {
            model.addAttribute("error", "Vous serez encore en pénalité jusqu'au " + penaliteFin);
            model.addAttribute("pretId", pretId);
            model.addAttribute("currentDateRetourPrevue", pret.getDateRetourPrevue());
            model.addAttribute("utilisateur", utilisateur);
            return "prolongement-pret";
        }

        // Proceed with prolongation request
        Prolongement prolongement = new Prolongement();
        prolongement.setPretId(pretId);
        prolongement.setUtilisateurId(utilisateur.getId());
        prolongement.setDateDemande(LocalDate.now());
        prolongement.setNouvelleDateRetour(nouvelleDateRetourParsed);
        prolongement.setStatut("en_attente");
        prolongementRepository.save(prolongement);

        return "redirect:/prets";
    }

    @GetMapping("/prolongements")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String afficherProlongements(Model model, Authentication authentication) {
        String username = authentication.getName();
        Utilisateur utilisateur = utilisateurRepository.findByNom(username)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        if (utilisateur.getType() != TypeUtilisateur.Admin) {
            model.addAttribute("error", "Seuls les bibliothécaires peuvent gérer les prolongations.");
            return "redirect:/home";
        }
        List<Prolongement> prolongements = prolongementRepository.findByStatut("en_attente");
        model.addAttribute("prolongements", prolongements);
        return "prolongements";
    }

    @PostMapping("/prolongements/valider")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String validerProlongement(@RequestParam("prolongementId") Long prolongementId,
            @RequestParam("decision") String decision,
            Authentication authentication,
            Model model) {
        String username = authentication.getName();
        Utilisateur utilisateur = utilisateurRepository.findByNom(username)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        if (utilisateur.getType() != TypeUtilisateur.Admin) {
            model.addAttribute("error", "Seuls les bibliothécaires peuvent valider les prolongations.");
            return "redirect:/home";
        }
        Prolongement prolongement = prolongementRepository.findById(prolongementId)
                .orElseThrow(() -> new RuntimeException("Prolongement non trouvé"));
        if (!prolongement.getStatut().equals("en_attente")) {
            model.addAttribute("error", "Cette demande de prolongation a déjà été traitée.");
            return "redirect:/prolongements";
        }
        if (decision.equals("approuve")) {
            prolongement.setStatut("approuve");
            Pret pret = pretRepository.findById(prolongement.getPretId())
                    .orElseThrow(() -> new RuntimeException("Prêt non trouvé"));
            pret.setDateRetourPrevue(prolongement.getNouvelleDateRetour());
            pretRepository.save(pret);
        } else if (decision.equals("rejete")) {
            prolongement.setStatut("rejete");
        } else {
            model.addAttribute("error", "Décision invalide.");
            return "redirect:/prolongements";
        }
        prolongementRepository.save(prolongement);
        return "redirect:/prolongements";
    }
}