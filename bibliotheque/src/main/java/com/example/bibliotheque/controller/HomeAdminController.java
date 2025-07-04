package com.example.bibliotheque.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeAdminController {

    @GetMapping("/homeAdmin")
    public String homeAdmin(Model model, Authentication authentication) {
        model.addAttribute("username", authentication.getName());
        return "homeAdmin";
    }
}