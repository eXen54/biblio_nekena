CREATE DATABASE IF NOT EXISTS biblio_db;
USE biblio_db;

-- Table: utilisateur
CREATE TABLE IF NOT EXISTS utilisateur (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(255) NOT NULL UNIQUE,
    type ENUM('Admin', 'Adherent') NOT NULL,
    profil ENUM('Etudiant', 'Professionnel', 'Professeur', 'Anonyme') NULL,
    abonnement_type ENUM('Normal', 'VIP') NULL,
    abonnement_debut DATE,
    abonnement_fin DATE,
    mot_de_passe VARCHAR(255) NOT NULL
);

-- Table: livre
CREATE TABLE IF NOT EXISTS livre (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    titre VARCHAR(255) NOT NULL,
    auteur VARCHAR(255) NOT NULL,
    date_parution DATE,
    isbn VARCHAR(13) UNIQUE NOT NULL,
    categorie VARCHAR(50),
    editeur VARCHAR(100),
    langue VARCHAR(50)
);

-- Table: exemplaire
CREATE TABLE IF NOT EXISTS exemplaire (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    livre_id BIGINT NOT NULL,
    statut VARCHAR(20) NOT NULL,
    FOREIGN KEY (livre_id) REFERENCES livre(id)
);

-- Table: pret
CREATE TABLE IF NOT EXISTS pret (
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

-- Table: reservation
CREATE TABLE IF NOT EXISTS reservation (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    utilisateur_id BIGINT NOT NULL,
    exemplaire_id BIGINT NOT NULL,
    date_reservation DATE NOT NULL,
    date_expiration DATE NOT NULL,
    statut VARCHAR(20) NOT NULL,
    FOREIGN KEY (utilisateur_id) REFERENCES utilisateur(id),
    FOREIGN KEY (exemplaire_id) REFERENCES exemplaire(id)
);

-- Table: prolongement
CREATE TABLE IF NOT EXISTS prolongement (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    pret_id BIGINT NOT NULL,
    utilisateur_id BIGINT NOT NULL,
    date_demande DATE NOT NULL,
    nouvelle_date_retour DATE NOT NULL,
    statut VARCHAR(20) NOT NULL,
    FOREIGN KEY (pret_id) REFERENCES pret(id),
    FOREIGN KEY (utilisateur_id) REFERENCES utilisateur(id)
);