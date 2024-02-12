--\Inizio File SQL DB Wiki Platform DB

CREATE SCHEMA IF NOT EXISTS "WIKI" AUTHORIZATION postgres;

/*
    ---------------------------
        !Creazione Domini!
    ---------------------------
*/
-- Vincolo Di Dominio : Password 
CREATE DOMAIN PASSWORD_DOMINIO AS VARCHAR(40)
	CHECK (VALUE ~ '^.*(?=.*[@!#$^*%&])(?=.*[0-9])(?=.*[a-zA-Z]).*$'
		AND VALUE LIKE '________%');

-- Vincolo Di Dominio: Username
CREATE DOMAIN USERNAME_DOMINIO AS VARCHAR(30)
    CHECK (VALUE ~ '^[a-z0-9_]{5,30}$');

-- Vincolo Di Dominio: Email
CREATE DOMAIN EMAIL_DOMINIO AS VARCHAR(50)
  CHECK (VALUE LIKE '%_@%_.__%');

-- Vincolo Di Dominio: Tipo di operazione: I: Inserimento, M: Modifica, C: Cancellazione
CREATE DOMAIN TIPO_OPERAZIONE AS CHAR(1)
  CHECK (VALUE LIKE 'I' OR VALUE LIKE 'M' OR VALUE LIKE 'C'); 

CREATE DOMAIN LEN_FRASE AS VARCHAR(100);

        
/*
    ---------------------------
        !Creazione Tabelle!
    ---------------------------
*/
/*
    ---------------------------
        !Table-UTENTE!
    ---------------------------
*/
CREATE TABLE UTENTE
(
    Username USERNAME_DOMINIO,
    Email EMAIL_DOMINIO NOT NULL,
    Password PASSWORD_DOMINIO NOT NULL,
    Autore BOOLEAN DEFAULT FALSE NOT NULL, --forse il not null va eliminato

    PRIMARY KEY(Username),
    UNIQUE(Email)
);

/*
    ---------------------------
        !Table-PAGINA!
    ---------------------------
*/
CREATE TABLE PAGINA 
(
    ID_Pagina SERIAL,
    Titolo VARCHAR(50) NOT NULL,
    Tema VARCHAR(50) NOT NULL, 
    DataCreazione TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UserAutore USERNAME_DOMINIO NOT NULL,

    PRIMARY KEY(ID_Pagina),
    FOREIGN KEY(UserAutore) REFERENCES UTENTE(Username) 
    ON DELETE CASCADE
    ON UPDATE CASCADE,
    UNIQUE(Titolo, Tema)
);

/*
    ---------------------------
        !Table-FRASE!
    ---------------------------
*/
CREATE TABLE FRASE 
(
    Riga INT,
    Ordine INT,
    ID_Pagina INT,
    Contenuto LEN_FRASE NOT NULL,
    Collegamento BOOLEAN DEFAULT FALSE NOT NULL, --forse il not null va eliminato

    PRIMARY KEY(Riga, Ordine, ID_Pagina),
    FOREIGN KEY(ID_Pagina) REFERENCES PAGINA(ID_Pagina) 
    ON DELETE CASCADE

);

/*
    ---------------------------
        !Table-COLLEGAMENTO!
    ---------------------------
*/
CREATE TABLE COLLEGAMENTO
(
    RigaFrase INT,
    OrdineFrase INT,
    ID_Pagina INT,
    ID_PaginaCollegata INT,

    PRIMARY KEY(RigaFrase, OrdineFrase, ID_Pagina, ID_PaginaCollegata),
    FOREIGN KEY(RigaFrase, OrdineFrase, ID_Pagina) REFERENCES FRASE(Riga, Ordine, ID_Pagina)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
    FOREIGN KEY(ID_PaginaCollegata) REFERENCES PAGINA(ID_Pagina)
    ON DELETE CASCADE
);

/*
    ---------------------------
        !Table-OPERAZIONE!
    ---------------------------
*/

CREATE TABLE OPERAZIONE
(
    ID_Operazione SERIAL,
    Tipo TIPO_OPERAZIONE NOT NULL,
    Proposta BOOLEAN NOT NULL,
    Riga INT NOT NULL,
    Ordine INT NOT NULL,
    FraseCoinvolta VARCHAR(100) NOT NULL,
    FraseModificata VARCHAR(100),
    Data TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ID_Pagina SERIAL NOT NULL,
    Utente USERNAME_DOMINIO NOT NULL DEFAULT 'Unknown', -- forse il NOT null va eliminato

    PRIMARY KEY(ID_Operazione),
    FOREIGN KEY(ID_Pagina) REFERENCES PAGINA(ID_Pagina) ON DELETE CASCADE,
    FOREIGN KEY(Utente) REFERENCES UTENTE(Username) ON DELETE SET DEFAULT
);


