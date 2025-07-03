INSERT INTO utilisateur (nom, type, profil, abonnement_type, abonnement_debut, abonnement_fin, mot_de_passe) 
VALUES 
    ('admin1', 'Admin', NULL, NULL, NULL, NULL, '$2a$10$8y9h6fB8Q7j/8J8m3Xz4qO9z1X2Y3Z4W5X6Y7Z8A9B0C1D2E3F4G'), -- Mot de passe : admin123
    ('etudiant1', 'Adherent', 'Etudiant', 'Normal', '2025-07-01', '2025-12-31', '$2a$10$8y9h6fB8Q7j/8J8m3Xz4qO9z1X2Y3Z4W5X6Y7Z8A9B0C1D2E3F4G'), -- Mot de passe : user123
    ('anonyme1', 'Adherent', 'Anonyme', 'Normal', '2025-07-01', '2025-12-31', '$2a$10$8y9h6fB8Q7j/8J8m3Xz4qO9z1X2Y3Z4W5X6Y7Z8A9B0C1D2E3F4G'); -- Mot de passe : user123

INSERT INTO livre (titre, auteur, date_parution, isbn, categorie, editeur, langue) 
VALUES 
    ('Livre Test', 'Auteur Test', '2020-01-01', '1234567890123', 'Roman', 'Editeur Test', 'Français'),
    ('Livre Scientifique', 'Auteur Sci', '2019-06-15', '9876543210987', 'Science', 'Editeur Sci', 'Français');