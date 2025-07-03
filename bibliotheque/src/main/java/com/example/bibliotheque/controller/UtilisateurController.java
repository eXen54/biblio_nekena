package com.example.bibliotheque.controller;

import com.example.bibliotheque.model.ProfilUtilisateur;
import com.example.bibliotheque.model.TypeAbonnement;
import com.example.bibliotheque.model.TypeUtilisateur;
import com.example.bibliotheque.model.Utilisateur;
import com.example.bibliotheque.repository.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class UtilisateurController {

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @GetMapping("/inscription")
    public String afficherFormulaireInscription() {
        return "inscription";
    }

    @PostMapping("/inscription")
    public String enregistrerUtilisateur(
            @RequestParam("nom") String nom,
            @RequestParam("motDePasse") String motDePasse,
            @RequestParam("profil") String profil,
            @RequestParam("abonnementType") String abonnementType,
            @RequestParam("abonnementDebut") String abonnementDebut,
            @RequestParam("abonnementFin") String abonnementFin,
            Model model) {
        if (utilisateurRepository.findByNom(nom).isPresent()) {
            model.addAttribute("error", "Ce nom d'utilisateur existe déjà.");
            return "inscription";
        }
        try {
            Utilisateur utilisateur = new Utilisateur();
            utilisateur.setNom(nom);
            utilisateur.setMotDePasse(motDePasse); // NoOpPasswordEncoder, plain text
            utilisateur.setType(TypeUtilisateur.Adherent); // Always Adherent for sign-up
            utilisateur.setProfil(ProfilUtilisateur.valueOf(profil));
            utilisateur.setAbonnementType(TypeAbonnement.valueOf(abonnementType));
            utilisateur.setAbonnementDebut(java.time.LocalDate.parse(abonnementDebut));
            utilisateur.setAbonnementFin(java.time.LocalDate.parse(abonnementFin));
            utilisateurRepository.save(utilisateur);
            return "redirect:/connexion";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", "Profil ou type d'abonnement invalide.");
            return "inscription";
        }
    }
}