/*
    ---------------------------
        !Table-APPROVAZIONE!
    ---------------------------
*/

CREATE TABLE APPROVAZIONE
(
    ID_Operazione SERIAL,
    Autore USERNAME_DOMINIO, 
    Data TIMESTAMP,
    Risposta BOOLEAN,

    PRIMARY KEY(ID_Operazione, Autore),
    FOREIGN KEY(ID_Operazione) REFERENCES OPERAZIONE(ID_Operazione) ON DELETE CASCADE,
    FOREIGN KEY(Autore) REFERENCES UTENTE(Username) ON DELETE CASCADE
);



/*
  ---------------------------------
    VINCOLI SEMANTICI
  ---------------------------------

*/

-- non può esistere un operazione di modifica in cui l'attributo "fraseModificata" sia null.
ALTER TABLE OPERAZIONE
ADD CONSTRAINT controlloModifica CHECK(NOT(Tipo LIKE 'M' AND FraseModificata IS NULL));

-- non può esistere un operazione di cancellamento o di inserimento che abbia l'attributo "fraseModificata" not null.
ALTER TABLE OPERAZIONE
ADD CONSTRAINT controlloIC CHECK(NOT((Tipo LIKE 'I' OR Tipo LIKE 'C') AND FraseModificata IS NOT NULL)); --sembra non funzionare

-- non può esistere un operazione con proposta=true effettuata da un utente che è lo stesso autore della pagina. --DA SISTEMARE
CREATE OR REPLACE FUNCTION before_insert_proposta()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.proposta=TRUE AND NEW.utente = (SELECT UserAutore FROM PAGINA WHERE ID_Pagina=NEW.ID_Pagina) THEN
        RAISE EXCEPTION 'Impossibile inserire un record in "OPERAZIONE" con proposta=true e utente è lo stesso autore della pagina';
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER check_proposta
BEFORE INSERT
ON OPERAZIONE
FOR EACH ROW
EXECUTE FUNCTION before_insert_proposta();

-- non può esistere un approvazione riferita ad un'operazione che non è una proposta.
CREATE OR REPLACE FUNCTION before_insert_approvazione()
RETURNS TRIGGER AS $$
BEGIN
    IF (SELECT proposta FROM OPERAZIONE WHERE ID_Operazione=NEW.ID_Operazione)=FALSE THEN
        RAISE EXCEPTION 'Impossibile inserire un record in "APPROVAZIONE" il cui id_operazione faccia riferimento ad una tupla di OPERAZIONE con proposta=false';
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER check_approvazione
BEFORE INSERT
ON APPROVAZIONE
FOR EACH ROW --Da mettere?
EXECUTE FUNCTION before_insert_approvazione();

-- un autore di una pagina non può approvare proposte di operazioni su pagine non scritte da lui

/*
  ---------------------------------
    TRIGGER
  ---------------------------------
*/


CREATE OR REPLACE FUNCTION ordinamentoFraseInserimento() RETURNS TRIGGER AS
$$
DECLARE
    maxFrase INT;
BEGIN
    SELECT MAX(ordine) INTO maxFrase FROM FRASE WHERE ID_Pagina=NEW.ID_Pagina AND Riga=NEW.riga;
    
    IF(maxFrase IS NULL) THEN
        maxFrase := 0;
    END IF;
    
    IF(maxFrase IS NOT NULL AND maxFrase>=NEW.ordine) THEN
        
        FOR i IN REVERSE maxFrase..NEW.ordine LOOP
            
            UPDATE FRASE 
            SET ordine = ordine + 1
            WHERE ordine = i AND id_pagina = NEW.id_pagina AND Riga = NEW.riga;
            
            
        END LOOP;

    ELSIF (NEW.ordine IS NULL OR NEW.ordine > maxFrase) THEN
        NEW.ordine = maxFrase + 1;
    END IF;

    RETURN NEW;
END
$$ LANGUAGE plpgsql;

CREATE OR REPLACE TRIGGER ordinaFraseInserimento
BEFORE INSERT 
ON FRASE 
FOR EACH ROW
EXECUTE FUNCTION ordinamentoFraseInserimento();


CREATE OR REPLACE FUNCTION ordinamentoFraseCancellazione() RETURNS TRIGGER AS
$$
DECLARE
    maxFrase INT;
