package com.example.bibliotheque.controller;

import com.example.bibliotheque.model.TypeUtilisateur;
import com.example.bibliotheque.repository.ExemplaireRepository;
import com.example.bibliotheque.repository.PretRepository;
import com.example.bibliotheque.repository.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Controller
public class AdminController {

    @Autowired
    private PretRepository pretRepository;

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private ExemplaireRepository exemplaireRepository;

    @GetMapping("/admin/statistiques")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String afficherStatistiques(Model model) {
        // Total active loans
        long totalActiveLoans = pretRepository.countByStatut("en cours");

        // Overdue loans
        long overdueLoans = pretRepository.countByStatutAndDateRetourPrevueBefore("en cours", LocalDate.now());

        // Total users by type
        Map<TypeUtilisateur, Long> usersByType = new HashMap<>();
        for (TypeUtilisateur type : TypeUtilisateur.values()) {
            usersByType.put(type, utilisateurRepository.countByType(type));
        }

        // Loans by user type
        Map<TypeUtilisateur, Long> loansByType = new HashMap<>();
        for (TypeUtilisateur type : TypeUtilisateur.values()) {
            loansByType.put(type, pretRepository.countByUtilisateur_TypeAndStatut(type, "en cours"));
        }

        // Book inventory
        long totalBooks = exemplaireRepository.count();
        long availableBooks = exemplaireRepository.countByStatut("disponible");
        long borrowedBooks = exemplaireRepository.countByStatut("emprunte");

        // Add data to model
        model.addAttribute("totalActiveLoans", totalActiveLoans);
        model.addAttribute("overdueLoans", overdueLoans);
        model.addAttribute("usersByType", usersByType);
        model.addAttribute("loansByType", loansByType);
        model.addAttribute("totalBooks", totalBooks);
        model.addAttribute("availableBooks", availableBooks);
        model.addAttribute("borrowedBooks", borrowedBooks);

        return "admin-statistiques";
    }
}