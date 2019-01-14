

	
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

CREATE USER 'java'@'localhost' IDENTIFIED BY 'pom2pin';
GRANT ALL ON historique.* TO 'java'@'localhost' IDENTIFIED BY 'pom2pin';


-- UTILISER LE FICHIE => SOURCE histo.sql;

-- LANCER SQL => mysql -h localhost -u root -p


-- RECUPERER TOUS LES DONNES D'UN MEME UTILISATEUR

/* SELECT *
FROM HistConv
WHERE Pseudo_dest='Pseudo'
ORDER BY Date_session_conv;
*/

/*	INSERT INTO HistConv VALUES ('PseudoDate','----- DEBUT CONVERSATION -----') */
	
	
	
/*	Selection de tous les pseudos uniques => SELECT DISTINCT pseudo FROM HistConv ; 

CREATE TABLE Histo (
    pseudo VARCHAR(80) NOT NULL, 
    date VARCHAR(80) NOT NULL,
    eour VARCHAR(10) NOT NULL,
    message TEXT
)
*/


