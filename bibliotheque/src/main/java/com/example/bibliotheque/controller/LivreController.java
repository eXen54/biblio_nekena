package com.example.bibliotheque.controller;

import com.example.bibliotheque.model.Exemplaire;
import com.example.bibliotheque.model.Livre;
import com.example.bibliotheque.repository.ExemplaireRepository;
import com.example.bibliotheque.repository.LivreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class LivreController {

    @Autowired
    private LivreRepository livreRepository;

    @Autowired
    private ExemplaireRepository exemplaireRepository;

    @GetMapping("/livres")
    public String afficherLivres(Model model) {
        List<Livre> livres = livreRepository.findAll();
        for (Livre livre : livres) {
            List<Exemplaire> exemplairesDisponibles = exemplaireRepository.findByLivreIdAndStatut(livre.getId(), "disponible");
            livre.setExemplairesDisponibles(exemplairesDisponibles.size());
            if (!exemplairesDisponibles.isEmpty()) {
                livre.setExemplaireDisponibleId(exemplairesDisponibles.get(0).getId());
            }
        }
        model.addAttribute("livres", livres);
        return "livres";
    }
}