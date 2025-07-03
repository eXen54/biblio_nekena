INSERT INTO utilisateur (nom, type, profil, abonnement_type, abonnement_debut, abonnement_fin, mot_de_passe) 
VALUES 
    ('admin1', 'Admin', NULL, NULL, NULL, NULL, '$2a$10$8y9h6fB8Q7j/8J8m3Xz4qO9z1X2Y3Z4W5X6Y7Z8A9B0C1D2E3F4G'), -- Mot de passe : admin123
    ('etudiant1', 'Adherent', 'Etudiant', 'Normal', '2025-07-01', '2025-12-31', '$2a$10$8y9h6fB8Q7j/8J8m3Xz4qO9z1X2Y3Z4W5X6Y7Z8A9B0C1D2E3F4G'), -- Mot de passe : user123
    ('anonyme1', 'Adherent', 'Anonyme', 'Normal', '2025-07-01', '2025-12-31', '$2a$10$8y9h6fB8Q7j/8J8m3Xz4qO9z1X2Y3Z4W5X6Y7Z8A9B0C1D2E3F4G'); -- Mot de passe : user123

INSERT INTO utilisateur (nom, type, profil, abonnement_type, abonnement_debut, abonnement_fin, mot_de_passe) 
VALUES 
    ('admin2', 'Admin', NULL, NULL, NULL, NULL, '$2a$10$GOLB.yLp2uW5j2p3.E0CjeCoL0a2y9a3j4b5c6d7e8f9g0h1i2j'), -- Mot de passe : admin123
    ('lala', 'Admin', NULL, NULL, NULL, NULL, '$2a$10$V1a2b3c4d5e6f7g8h9i0j.kL/mN.oP.qR.sT.uV.wX.yZ.1a2b3c'); -- Mot de passe : lala

INSERT INTO utilisateur (nom, type, profil, abonnement_type, abonnement_debut, abonnement_fin, mot_de_passe) 
VALUES 
    ('lolo', 'Adherent', 'Anonyme', 'Normal', '2025-07-01', '2025-12-31', 'lolo');
INSERT INTO livre (titre, auteur, date_parution, isbn, categorie, editeur, langue) 
VALUES 
    ('Livre Test', 'Auteur Test', '2020-01-01', '1234567890123', 'Roman', 'Editeur Test', 'Français'),
    ('Livre Scientifique', 'Auteur Sci', '2019-06-15', '9876543210987', 'Science', 'Editeur Sci', 'Français');
-- Insert sample data
INSERT INTO pret (utilisateur_id, livre_id, date_pret, date_retour_prevue, statut) VALUES
(1, 1, '2025-07-01', '2025-07-15', 'en cours'),
(2, 2, '2025-07-02', '2025-07-16', 'en cours');

-- Insert sample data
INSERT INTO utilisateur (nom, type, profil, abonnement_type, abonnement_debut, abonnement_fin, mot_de_passe) VALUES
('admin', 'Admin', NULL, NULL, NULL, NULL, 'admin'),
('user1', 'Adherent', 'Etudiant', 'Normal', '2025-01-01', '2025-12-31', 'user1');

INSERT INTO livre (titre, auteur, date_parution, isbn, categorie, editeur, langue) VALUES
('Livre 1', 'Auteur 1', '2020-01-01', '9783161484100', 'Fiction', 'Éditeur 1', 'Français'),
('Livre 2', 'Auteur 2', '2021-06-15', '9781234567897', 'Science', 'Éditeur 2', 'Anglais');

INSERT INTO exemplaire (livre_id, statut) VALUES
(1, 'disponible'),
(1, 'disponible'),
(2, 'disponible');

INSERT INTO pret (utilisateur_id, exemplaire_id, date_pret, date_retour_prevue, statut) VALUES
(1, 1, '2025-07-01', '2025-07-15', 'en cours'),
(2, 3, '2025-07-02', '2025-07-16', 'en cours');