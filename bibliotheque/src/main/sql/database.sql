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

-- Create exemplaire table
CREATE TABLE exemplaire (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    livre_id BIGINT NOT NULL,
    statut VARCHAR(20) NOT NULL,
    FOREIGN KEY (livre_id) REFERENCES livre(id)
);

-- Drop and recreate pret table to reference exemplaire_id
DROP TABLE IF EXISTS pret;
CREATE TABLE pret (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    utilisateur_id BIGINT NOT NULL,
    exemplaire_id BIGINT NOT NULL,
    date_pret DATE NOT NULL,
    date_retour_prevue DATE NOT NULL,
    date_retour_effective DATE,
    statut VARCHAR(20) NOT NULL,
    FOREIGN KEY (utilisateur_id) REFERENCES utilisateur(id),
    FOREIGN KEY (exemplaire_id) REFERENCES exemplaire(id)
);

