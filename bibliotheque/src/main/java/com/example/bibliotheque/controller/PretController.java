package com.example.bibliotheque.controller;

import com.example.bibliotheque.model.Exemplaire;
import com.example.bibliotheque.model.Pret;
import com.example.bibliotheque.model.Prolongement;
import com.example.bibliotheque.model.Reservation;
import com.example.bibliotheque.model.TypeUtilisateur;
import com.example.bibliotheque.model.Utilisateur;
import com.example.bibliotheque.repository.ExemplaireRepository;
import com.example.bibliotheque.repository.LivreRepository;
import com.example.bibliotheque.repository.PretRepository;
import com.example.bibliotheque.repository.ProlongementRepository;
import com.example.bibliotheque.repository.ReservationRepository;
import com.example.bibliotheque.repository.UtilisateurRepository;
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

    @GetMapping("/prets/nouveau")
    public String afficherFormulairePret(@RequestParam("exemplaireId") Long exemplaireId, Model model, Authentication authentication) {
        Exemplaire exemplaire = exemplaireRepository.findById(exemplaireId)
                .orElseThrow(() -> new RuntimeException("Exemplaire non trouvé"));
        Long livreId = exemplaire.getLivre().getId();
        if (!livreRepository.existsById(livreId)) {
            model.addAttribute("error", "Livre non trouvé...");
            return "redirect:/livres";
        }
        String username = authentication.getName();
        Utilisateur utilisateur = utilisateurRepository.findByNom(username)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        if (utilisateur.getType() != TypeUtilisateur.Adherent) {
            model.addAttribute("error", "Seuls les adhérents peuvent emprunter.");
            return "redirect:/livres";
        }
        List<Exemplaire> exemplairesDisponibles = exemplaireRepository.findByLivreIdAndStatut(livreId, "disponible");
        List<Exemplaire> exemplairesReserves = exemplaireRepository.findByLivreIdAndStatut(livreId, "reserve");
        List<Exemplaire> exemplairesEligibles = new ArrayList<>(exemplairesDisponibles);
        for (Exemplaire ex : exemplairesReserves) {
            Reservation reservation = reservationRepository.findByExemplaireIdAndStatut(ex.getId(), Reservation.Statut.approuve);
            if (reservation != null && reservation.getUtilisateur().getId().equals(utilisateur.getId())) {
                exemplairesEligibles.add(ex);
            }
        }
        if (!exemplairesEligibles.stream().anyMatch(ex -> ex.getId().equals(exemplaireId))) {
            model.addAttribute("error", "Cet exemplaire n'est pas disponible ou réservé pour vous.");
            return "redirect:/livres";
        }
        model.addAttribute("livre", livreRepository.findById(livreId).get());
        model.addAttribute("exemplaires", exemplairesEligibles);
        model.addAttribute("selectedExemplaireId", exemplaireId);
        model.addAttribute("utilisateur", utilisateur);
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
        String username = authentication.getName();
        Utilisateur utilisateur = utilisateurRepository.findByNom(username)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        if (utilisateur.getType() != TypeUtilisateur.Adherent) {
            model.addAttribute("error", "Seuls les adhérents peuvent emprunter.");
            return "redirect:/livres";
        }
        Exemplaire exemplaire = exemplaireRepository.findById(exemplaireId).get();
        boolean wasReserved = exemplaire.getStatut().equals("reserve");
        if (wasReserved) {
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
        if (wasReserved) {
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

    @GetMapping("/prets/prolongement")
    public String afficherFormulaireProlongement(@RequestParam("pretId") Long pretId, Model model, Authentication authentication) {
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
        Prolongement prolongement = new Prolongement();
        prolongement.setPretId(pretId);
        prolongement.setUtilisateurId(utilisateur.getId());
        prolongement.setDateDemande(LocalDate.now());
        prolongement.setNouvelleDateRetour(LocalDate.parse(nouvelleDateRetour));
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