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
        !Table-TEMA!
    ---------------------------
*/

CREATE TABLE TEMA
(
    nome VARCHAR(50),

    PRIMARY KEY(nome)
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
    Tema VARCHAR(50), 
    DataCreazione TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UserAutore USERNAME_DOMINIO NOT NULL,

    PRIMARY KEY(ID_Pagina),
    FOREIGN KEY(UserAutore) REFERENCES UTENTE(Username) 
    ON DELETE CASCADE
    ON UPDATE CASCADE
    FOREIGN KEY(Tema) REFERENCES TEMA(nome)
    ON DELETE SET NULL
    ON UPDATE CASCADE
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
    Collegamento BOOLEAN DEFAULT FALSE,

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
    Utente USERNAME_DOMINIO DEFAULT 'Unknown', 

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


CREATE OR REPLACE FUNCTION creazioneApprovazione() RETURNS TRIGGER AS
$$
DECLARE
    autore USERNAME_DOMINIO;
BEGIN
    SELECT UserAutore INTO Autore FROM PAGINA WHERE ID_Pagina = NEW.id_pagina;

    INSERT INTO APPROVAZIONE VALUES (NEW.ID_Operazione, autore, null, null);

    RETURN NEW;
END
$$ LANGUAGE plpgsql;



CREATE OR REPLACE TRIGGER creazioneApprovazioneTrigger 
AFTER INSERT 
ON OPERAZIONE 
FOR EACH ROW
WHEN (NEW.proposta = TRUE)
EXECUTE FUNCTION creazioneApprovazione();


CREATE OR REPLACE FUNCTION effettuaProposta() RETURNS TRIGGER AS
$$
DECLARE
    operazioneProposta OPERAZIONE%ROWTYPE;
BEGIN
    SELECT * INTO operazioneProposta FROM OPERAZIONE WHERE ID_Operazione = NEW.ID_Operazione;

    IF(operazioneProposta.tipo LIKE 'I') THEN
        INSERT INTO FRASE VALUES (operazioneProposta.Riga, operazioneProposta.ordine, operazioneProposta.ID_Pagina, operazioneProposta.FraseCoinvolta, false);
    ELSIF (operazioneProposta.tipo LIKE 'M') THEN
        UPDATE FRASE
        SET Contenuto = operazioneProposta.FraseModificata
        WHERE riga= operazioneProposta.riga AND ordine = operazioneProposta.ordine AND ID_Pagina = operazioneProposta.id_pagina;
    ELSIF (operazioneProposta.tipo LIKE 'C') THEN
        DELETE FROM FRASE
        WHERE riga= operazioneProposta.riga AND ordine = operazioneProposta.ordine AND ID_Pagina = operazioneProposta.id_pagina;
    END IF;

    RETURN NEW;
END
$$ LANGUAGE plpgsql;


CREATE OR REPLACE TRIGGER controlloEsitoApprovazioneTrigger
AFTER UPDATE 
OF Risposta ON APPROVAZIONE 
FOR EACH ROW
WHEN (NEW.Risposta = TRUE)
EXECUTE FUNCTION effettuaProposta();




/*
  ---------------------------------
    PROCEDURE E FUNZIONI
  ---------------------------------
*/

CREATE OR REPLACE PROCEDURE inserimentoFrase(rigaF INT, ordineF INT, ID_PaginaF INT, ContenutoF LEN_FRASE, collegamento BOOLEAN, nomeUtente USERNAME_DOMINIO)
LANGUAGE plpgsql
AS $$
DECLARE
    maxFrase INT;
    ordineFrase INT;
    autorePagina USERNAME_DOMINIO;
    proposta BOOLEAN;
BEGIN
    IF(EXISTS(SELECT * FROM UTENTE WHERE Username=nomeUtente)=FALSE) THEN
        RAISE EXCEPTION 'utente indicato non esistente';
    END IF;

    SELECT UserAutore INTO autorePagina FROM PAGINA WHERE id_pagina = ID_PaginaF;
    
    SELECT MAX(ordine) INTO maxFrase FROM FRASE WHERE ID_Pagina = ID_PaginaF AND riga = rigaF;
	
	  IF(maxFrase IS NULL) THEN
		  maxFrase:=0;
	  END IF;
	
    IF(ordineF IS NOT NULL AND maxFrase>ordineF) THEN
        ordineFrase := OrdineF;
    ELSE
        ordineFrase := maxFrase+1;
    END IF;
	
	
    IF(autorePagina = nomeUtente) THEN
      INSERT INTO FRASE VALUES(RigaF, ordineFrase, ID_PaginaF, ContenutoF, collegamento);
      proposta:=false;
    ELSE
      proposta:=true;
    END IF;

    INSERT INTO OPERAZIONE VALUES(DEFAULT, 'I', proposta, rigaF, ordineFrase, ContenutoF, null, DEFAULT, ID_PaginaF, nomeUtente); 
   
END;
$$;

CREATE OR REPLACE PROCEDURE modificaFrase(rigaF INT, ordineF INT, ID_PaginaF INT, ContenutoF LEN_FRASE, nomeUtente USERNAME_DOMINIO)
LANGUAGE plpgsql
AS $$
DECLARE
    oldFrase LEN_FRASE;
    autorePagina USERNAME_DOMINIO;
    proposta BOOLEAN;
BEGIN
    IF(EXISTS(SELECT * FROM UTENTE WHERE Username=nomeUtente)=FALSE) THEN
        RAISE EXCEPTION 'utente indicato non esistente';
    END IF;

    IF(EXISTS(SELECT * FROM FRASE WHERE riga = rigaF AND ordine=ordineF AND ID_Pagina = ID_PaginaF)=FALSE) THEN
        RAISE EXCEPTION 'la frase indicata non esiste';
    END IF;

    SELECT UserAutore INTO autorePagina FROM PAGINA WHERE id_pagina = ID_PaginaF;

    SELECT Contenuto INTO oldFrase FROM FRASE WHERE riga=rigaF AND ordine = ordineF AND ID_Pagina = ID_PaginaF;

    IF(autorePagina = nomeUtente) THEN
        UPDATE FRASE
        SET Contenuto = ContenutoF
        WHERE riga=rigaF AND ordine = ordineF AND ID_Pagina = ID_PaginaF;
        proposta := false;
    ELSE
        proposta := true;
    END IF;

    INSERT INTO OPERAZIONE VALUES(DEFAULT, 'M', proposta, rigaF, ordineF, oldFrase, ContenutoF, DEFAULT, ID_PaginaF, nomeUtente);  



    
END;
$$;

CREATE OR REPLACE PROCEDURE rimuoviFrase(rigaF INT, ordineF INT, ID_PaginaF INT, nomeUtente USERNAME_DOMINIO)
LANGUAGE plpgsql
AS $$
DECLARE
    oldFrase LEN_FRASE;
    autorePagina USERNAME_DOMINIO;
    proposta BOOLEAN;
BEGIN
    IF(EXISTS(SELECT * FROM UTENTE WHERE Username=nomeUtente)=FALSE) THEN
        RAISE EXCEPTION 'utente indicato non esistente';
    END IF;

    IF(EXISTS(SELECT * FROM FRASE WHERE riga = rigaF AND ordine=ordineF AND ID_Pagina = ID_PaginaF)=FALSE) THEN
        RAISE EXCEPTION 'la frase indicata non esiste';
    END IF;

    SELECT Contenuto INTO oldFrase FROM FRASE WHERE riga=rigaF AND ordine = ordineF AND ID_Pagina = ID_PaginaF;

    SELECT UserAutore INTO autorePagina FROM PAGINA WHERE id_pagina = ID_PaginaF;

    IF(autorePagina = nomeUtente) THEN
        DELETE FROM FRASE
        WHERE riga=rigaF AND ordine = ordineF AND ID_Pagina = ID_PaginaF;
        proposta := false;
    ELSE
        proposta := true;
    END IF;
 
    INSERT INTO OPERAZIONE VALUES(DEFAULT, 'C', proposta, rigaF, ordineF, oldFrase, null, DEFAULT, ID_PaginaF, nomeUtente);  

END;
$$;

CREATE OR REPLACE PROCEDURE approvaProposta(id INT, risp BOOLEAN)
LANGUAGE plpgsql
AS $$
DECLARE
    oldFrase LEN_FRASE;
BEGIN
    UPDATE APPROVAZIONE 
    SET Risposta = risp
    WHERE id_operazione = id;
END;
$$;


/*
  ---------------------------------
    VISTE
  ---------------------------------
*/


CREATE OR REPLACE VIEW storicoPagine AS 
(SELECT *
FROM OPERAZIONE 
WHERE proposta=false)
UNION 
(SELECT O.*
FROM OPERAZIONE O, APPROVAZIONE A
WHERE O.id_operazione = A.ID_Operazione AND A.Risposta=true)
ORDER BY id_pagina;








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




  
