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
    DataIscrizione TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
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
    ON UPDATE CASCADE,
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
FOR EACH ROW 
EXECUTE FUNCTION before_insert_approvazione();

-- un autore di una pagina non può approvare proposte di operazioni su pagine non scritte da lui

/*
  ---------------------------------
    TRIGGER
  ---------------------------------
*/
-- quando un utente crea una pagina il suo valore Autore diventa true
CREATE TRIGGER 
/*
    TRIGGER E FUNZIONE: GESTIONE CAMPO ORDINE (INSERIMENTO)
------------------------------------------------------------------------------------------------------------------------------
*/



--ordinamento del campo 'ordine' nel momento in cui avviene un inserimento in frase.
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

/*
    TRIGGER E FUNZIONE: GESTIONE CAMPO ORDINE (RIMOZIONE)
------------------------------------------------------------------------------------------------------------------------------
*/

--ordinamento del campo 'ordine' nel momento in cui avviene una rimozione in frase.
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
    TRIGGER E FUNZIONE: QUANDO VIENE INSERITA UNA TUPLA OPERAZIONE CON PROPOSTA=TRUE
------------------------------------------------------------------------------------------------------------------------------
*/

--nel momento in cui inseriamo una tupla in operazione che ha il campo proposta = true, viene inserita una tupla a cui fa riferimento in 'Approvazione'
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

/*
    TRIGGER E FUNZIONE: QUANDO UNA PROPOSTA VIENE ACCETTATA
------------------------------------------------------------------------------------------------------------------------------
*/

--Quando una proposta viene accettata, viene chiamata la funzione 'effettuaProposta' che eseguirà l'operazione indicata
CREATE OR REPLACE FUNCTION effettuaProposta() RETURNS TRIGGER AS
$$
DECLARE
    operazioneProposta OPERAZIONE%ROWTYPE;
    stringaId VARCHAR(50);
    id_collegamento INT;
    fraseConsiderata FRASE%ROWTYPE;
BEGIN
    SELECT * INTO operazioneProposta FROM OPERAZIONE WHERE ID_Operazione = NEW.ID_Operazione;

    --controllare se la frase da modificare o rimuovere esiste ancora
    SELECT * INTO fraseConsiderata FROM FRASE WHERE id_pagina=operazioneProposta.id_pagina AND riga = operazioneProposta.riga AND ordine = operazioneProposta.ordine;

    IF(operazioneProposta.tipo LIKE 'I') THEN

        INSERT INTO FRASE VALUES (operazioneProposta.Riga, operazioneProposta.ordine, operazioneProposta.ID_Pagina, operazioneProposta.FraseCoinvolta, false);

    ELSIF (operazioneProposta.tipo LIKE 'M') THEN

        IF(fraseConsiderata IS NULL) THEN
            RAISE EXCEPTION 'la frase coinvolta nel operazione non esiste più';
        END IF;

        IF(operazioneProposta.fraseModificata LIKE '#+%') THEN --se è stato aggiunto un collegamento
            
            SELECT regexp_replace(operazioneProposta.fraseModificata, '\D', '', 'g') INTO stringaId; --cancella qualsiasi carattere che non è una cifra
            SELECT stringaId::INT INTO id_collegamento; -- conversione da VARCHAR a INT
            
            -- controllo se esiste già il collegamento
            IF(EXISTS(SELECT * FROM COLLEGAMENTO WHERE id_pagina = operazioneProposta.ID_Pagina AND rigaFrase=operazioneProposta.riga AND ordineFrase=operazioneProposta.ordine)=FALSE) THEN
                INSERT INTO COLLEGAMENTO VALUES(operazioneProposta.riga, operazioneProposta.ordine, operazioneProposta.id_pagina, id_collegamento);

                UPDATE FRASE
                SET Collegamento = true
                WHERE riga= operazioneProposta.riga AND ordine = operazioneProposta.ordine AND ID_Pagina = operazioneProposta.id_pagina;
            ELSE
                UPDATE COLLEGAMENTO
                SET ID_PaginaCollegata = id_collegamento
                WHERE id_pagina = operazioneProposta.ID_Pagina AND rigaFrase=operazioneProposta.riga AND ordineFrase=operazioneProposta.ordine;
            END IF;
        ELSIF(operazioneProposta.fraseModificata LIKE '#-%') THEN --se è stato rimosso un collegamento
            
            DELETE FROM COLLEGAMENTO
            WHERE id_pagina = operazioneProposta.ID_Pagina AND rigaFrase=operazioneProposta.riga AND ordineFrase=operazioneProposta.ordine;
            
            UPDATE FRASE
            SET Collegamento = false
            WHERE riga= operazioneProposta.riga AND ordine = operazioneProposta.ordine AND ID_Pagina = operazioneProposta.id_pagina;

        ELSE --è stato modificato il contenuto della frase
            UPDATE FRASE
            SET Contenuto = operazioneProposta.FraseModificata
            WHERE riga= operazioneProposta.riga AND ordine = operazioneProposta.ordine AND ID_Pagina = operazioneProposta.id_pagina;
        END IF;

    ELSIF (operazioneProposta.tipo LIKE 'C') THEN

        IF(fraseConsiderata IS NULL) THEN
            RAISE EXCEPTION 'la frase coinvolta nel operazione non esiste più';
        END IF;

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

