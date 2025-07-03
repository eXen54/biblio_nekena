package com.example.bibliotheque.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "pret")
public class Pret {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "utilisateur_id", nullable = false)
    private Long utilisateurId;

    @Column(name = "exemplaire_id", nullable = false)
    private Long exemplaireId;

    @Column(name = "date_pret", nullable = false)
    private LocalDate datePret;

    @Column(name = "date_retour_prevue", nullable = false)
    private LocalDate dateRetourPrevue;

    @Column(name = "date_retour_effective")
    private LocalDate dateRetourEffective;

    @Column(name = "statut", nullable = false)
    private String statut; // "en cours", "terminé", "en retard"

    // Constructors
    public Pret() {}

    public Pret(Long utilisateurId, Long exemplaireId, LocalDate datePret, LocalDate dateRetourPrevue, String statut) {
        this.utilisateurId = utilisateurId;
        this.exemplaireId = exemplaireId;
        this.datePret = datePret;
        this.dateRetourPrevue = dateRetourPrevue;
        this.statut = statut;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUtilisateurId() { return utilisateurId; }
    public void setUtilisateurId(Long utilisateurId) { this.utilisateurId = utilisateurId; }
    public Long getExemplaireId() { return exemplaireId; }
    public void setExemplaireId(Long exemplaireId) { this.exemplaireId = exemplaireId; }
    public LocalDate getDatePret() { return datePret; }
    public void setDatePret(LocalDate datePret) { this.datePret = datePret; }
    public LocalDate getDateRetourPrevue() { return dateRetourPrevue; }
    public void setDateRetourPrevue(LocalDate dateRetourPrevue) { this.dateRetourPrevue = dateRetourPrevue; }
    public LocalDate getDateRetourEffective() { return dateRetourEffective; }
    public void setDateRetourEffective(LocalDate dateRetourEffective) { this.dateRetourEffective = dateRetourEffective; }
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
}