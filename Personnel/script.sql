CREATE TABLE LIGUE (
    num_ligue INT PRIMARY KEY AUTO_INCREMENT,
    nom VARCHAR(100) NOT NULL);


CREATE TABLE EMPLOYE (
    Num_employe INT PRIMARY KEY AUTO_INCREMENT,
    Mail VARCHAR(100) NOT NULL,
    Nom VARCHAR(50) NOT NULL,
    Prenom VARCHAR(50) NOT NULL,
    Password VARCHAR(255) NOT NULL,
    role_employe VARCHAR(50) NOT NULL,
    Date_arrivee DATE NOT NULL,
    Date_depart DATE,
    num_ligue_appartenir INT,
    num_ligue_administrer INT,
    
    CONSTRAINT fk_employe_appartenir FOREIGN KEY (num_ligue_appartenir) REFERENCES LIGUE(num_ligue),
    CONSTRAINT fk_employe_administrer FOREIGN KEY (num_ligue_administrer) REFERENCES LIGUE(num_ligue));