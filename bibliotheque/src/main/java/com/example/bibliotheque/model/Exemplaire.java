package com.example.bibliotheque.model;

import jakarta.persistence.*;

@Entity
@Table(name = "exemplaire")
public class Exemplaire {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "livre_id", nullable = false)
    private Long livreId;

    @Column(name = "statut", nullable = false)
    private String statut; // "disponible", "emprunté"

    // Constructors
    public Exemplaire() {}

    public Exemplaire(Long livreId, String statut) {
        this.livreId = livreId;
        this.statut = statut;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getLivreId() { return livreId; }
    public void setLivreId(Long livreId) { this.livreId = livreId; }
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
}