BEGIN
    SELECT MAX(ordine) INTO maxFrase FROM FRASE WHERE ID_Pagina=OLD.ID_Pagina AND Riga=OLD.riga;
    
    IF(maxFrase IS NOT NULL AND maxFrase>OLD.ordine) THEN
        
        FOR i IN OLD.ordine+1..maxFrase LOOP
            
            UPDATE FRASE 
            SET ordine = ordine - 1
            WHERE ordine = i AND id_pagina = OLD.id_pagina AND Riga = OLD.riga;
            
        END LOOP;
    END IF;

    RETURN NEW;
END
$$ LANGUAGE plpgsql;

CREATE OR REPLACE TRIGGER ordinaFraseCancellazione 
AFTER DELETE 
ON FRASE 
FOR EACH ROW
EXECUTE FUNCTION ordinamentoFraseCancellazione();




/*
  ---------------------------------
    PROCEDURE E FUNZIONI
  ---------------------------------
*/

CREATE OR REPLACE PROCEDURE inserimentoFrase(riga INT, ordine INT, ID_Pagina INT, Contenuto LEN_FRASE, collegamento BOOLEAN, nomeUtente USERNAME_DOMINIO, proposta BOOLEAN)
LANGUAGE plpgsql
AS $$
DECLARE
    
BEGIN
    IF(EXISTS(SELECT * FROM UTENTE WHERE Username=nomeUtente)=FALSE) THEN
        RAISE EXCEPTION 'utente indicato non esistente';
    END IF;

    INSERT INTO FRASE VALUES(Riga, Ordine, ID_Pagina, Contenuto, collegamento);
    
    INSERT INTO OPERAZIONE VALUES(DEFAULT, 'I', proposta, riga, ordine, Contenuto, null, DEFAULT, ID_Pagina, nomeUtente); 

    EXCEPTION
        WHEN OTHERS THEN
            RAISE EXCEPTION 'Si è verificata un''eccezione: %', SQLERRM;
END;
$$;

CREATE OR REPLACE PROCEDURE modificaFrase(rigaF INT, ordineF INT, ID_PaginaF INT, ContenutoF LEN_FRASE, nomeUtente USERNAME_DOMINIO, proposta BOOLEAN)
LANGUAGE plpgsql
AS $$
DECLARE
    oldFrase LEN_FRASE;
BEGIN
    IF(EXISTS(SELECT * FROM UTENTE WHERE Username=nomeUtente)=FALSE) THEN
        RAISE EXCEPTION 'utente indicato non esistente';
    END IF;

    SELECT Contenuto INTO oldFrase FROM FRASE WHERE riga=rigaF AND ordine = ordineF AND ID_Pagina = ID_PaginaF;

    UPDATE FRASE
    SET Contenuto = ContenutoF
    WHERE riga=rigaF AND ordine = ordineF AND ID_Pagina = ID_PaginaF;

    INSERT INTO OPERAZIONE VALUES(DEFAULT, 'M', proposta, rigaF, ordineF, oldFrase, ContenutoF, DEFAULT, ID_PaginaF, nomeUtente);  

    EXCEPTION
        WHEN OTHERS THEN
            RAISE EXCEPTION 'Si è verificata un''eccezione: %', SQLERRM;

    
END;
$$;

CREATE OR REPLACE PROCEDURE rimuoviFrase(rigaF INT, ordineF INT, ID_PaginaF INT, nomeUtente USERNAME_DOMINIO, proposta BOOLEAN)
LANGUAGE plpgsql
AS $$
DECLARE
    oldFrase LEN_FRASE;
BEGIN
    IF(EXISTS(SELECT * FROM UTENTE WHERE Username=nomeUtente)=FALSE) THEN
        RAISE EXCEPTION 'utente indicato non esistente';
    END IF;

    SELECT Contenuto INTO oldFrase FROM FRASE WHERE riga=rigaF AND ordine = ordineF AND ID_Pagina = ID_PaginaF;

    DELETE FROM FRASE
    WHERE riga=rigaF AND ordine = ordineF AND ID_Pagina = ID_PaginaF;
 
    INSERT INTO OPERAZIONE VALUES(DEFAULT, 'C', proposta, rigaF, ordineF,oldFrase, DEFAULT, ID_PaginaF, nomeUtente);  

    EXCEPTION
        WHEN OTHERS THEN
            RAISE EXCEPTION 'Si è verificata un''eccezione: %', SQLERRM;

    
END;
$$;








