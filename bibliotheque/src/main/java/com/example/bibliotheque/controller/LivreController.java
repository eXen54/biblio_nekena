package com.example.bibliotheque.controller;

import com.example.bibliotheque.repository.LivreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LivreController {

    @Autowired
    private LivreRepository livreRepository;

    @GetMapping("/livres")
    public String afficherListeLivres(Model model) {
        model.addAttribute("livres", livreRepository.findAll());
        return "livres";
    }
}