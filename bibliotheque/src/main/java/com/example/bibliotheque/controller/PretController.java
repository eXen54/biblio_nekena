package com.example.bibliotheque.controller;

import com.example.bibliotheque.model.Exemplaire;
import com.example.bibliotheque.model.Pret;
import com.example.bibliotheque.repository.ExemplaireRepository;
import com.example.bibliotheque.repository.PretRepository;
import com.example.bibliotheque.repository.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.time.LocalDate;

@Controller
public class PretController {

    @Autowired
    private PretRepository pretRepository;

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private ExemplaireRepository exemplaireRepository;

    @GetMapping("/prets")
    public String afficherPrets(Model model) {
        model.addAttribute("prets", pretRepository.findAll());
        return "prets";
    }

    @GetMapping("/prets/nouveau")
    public String afficherFormulairePret(@RequestParam("exemplaireId") Long exemplaireId, Model model) {
        Exemplaire exemplaire = exemplaireRepository.findById(exemplaireId)
                .orElseThrow(() -> new RuntimeException("Exemplaire non trouvé"));
        if (!exemplaire.getStatut().equals("disponible")) {
            model.addAttribute("error", "Cet exemplaire n'est pas disponible.");
            return "nouveau-pret";
        }
        model.addAttribute("exemplaireId", exemplaireId);
        return "nouveau-pret";
    }

    @PostMapping("/prets/nouveau")
    public String creerPret(@RequestParam("exemplaireId") Long exemplaireId,
                           @RequestParam("datePret") String datePret,
                           @RequestParam("dateRetourPrevue") String dateRetourPrevue,
                           Model model) {
        Exemplaire exemplaire = exemplaireRepository.findById(exemplaireId)
                .orElseThrow(() -> new RuntimeException("Exemplaire non trouvé"));
        if (!exemplaire.getStatut().equals("disponible")) {
            model.addAttribute("error", "Cet exemplaire n'est pas disponible.");
            return "nouveau-pret";
        }
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Long utilisateurId = utilisateurRepository.findByNom(username)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"))
                .getId();
        Pret pret = new Pret(
                utilisateurId,
                exemplaireId,
                LocalDate.parse(datePret),
                LocalDate.parse(dateRetourPrevue),
                "en cours"
        );
        exemplaire.setStatut("emprunté");
        pretRepository.save(pret);
        exemplaireRepository.save(exemplaire);
        return "redirect:/prets";
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
        pret.setStatut("terminé");
        exemplaire.setStatut("disponible");
        pretRepository.save(pret);
        exemplaireRepository.save(exemplaire);
        return "redirect:/prets";
    }
}