package com.example.bibliotheque.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "prolongement")
public class Prolongement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "pret_id", nullable = false)
    private Long pretId;

    @Column(name = "utilisateur_id", nullable = false)
    private Long utilisateurId;

    @Column(name = "date_demande", nullable = false)
    private LocalDate dateDemande;

    @Column(name = "nouvelle_date_retour", nullable = false)
    private LocalDate nouvelleDateRetour;

    @Column(name = "statut", nullable = false)
    private String statut;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getPretId() { return pretId; }
    public void setPretId(Long pretId) { this.pretId = pretId; }
    public Long getUtilisateurId() { return utilisateurId; }
    public void setUtilisateurId(Long utilisateurId) { this.utilisateurId = utilisateurId; }
    public LocalDate getDateDemande() { return dateDemande; }
    public void setDateDemande(LocalDate dateDemande) { this.dateDemande = dateDemande; }
    public LocalDate getNouvelleDateRetour() { return nouvelleDateRetour; }
    public void setNouvelleDateRetour(LocalDate nouvelleDateRetour) { this.nouvelleDateRetour = nouvelleDateRetour; }
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
}