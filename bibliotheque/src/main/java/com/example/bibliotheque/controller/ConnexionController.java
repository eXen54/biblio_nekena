package com.example.bibliotheque.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ConnexionController {
    @GetMapping("/connexion")
    public String showLoginPage() {
        return "connexion";
    }
}