CREATE OR REPLACE PROCEDURE inserimentoFrase(ID_PaginaF INT, rigaF INT, ordineF INT, ContenutoF LEN_FRASE, nomeUtente USERNAME_DOMINIO)
LANGUAGE plpgsql
AS $$
DECLARE
    maxOrdine INT;
    ordineFrase INT;
    autorePagina USERNAME_DOMINIO;
    proposta BOOLEAN;
BEGIN
    -- controllo se esiste l'utente
    IF(EXISTS(SELECT * FROM UTENTE WHERE Username=nomeUtente)=FALSE) THEN
        RAISE EXCEPTION 'utente indicato non esistente';
    END IF;

    -- controllo se esiste la pagina
    IF(EXISTS(SELECT * FROM PAGINA WHERE ID_Pagina=ID_PaginaF)=FALSE) THEN
        RAISE EXCEPTION 'pagina indicata non esistente';
    END IF;

    SELECT UserAutore INTO autorePagina FROM PAGINA WHERE id_pagina = ID_PaginaF;
    
    -- prendo l'ordine massimo sulla riga coinvolta
    SELECT MAX(ordine) INTO maxOrdine FROM FRASE WHERE ID_Pagina = ID_PaginaF AND riga = rigaF;

    -- se si tratta della prima frase sulla riga
	IF(maxOrdine IS NULL) THEN
		maxOrdine:=0;
	END IF;
    
    /* se l'utente specifica l'ordine della frase che sta inserendo e quest'ultimo si trova nel mezzo della 
    riga (ordineF < maxOrdine) allora l'ordine della frase è quello indicato, altrimenti l'ordine sarà il massimo +1 */
    IF(ordineF IS NOT NULL AND maxOrdine>ordineF) THEN
        ordineFrase := OrdineF;
    ELSE
        ordineFrase := maxOrdine+1;
    END IF;
    
	-- controlla se l'utente è l'autore stesso
    IF(autorePagina = nomeUtente) THEN
        
        proposta:=false;    -- inserimento diretto (da parte dell'autore stesso)

        INSERT INTO FRASE VALUES(RigaF, ordineFrase, ID_PaginaF, ContenutoF, false);

    ELSE
        proposta:=true; -- altrimenti l'operazione è solo di proposta
    END IF;

    INSERT INTO OPERAZIONE VALUES(DEFAULT, 'I', proposta, rigaF, ordineFrase, ContenutoF, null, DEFAULT, ID_PaginaF, nomeUtente); 
   
END;
$$;

CREATE OR REPLACE PROCEDURE inserisciCollegamento(ID_PaginaF INT, rigaF INT, ordineF INT, id_collegamento INT, nomeUtente USERNAME_DOMINIO)
LANGUAGE plpgsql
AS $$
DECLARE
    stringaFrase LEN_FRASE;
    titoloCollegamento VARCHAR(100);
	stringaCollegamento LEN_FRASE;
    autorePagina USERNAME_DOMINIO;
    proposta BOOLEAN;
BEGIN
    -- controllo se esiste l'utente
    IF(EXISTS(SELECT * FROM UTENTE WHERE Username=nomeUtente)=FALSE) THEN
        RAISE EXCEPTION 'utente indicato non esistente';
    END IF;

    -- controllo se esiste la pagina
    IF(EXISTS(SELECT * FROM PAGINA WHERE ID_Pagina=ID_PaginaF)=FALSE) THEN
        RAISE EXCEPTION 'pagina indicata non esistente';
    END IF;

    -- controllo se esiste la frase (e di conseguenza la pagina) 
    SELECT Contenuto INTO stringaFrase FROM FRASE WHERE riga = rigaF AND ordine=ordineF AND ID_Pagina = ID_PaginaF;

    IF(stringaFrase IS NULL) THEN
        RAISE EXCEPTION 'la frase indicata non esiste';
    END IF;

    SELECT titolo INTO titoloCollegamento FROM PAGINA WHERE id_pagina = id_collegamento;

    IF(titoloCollegamento IS NULL) THEN
        RAISE EXCEPTION 'La pagina indicata per il collegamento non esiste';
    END IF;

    SELECT UserAutore INTO autorePagina FROM PAGINA WHERE id_pagina = ID_PaginaF;

    IF(autorePagina = nomeUtente) THEN
        proposta := false;
        --controlliamo se esiste già una tupla in collegamento, altrimenti la creiamo
        IF(EXISTS(SELECT * FROM COLLEGAMENTO WHERE rigaFrase = rigaF AND ordineFrase=ordineF AND ID_Pagina = ID_PaginaF)=FALSE) THEN --se non esiste
            INSERT INTO COLLEGAMENTO VALUES (rigaF, ordineF, ID_PaginaF, id_collegamento);

            UPDATE FRASE
            SET collegamento = true
            WHERE riga = rigaF AND ordine=ordineF AND ID_Pagina = ID_PaginaF;
        ELSE
            UPDATE COLLEGAMENTO
            SET ID_PaginaCollegata = id_collegamento
            WHERE rigaFrase = rigaF AND ordineFrase=ordineF AND ID_Pagina = ID_PaginaF;
        END IF;
    ELSE 
        proposta := true;
    END IF;
    
    stringaCollegamento := '#+' || id_collegamento || ' ' || titoloCollegamento;
    INSERT INTO OPERAZIONE VALUES(DEFAULT, 'M', proposta, rigaF, ordineF, stringaFrase, stringaCollegamento, DEFAULT, ID_PaginaF, nomeUtente); 

END;
$$;


CREATE OR REPLACE PROCEDURE rimuoviCollegamento(ID_PaginaF INT, rigaF INT, ordineF INT, nomeUtente USERNAME_DOMINIO)
LANGUAGE plpgsql
AS $$
DECLARE
    stringaFrase LEN_FRASE;
    titoloCollegamento VARCHAR(100);
	stringaCollegamento LEN_FRASE;
    autorePagina USERNAME_DOMINIO;
    proposta BOOLEAN;
BEGIN
    -- controllo se esiste l'utente
    IF(EXISTS(SELECT * FROM UTENTE WHERE Username=nomeUtente)=FALSE) THEN
        RAISE EXCEPTION 'utente indicato non esistente';
    END IF;

    -- controllo se esiste la pagina
    IF(EXISTS(SELECT * FROM PAGINA WHERE ID_Pagina=ID_PaginaF)=FALSE) THEN
        RAISE EXCEPTION 'pagina indicata non esistente';
    END IF;

    -- controllo se esiste la frase (e di conseguenza la pagina) 
    SELECT Contenuto INTO stringaFrase FROM FRASE WHERE riga = rigaF AND ordine=ordineF AND ID_Pagina = ID_PaginaF;

    IF(stringaFrase IS NULL) THEN
        RAISE EXCEPTION 'la frase indicata non esiste';
    END IF;

    IF(EXISTS(SELECT * FROM COLLEGAMENTO WHERE rigaFrase = rigaF AND ordineFrase=ordineF AND ID_Pagina = ID_PaginaF)=FALSE) THEN
        RAISE EXCEPTION 'la frase non possiede collegamento';
    END IF;


    SELECT UserAutore INTO autorePagina FROM PAGINA WHERE id_pagina = ID_PaginaF;

    IF(autorePagina = nomeUtente) THEN
        proposta := false;
        
        DELETE FROM COLLEGAMENTO
        WHERE rigaFrase = rigaF AND ordineFrase=ordineF AND ID_Pagina = ID_PaginaF;

        UPDATE FRASE
        SET collegamento = false
        WHERE riga = rigaF AND ordine=ordineF AND ID_Pagina = ID_PaginaF;
 
    ELSE 
        proposta := true;
    END IF;
    
    stringaCollegamento := '#- COLLEGAMENTO RIMOSSO';
    INSERT INTO OPERAZIONE VALUES(DEFAULT, 'M', proposta, rigaF, ordineF, stringaFrase, stringaCollegamento, DEFAULT, ID_PaginaF, nomeUtente); 

END;
$$;


CREATE OR REPLACE PROCEDURE modificaFrase(ID_PaginaF INT, rigaF INT, ordineF INT, ContenutoF LEN_FRASE, nomeUtente USERNAME_DOMINIO)
LANGUAGE plpgsql
AS $$
DECLARE
    oldFrase LEN_FRASE;
    autorePagina USERNAME_DOMINIO;
    proposta BOOLEAN;
BEGIN
    -- controllo se esiste l'utente
    IF(EXISTS(SELECT * FROM UTENTE WHERE Username=nomeUtente)=FALSE) THEN
        RAISE EXCEPTION 'utente indicato non esistente';
    END IF;

    -- controllo se esiste la pagina
    IF(EXISTS(SELECT * FROM PAGINA WHERE ID_Pagina=ID_PaginaF)=FALSE) THEN
        RAISE EXCEPTION 'pagina indicata non esistente';
    END IF;

    -- controllo se esiste la frase (e di conseguenza la pagina) 
    IF(EXISTS(SELECT * FROM FRASE WHERE riga = rigaF AND ordine=ordineF AND ID_Pagina = ID_PaginaF)=FALSE) THEN
        RAISE EXCEPTION 'la frase indicata non esiste';
    END IF;

    SELECT UserAutore INTO autorePagina FROM PAGINA WHERE id_pagina = ID_PaginaF;

    SELECT Contenuto INTO oldFrase FROM FRASE WHERE riga=rigaF AND ordine = ordineF AND ID_Pagina = ID_PaginaF;

    IF(autorePagina = nomeUtente) THEN

        proposta := false;

        UPDATE FRASE
        SET Contenuto = ContenutoF
        WHERE riga=rigaF AND ordine = ordineF AND ID_Pagina = ID_PaginaF;        
    ELSE
        proposta := true;
    END IF;

    INSERT INTO OPERAZIONE VALUES(DEFAULT, 'M', proposta, rigaF, ordineF, oldFrase, ContenutoF, DEFAULT, ID_PaginaF, nomeUtente);  
    
END;
$$;

CREATE OR REPLACE PROCEDURE rimuoviFrase(ID_PaginaF INT, rigaF INT, ordineF INT, nomeUtente USERNAME_DOMINIO)
LANGUAGE plpgsql
AS $$
DECLARE
    oldFrase LEN_FRASE;
    autorePagina USERNAME_DOMINIO;
    proposta BOOLEAN;
BEGIN
    -- controllo se esiste l'utente
    IF(EXISTS(SELECT * FROM UTENTE WHERE Username=nomeUtente)=FALSE) THEN
        RAISE EXCEPTION 'utente indicato non esistente';
    END IF;

    -- controllo se esiste la pagina
    IF(EXISTS(SELECT * FROM PAGINA WHERE ID_Pagina=ID_PaginaF)=FALSE) THEN
        RAISE EXCEPTION 'pagina indicata non esistente';
    END IF;

    -- controllo se esiste la frase (e di conseguenza la pagina) 
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

CREATE OR REPLACE PROCEDURE approvaProposta(id INT, risp BOOLEAN, nomeUtente USERNAME_DOMINIO)
LANGUAGE plpgsql
AS $$
DECLARE
    autorePagina USERNAME_DOMINIO;
    paginaInteressata INT;
BEGIN
    SELECT ID_Pagina INTO paginaInteressata FROM OPERAZIONE WHERE id_operazione = id;
    SELECT UserAutore INTO autorePagina FROM PAGINA WHERE id_pagina = paginaInteressata;

    IF(UserAutore <> nomeUtente) THEN
        RAISE EXCEPTION 'Errore, l''utente indicato non è l''autore della pagina';
    END IF;

    IF(EXISTS(SELECT * FROM OPERAZIONE WHERE id_operazione = id) = false) THEN
        RAISE EXCEPTION 'Errore, l''operazione indicata non esiste';
    END IF;

    IF((SELECT proposta FROM OPERAZIONE WHERE id_operazione = id)=false) THEN
        RAISE EXCEPTION 'Errore, id indicato non è una proposta di un operazione';
    END IF;

    IF((SELECT Risposta FROM APPROVAZIONE WHERE id_operazione = id) IS NOT NULL) THEN
        RAISE EXCEPTION 'Errore, La proposta indicata ha già avuto risposta';
    END IF;

    UPDATE APPROVAZIONE 
    SET Risposta = risp, data = CURRENT_TIMESTAMP
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
FROM OPERAZIONE O NATURAL JOIN APPROVAZIONE A
WHERE A.Risposta=true)
ORDER BY id_pagina ASC, data DESC;


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
    !INSERT->TABLE->TEMA!
  ---------------------------------
