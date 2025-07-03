package com.example.bibliotheque.service;

import com.example.bibliotheque.model.Utilisateur;
import com.example.bibliotheque.repository.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Utilisateur utilisateur = utilisateurRepository.findByNom(username)
            .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé : " + username));
        return new User(
            utilisateur.getNom(),
            utilisateur.getMotDePasse(),
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + utilisateur.getType().name().toUpperCase()))
        );
    }
}