/*
  ---------------------------------
    !INSERT->TABLE->UTENTE!
  ---------------------------------
*/
INSERT INTO UTENTE VALUES
    ('john_doe', 'john.doe@gmail.com', 'P@ssw0rd123', default),
    ('jane_smith', 'jane.smith@yahoo.com', 'JaneSmith!456', default),
    ('alex_brown', 'alex.brown@hotmail.com', 'SecurePwd789@', default),
    ('lisa_johnson', 'lisa.johnson@outlook.com', 'LisaPass123@', default),
    ('mike_williams', 'mike.williams@aol.com', 'Williams123$', default),
    ('sarah_davis', 'sarah.davis@icloud.com', 'Sar@hDav!s678', default),
    ('chris_miller', 'chris.miller@protonmail.com', 'MillerPass456@', default),
    ('emily_taylor', 'emily.taylor@yandex.com', 'P@ssw0rdTaylor', default),
    ('ryan_anderson', 'ryan.anderson@zoho.com', 'AndersonRy@n789', default),
    ('amanda_thompson', 'amanda.thompson@mail.com', 'ThompsonPwd456@', default),
    ('david_clark', 'david.clark@inbox.com', 'D@v!dCl@rk123', default),
    ('samantha_wilson', 'samantha.wilson@tutanota.com', 'Wilson!Sam789', default),
    ('kevin_martin', 'kevin.martin@fastmail.com', 'M@rt!nK3v!n', default),
    ('jessica_taylor', 'jessica.taylor@yandex.com', 'TaylorJess123@', default),
    ('brandon_carter', 'brandon.carter@zoho.com', 'CarterBr@nd0n', default);



/*
  ---------------------------------
    !INSERT->TABLE->PAGINA!
  ---------------------------------
*/
INSERT INTO PAGINA VALUES
    (default, 'Storia dell''Impero Romano', 'Storia', '2024-02-08 09:00:00', 'john_doe'),
    (default, 'Teoria della relatività di Einstein', 'Fisica', '2024-02-07 10:30:00', 'jane_smith'),
    (default, 'Biografia di Leonardo da Vinci', 'Biografia', '2024-02-06 11:45:00', 'alex_brown'),
    (default, 'Economia degli Stati Uniti', 'Economia', '2024-02-05 12:15:00', 'lisa_johnson'),
    (default, 'Filosofia del Rinascimento', 'Filosofia', '2024-02-04 13:20:00', 'mike_williams'),
    (default, 'Scienza della computazione', 'Informatica', '2024-02-03 14:30:00', 'sarah_davis'),
    (default, 'Arte moderna: Movimento surrealista', 'Arte', '2024-02-02 15:45:00', 'chris_miller'),
    (default, 'Medicina alternativa e tradizionale', 'Medicina', '2024-02-01 16:50:00', 'emily_taylor'),
    (default, 'Biologia molecolare: DNA e genetica', 'Biologia', '2024-01-31 17:00:00', 'ryan_anderson'),
    (default, 'Storia dell''arte antica: Grecia classica', 'Storia dell''arte', '2024-01-30 18:15:00', 'amanda_thompson');

/*
  ---------------------------------
    !INSERT->TABLE->FRASE!
  ---------------------------------
*/
INSERT INTO FRASE VALUES;


/*
  ---------------------------------
    !INSERT->TABLE->COLLEGAMENTO!
  ---------------------------------
*/
INSERT INTO COLLEGAMENTO VALUES;


/*
  ---------------------------------
    !INSERT->TABLE->INSERIMENTO!
  ---------------------------------
*/
INSERT INTO INSERIMENTO VALUES;


/*
  ---------------------------------
    !INSERT->TABLE->MODIFICA !
  ---------------------------------
*/
INSERT INTO MODIFICA VALUES;


/*
  ---------------------------------
    !INSERT->TABLE->CANCELLAZIONE !
  ---------------------------------
*/
INSERT INTO CANCELLAZIONE VALUES;


/*
  ---------------------------------
    !INSERT->TABLE->APPROVAZIONE_INSERIMENTO!
  ---------------------------------
*/
INSERT INTO APPROVAZIONE_INSERIMENTO VALUES;


/*
  ---------------------------------
    !INSERT->TABLE->APPROVAZIONE_MODIFICA!
  ---------------------------------
*/
INSERT INTO APPROVAZIONE_MODIFICA VALUES;


/*
  ---------------------------------
    !INSERT->TABLE->APPROVAZIONE_CANCELLAZIONE!
  ---------------------------------
*/
INSERT INTO APPROVAZIONE_CANCELLAZIONE VALUES;




CREATE USER silvio_barra PASSWORD 'ProgettoOOBD@'; -- Creazione Nuovo Utente 
GRANT ALL ON ALL TABLES IN SCHEMA public TO Silvio_Barra; -- Asse




  
