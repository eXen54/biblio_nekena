-- Clear existing data
DELETE FROM reservation;
DELETE FROM pret;
DELETE FROM exemplaire;
DELETE FROM livre;
DELETE FROM utilisateur;

-- Insert users
INSERT INTO utilisateur (id, nom, type, profil, abonnement_type, abonnement_debut, abonnement_fin, mot_de_passe) VALUES
(1, 'Admin User', 'Admin', NULL, NULL, NULL, NULL, 'mdp'),
(2, 'Mimi', 'Adherent', 'Etudiant', 'Normal', '2025-01-01', '2025-12-31', 'mdp'),
(3, 'Lolo', 'Adherent', 'Professeur', 'VIP', '2025-01-01', '2025-12-31', 'mdp'),
(4, 'Nana', 'Adherent', 'Professionnel', 'Normal', '2025-06-01', '2025-11-30', 'mdp');

-- Insert books
INSERT INTO livre (id, titre, auteur, date_parution, isbn, categorie, editeur, langue) VALUES
(1, 'Les Misérables', 'Victor Hugo', '1862-01-01', '9780140444308', 'Fiction', 'Penguin Classics', 'Français'),
(2, '1984', 'George Orwell', '1949-06-08', '9780451524935', 'Dystopie', 'Signet Classics', 'Anglais'),
(3, 'Le Petit Prince', 'Antoine de Saint-Exupéry', '1943-04-06', '9780156013987', 'Jeunesse', 'Harcourt', 'Français');

-- Insert exemplaires
INSERT INTO exemplaire (id, livre_id, statut) VALUES
(1, 1, 'disponible'),
(2, 1, 'disponible'),
(3, 2, 'disponible'),
(4, 2, 'disponible'),
(5, 3, 'disponible');

-- Insert loans
INSERT INTO pret (id, utilisateur_id, exemplaire_id, date_pret, date_retour_prevue, statut) VALUES
(1, 2, 2, '2025-07-01', '2025-07-15', 'en cours'),
(2, 3, 3, '2025-07-02', '2025-07-16', 'en cours');

-- Insert reservations
INSERT INTO reservation (id, utilisateur_id, exemplaire_id, date_reservation, date_expiration, statut) VALUES
(1, 3, 3, '2025-07-01', '2025-07-08', 'approuve'),
(2, 4, 4, '2025-07-03', '2025-07-10', 'en_attente');

-- Insert prolongements
INSERT INTO prolongement (id, pret_id, utilisateur_id, date_demande, nouvelle_date_retour, statut) VALUES
(1, 1, 2, '2025-07-03', '2025-07-22', 'en_attente'),
(2, 2, 3, '2025-07-03', '2025-07-23', 'en_attente');