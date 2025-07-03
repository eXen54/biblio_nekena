INSERT INTO utilisateur (nom, type, profil, abonnement_type, abonnement_debut, abonnement_fin, mot_de_passe) 
VALUES 
    ('admin1', 'Admin', NULL, NULL, NULL, NULL, '$2a$10$8y9h6fB8Q7j/8J8m3Xz4qO9z1X2Y3Z4W5X6Y7Z8A9B0C1D2E3F4G'), -- Mot de passe : admin123
    ('etudiant1', 'Adherent', 'Etudiant', 'Normal', '2025-07-01', '2025-12-31', '$2a$10$8y9h6fB8Q7j/8J8m3Xz4qO9z1X2Y3Z4W5X6Y7Z8A9B0C1D2E3F4G'), -- Mot de passe : user123
    ('anonyme1', 'Adherent', 'Anonyme', 'Normal', '2025-07-01', '2025-12-31', '$2a$10$8y9h6fB8Q7j/8J8m3Xz4qO9z1X2Y3Z4W5X6Y7Z8A9B0C1D2E3F4G'); -- Mot de passe : user123

INSERT INTO utilisateur (nom, type, profil, abonnement_type, abonnement_debut, abonnement_fin, mot_de_passe) 
VALUES 
    ('admin2', 'Admin', NULL, NULL, NULL, NULL, '$2a$10$GOLB.yLp2uW5j2p3.E0CjeCoL0a2y9a3j4b5c6d7e8f9g0h1i2j'), -- Mot de passe : admin123
    ('lala', 'Admin', NULL, NULL, NULL, NULL, '$2a$10$V1a2b3c4d5e6f7g8h9i0j.kL/mN.oP.qR.sT.uV.wX.yZ.1a2b3c'); -- Mot de passe : lala

INSERT INTO livre (titre, auteur, date_parution, isbn, categorie, editeur, langue) 
VALUES 
    ('Livre Test', 'Auteur Test', '2020-01-01', '1234567890123', 'Roman', 'Editeur Test', 'Français'),
    ('Livre Scientifique', 'Auteur Sci', '2019-06-15', '9876543210987', 'Science', 'Editeur Sci', 'Français');