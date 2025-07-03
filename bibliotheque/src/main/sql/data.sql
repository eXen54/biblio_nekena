-- Clear existing data
DELETE FROM reservation;
DELETE FROM pret;
DELETE FROM exemplaire;
DELETE FROM livre;
DELETE FROM utilisateur;

-- Insert utilisateurs (passwords hashed with BCrypt for 'admin123' and 'user123')
INSERT INTO utilisateur (nom, type, profil, abonnement_type, abonnement_debut, abonnement_fin, mot_de_passe) 
VALUES 
    ('admin', 'Admin', NULL, NULL, NULL, NULL, 'admin'), -- admin123
    ('mimi', 'Adherent', 'Etudiant', 'Normal', '2025-07-01', '2025-12-31', 'mimi'), -- user123
    ('lolo', 'Adherent', 'Anonyme', 'Normal', '2025-07-01', '2025-12-31', 'lolo'); -- user123

-- Insert livres
INSERT INTO livre (titre, auteur, date_parution, isbn, categorie, editeur, langue) 
VALUES 
    ('Livre 1', 'Auteur 1', '2020-01-01', '9783161484100', 'Fiction', 'Éditeur 1', 'Français'),
    ('Livre 2', 'Auteur 2', '2021-06-15', '9781234567897', 'Science', 'Éditeur 2', 'Anglais');

-- Insert exemplaires
INSERT INTO exemplaire (livre_id, statut) 
VALUES 
    (1, 'disponible'), -- Exemplaire ID: 1 for Livre 1
    (1, 'emprunte'),   -- Exemplaire ID: 2 for Livre 1
    (2, 'disponible'); -- Exemplaire ID: 3 for Livre 2

-- Insert prets
INSERT INTO pret (utilisateur_id, exemplaire_id, date_pret, date_retour_prevue, statut) 
VALUES 
    (2, 2, '2025-07-01', '2025-07-15', 'en cours'); -- mimi borrows Exemplaire ID: 2

-- Insert reservations
INSERT INTO reservation (utilisateur_id, exemplaire_id, date_reservation, date_expiration, statut) 
VALUES 
    (2, 1, '2025-07-03', '2025-07-10', 'en_attente'), -- mimi reserves Exemplaire ID: 1
    (3, 1, '2025-07-03', '2025-07-10', 'en_attente'); -- lolo reserves Exemplaire ID: 1