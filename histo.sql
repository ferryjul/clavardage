

	
-- Creation de la database
CREATE DATABASE historique CHARACTER SET 'utf8';

-- utilisation de la base de donnée
USE historique;

-- Creation de la table
CREATE TABLE HistConv (
    pseudo_and_date_debut_conv VARCHAR(100) NOT NULL,
    message TEXT
)
ENGINE=INNODB;

-- Creation de l'utilisateur qui pourra utiliser la BDD
CREATE USER 'java'@'localhost' IDENTIFIED BY 'pom2pin';
GRANT ALL ON historique.* TO 'java'@'localhost';


-- UTILISER LE FICHIER => SOURCE histo.sql;

-- LANCER SQL => mysql -h localhost -u root -p





