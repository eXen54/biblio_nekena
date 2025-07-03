CREATE DATABASE biblio_db;
USE biblio_db;

CREATE TABLE utilisateur (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(255) NOT NULL,
    type ENUM('Admin', 'Adherent') NOT NULL,
    profil ENUM('Etudiant', 'Professionnel', 'Professeur', 'Anonyme') NULL, -- NULL pour Admin
    abonnement_type ENUM('Normal', 'VIP') NULL, -- NULL pour Admin ou non-abonnés
    abonnement_debut DATE,
    abonnement_fin DATE,
    mot_de_passe VARCHAR(255) NOT NULL
);

CREATE TABLE livre (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    titre VARCHAR(255) NOT NULL,
    auteur VARCHAR(255) NOT NULL,
    date_parution DATE,
    isbn VARCHAR(13) UNIQUE NOT NULL,
    categorie VARCHAR(50),
    editeur VARCHAR(100),
    langue VARCHAR(50)
);