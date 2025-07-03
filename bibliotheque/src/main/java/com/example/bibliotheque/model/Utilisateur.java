package com.example.bibliotheque.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class Utilisateur {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeUtilisateur type;

    @Enumerated(EnumType.STRING)
    private ProfilUtilisateur profil;

    @Enumerated(EnumType.STRING)
    private TypeAbonnement abonnementType;

    private LocalDate abonnementDebut;
    private LocalDate abonnementFin;

    @Column(nullable = false)
    private String motDePasse;

    // Getters et setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public TypeUtilisateur getType() { return type; }
    public void setType(TypeUtilisateur type) { this.type = type; }
    public ProfilUtilisateur getProfil() { return profil; }
    public void setProfil(ProfilUtilisateur profil) { this.profil = profil; }
    public TypeAbonnement getAbonnementType() { return abonnementType; }
    public void setAbonnementType(TypeAbonnement abonnementType) { this.abonnementType = abonnementType; }
    public LocalDate getAbonnementDebut() { return abonnementDebut; }
    public void setAbonnementDebut(LocalDate abonnementDebut) { this.abonnementDebut = abonnementDebut; }
    public LocalDate getAbonnementFin() { return abonnementFin; }
    public void setAbonnementFin(LocalDate abonnementFin) { this.abonnementFin = abonnementFin; }
    public String getMotDePasse() { return motDePasse; }
    public void setMotDePasse(String motDePasse) { this.motDePasse = motDePasse; }
}




