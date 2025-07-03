package com.example.bibliotheque.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.time.LocalDate;

@Entity
public class Livre {
    @Id
    private Long id;
    private String titre;
    private String auteur;
    private LocalDate dateParution;
    private String isbn;
    private String categorie;
    private String editeur;
    private String langue;
    private transient int exemplairesDisponibles;
    private transient Long exemplaireDisponibleId;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }
    public String getAuteur() { return auteur; }
    public void setAuteur(String auteur) { this.auteur = auteur; }
    public LocalDate getDateParution() { return dateParution; }
    public void setDateParution(LocalDate dateParution) { this.dateParution = dateParution; }
    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
    public String getCategorie() { return categorie; }
    public void setCategorie(String categorie) { this.categorie = categorie; }
    public String getEditeur() { return editeur; }
    public void setEditeur(String editeur) { this.editeur = editeur; }
    public String getLangue() { return langue; }
    public void setLangue(String langue) { this.langue = langue; }
    public int getExemplairesDisponibles() { return exemplairesDisponibles; }
    public void setExemplairesDisponibles(int exemplairesDisponibles) { this.exemplairesDisponibles = exemplairesDisponibles; }
    public Long getExemplaireDisponibleId() { return exemplaireDisponibleId; }
    public void setExemplaireDisponibleId(Long exemplaireDisponibleId) { this.exemplaireDisponibleId = exemplaireDisponibleId; }
}