*/

INSERT INTO tema (nome) VALUES
	('Enciclopedia generale'),
	('Cultura popolare'),
	('Scienze e tecnologie'),
	('Storia'),
	('Letteratura e arti visive'),
	('Cucina'),
	('Viaggi'),
	('Linguistica'),
	('Filosofia e pensiero'),
	('Ambiente e sostenibilità'),
	('Salute e benessere'),
	('Educazione'),
	('Diritto e giustizia'),
	('Economia e finanza'),
	('Politica e governance'),
	('Religione e spiritualità'),
	('Tecnologia dell''informazione');

/*
  ---------------------------------
    !INSERT->TABLE->PAGINA!
  ---------------------------------
*/
INSERT INTO PAGINA VALUES
	(default, 'Storia dell''Impero Romano', 'Storia', '2024-02-08 09:00:00', 'john_doe'),
	(default, 'Teoria della relatività di Einstein', 'Scienze e tecnologie', '2024-02-07 10:30:00', 'jane_smith'),
	(default, 'Biografia di Leonardo da Vinci', 'Letteratura e arti visive', '2024-02-06 11:45:00', 'alex_brown'),
	(default, 'Economia degli Stati Uniti', 'Economia e finanza', '2024-02-05 12:15:00', 'lisa_johnson'),
	(default, 'Filosofia del Rinascimento', 'Filosofia e pensiero', '2024-02-04 13:20:00', 'mike_williams'),
	(default, 'Scienza della computazione', 'Tecnologia dell''informazione', '2024-02-03 14:30:00', 'sarah_davis'),
	(default, 'Arte moderna: Movimento surrealista', 'Letteratura e arti visive', '2024-02-02 15:45:00', 'chris_miller'),
	(default, 'Medicina alternativa e tradizionale', 'Salute e benessere', '2024-02-01 16:50:00', 'emily_taylor'),
	(default, 'Biologia molecolare: DNA e genetica', 'Scienze e tecnologie', '2024-01-31 17:00:00', 'ryan_anderson'),
	(default, 'Storia dell''arte antica: Grecia classica', 'Letteratura e arti visive', '2024-01-30 18:15:00', 'amanda_thompson');

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




  
