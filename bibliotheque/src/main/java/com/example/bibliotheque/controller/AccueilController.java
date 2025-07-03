package com.example.bibliotheque.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AccueilController {
    @GetMapping("/accueil")
    public String accueil() {
        return "accueil";
    }
}