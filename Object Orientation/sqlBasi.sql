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

CREATE DOMAIN LEN_FRASE AS VARCHAR(500);

        
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
    Autore BOOLEAN DEFAULT FALSE, 

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
    idTema SERIAL,
    Nome VARCHAR(50),

    PRIMARY KEY(idTema),
    UNIQUE(Nome)
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
    Tema SERIAL, 
    DataCreazione TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UserAutore USERNAME_DOMINIO NOT NULL,

    PRIMARY KEY(ID_Pagina),
    FOREIGN KEY(UserAutore) REFERENCES UTENTE(Username) 
    ON DELETE CASCADE
    ON UPDATE CASCADE,
    FOREIGN KEY(Tema) REFERENCES TEMA(idTema)
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

    PRIMARY KEY(RigaFrase, OrdineFrase, ID_Pagina),
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
    FraseCoinvolta LEN_FRASE NOT NULL,
    FraseModificata LEN_FRASE,
    Data TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ID_Pagina SERIAL NOT NULL,
    Utente USERNAME_DOMINIO,

    PRIMARY KEY(ID_Operazione),
    FOREIGN KEY(ID_Pagina) REFERENCES PAGINA(ID_Pagina) ON DELETE CASCADE,
    FOREIGN KEY(Utente) REFERENCES UTENTE(Username) ON DELETE SET NULL
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

    PRIMARY KEY(ID_Operazione),
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
ADD CONSTRAINT controlloIC CHECK(NOT((Tipo LIKE 'I' OR Tipo LIKE 'C') AND FraseModificata IS NOT NULL)); 

--non può esiste un tupla in approvazione che abbia data NOT NULL e risposta NULL o viceversa
ALTER TABLE APPROVAZIONE
ADD CONSTRAINT dataRisposta CHECK((data IS NULL AND risposta IS NULL) OR (data IS NOT NULL AND risposta IS NOT NULL));

-- non può esistere un operazione con proposta=true effettuata da un utente che è lo stesso autore della pagina. 
CREATE OR REPLACE FUNCTION check_proposta_function()
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
EXECUTE FUNCTION check_proposta_function();

-- non può esistere un approvazione riferita ad un'operazione che non è una proposta.
-- un autore di una pagina non può approvare proposte di operazioni su pagine non scritte da lui

CREATE OR REPLACE FUNCTION check_approvazione_function()
RETURNS TRIGGER AS $$
DECLARE 
    paginaInteressata INT;
    autorePagina USERNAME_DOMINIO;
BEGIN
    IF((SELECT proposta FROM OPERAZIONE WHERE ID_Operazione=NEW.ID_Operazione)=FALSE) THEN
        RAISE EXCEPTION 'Impossibile inserire un record in "APPROVAZIONE" il cui id_operazione faccia riferimento ad una tupla di OPERAZIONE con proposta=false';
    END IF;

    SELECT id_pagina INTO paginaInteressata FROM OPERAZIONE WHERE id_operazione = NEW.id_operazione;
    SELECT UserAutore INTO autorePagina FROM PAGINA WHERE id_pagina = paginaInteressata;

    IF(autorePagina <> NEW.autore) THEN
        RAISE EXCEPTION 'Impossibile inserire un record in "APPROVAZIONE" che fa riferimento ad un operazione di una pagina, il cui autore non è l''utente indicato';
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;


CREATE TRIGGER check_approvazione
BEFORE INSERT
ON APPROVAZIONE
FOR EACH ROW 
EXECUTE FUNCTION check_approvazione_function();




/*
  ---------------------------------
    TRIGGER E FUNZIONI ANNESSE 
  ---------------------------------
*/


/*
    TRIGGER E FUNZIONE: QUANDO UN UTENTE CREA UNA PAGINA
------------------------------------------------------------------------------------------------------------------------------
*/


CREATE OR REPLACE FUNCTION setAutore()
RETURNS TRIGGER AS $$
BEGIN
    UPDATE UTENTE
    SET autore = TRUE
    WHERE Username = NEW.UserAutore;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;


-- quando un utente crea una pagina il suo valore Autore diventa true
CREATE TRIGGER diventaAutore
AFTER INSERT ON PAGINA
FOR EACH ROW
EXECUTE FUNCTION setAutore();



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

            --UPDATE OPERAZIONE O
            --SET ordine = ordine + 1
            --FROM APPROVAZIONE A
            --WHERE O.id_operazione=A.id_operazione AND A.risposta IS NULL AND ordine = i AND id_pagina = NEW.id_pagina AND Riga = NEW.riga;
            
            
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
    TRIGGER E FUNZIONE: QUANDO VENGONO PROPOSTE OPERAZIONI IDENTICHE
------------------------------------------------------------------------------------------------------------------------------
*/

CREATE OR REPLACE FUNCTION sovrascizioneProposta_function() RETURNS TRIGGER AS
$$
DECLARE
    tupleRimosse INT;
BEGIN
    DELETE FROM OPERAZIONE O 
    USING APPROVAZIONE A
    WHERE O.id_operazione = A.id_operazione 
    AND O.id_operazione <> NEW.id_operazione
    AND O.proposta = true 
    AND O.id_pagina = NEW.id_pagina
    AND O.riga = NEW.riga
    AND O.ordine = NEW.ordine
    AND O.utente = NEW.utente
    AND A.risposta IS NULL;

    GET DIAGNOSTICS tupleRimosse = ROW_COUNT;

    IF(tupleRimosse<>0) THEN
        RAISE NOTICE 'proposta di operazione sovrascritta';
    END IF;

    RETURN NEW;
END
$$ LANGUAGE plpgsql;

CREATE OR REPLACE TRIGGER sovrascizioneProposta
AFTER INSERT 
ON OPERAZIONE 
FOR EACH ROW
WHEN (NEW.proposta = TRUE)
EXECUTE FUNCTION sovrascizioneProposta_function();

/*
    TRIGGER E FUNZIONE: QUANDO LE PROPOSTE NON ANCORA ACCETTATE NON SI RIFERISCONO PIù AD UN TESTO ESISTENTE
------------------------------------------------------------------------------------------------------------------------------
*/

CREATE OR REPLACE FUNCTION eliminaProposteAntiche_function1() RETURNS TRIGGER AS
$$
BEGIN
    
    UPDATE APPROVAZIONE AS A
    SET Risposta = false, data = CURRENT_TIMESTAMP
    FROM OPERAZIONE O
    WHERE A.id_operazione = O.id_operazione
    AND O.proposta = true 
    AND O.id_pagina = NEW.id_pagina
    AND O.riga = NEW.riga
    AND O.ordine = NEW.ordine
    AND A.risposta IS NULL;

	
	RETURN NEW;
END
$$ LANGUAGE plpgsql;


CREATE OR REPLACE FUNCTION eliminaProposteAntiche_function2() RETURNS TRIGGER AS
$$
DECLARE
    operazioneInteressata OPERAZIONE%ROWTYPE;
BEGIN
    SELECT * INTO operazioneInteressata FROM OPERAZIONE WHERE ID_Operazione = NEW.ID_Operazione;

    UPDATE APPROVAZIONE AS A
    SET Risposta = false, data = CURRENT_TIMESTAMP
    FROM OPERAZIONE O
    WHERE A.id_operazione = O.id_operazione
    AND O.proposta = true 
    AND O.id_pagina = operazioneInteressata.id_pagina
    AND O.riga = operazioneInteressata.riga
    AND O.ordine = operazioneInteressata.ordine
    AND A.risposta IS NULL;
    
	
	RETURN NEW;
END
$$ LANGUAGE plpgsql;


CREATE OR REPLACE TRIGGER eliminaProposteAntiche1
AFTER INSERT   
ON OPERAZIONE 
FOR EACH ROW
WHEN (NEW.proposta=FALSE)
EXECUTE FUNCTION eliminaProposteAntiche_function1();

CREATE OR REPLACE TRIGGER eliminaProposteAntiche2
AFTER UPDATE OF risposta ON APPROVAZIONE
FOR EACH ROW
WHEN(NEW.risposta = TRUE)
EXECUTE FUNCTION eliminaProposteAntiche_function2();




/*
    TRIGGER E FUNZIONE: QUANDO UNA PROPOSTA VIENE ACCETTATA
------------------------------------------------------------------------------------------------------------------------------
*/

--Quando una proposta viene accettata, viene chiamata la funzione 'effettuaProposta' che eseguirà l'operazione indicata
CREATE OR REPLACE FUNCTION effettuaProposta() RETURNS TRIGGER AS
$$
DECLARE
    operazioneProposta OPERAZIONE%ROWTYPE;
    stringaId VARCHAR(100);
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
            
            SELECT SUBSTRING(operazioneProposta.fraseModificata FROM 3) INTO stringaId;
            SELECT SUBSTRING(stringaId, 1, (SELECT strpos(stringaId, '#')-1)) INTO stringaId;
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


CREATE OR REPLACE FUNCTION setFalseLink() RETURNS TRIGGER AS
$$
BEGIN
    UPDATE FRASE
    SET collegamento=FALSE
    WHERE id_pagina = OLD.id_pagina AND riga= OLD.rigafrase AND ordine=OLD.ordinefrase;

    RETURN OLD;
END
$$ LANGUAGE plpgsql;

CREATE OR REPLACE TRIGGER setFalseLinkTrigger
AFTER DELETE 
ON COLLEGAMENTO 
FOR EACH ROW
EXECUTE FUNCTION setFalseLink();

/*
  ---------------------------------
    PROCEDURE
  ---------------------------------
*/

CREATE OR REPLACE PROCEDURE inserisciFrase(ID_PaginaF INT, rigaF INT, ordineF INT, ContenutoF LEN_FRASE, nomeUtente USERNAME_DOMINIO)
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
        ordineFrase := OrdineF;
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
    
    stringaCollegamento := '#+' || id_collegamento || '# ' || titoloCollegamento;
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
    
    stringaCollegamento := '#-# COLLEGAMENTO RIMOSSO';
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
BEGIN
    SELECT autore INTO autorePagina FROM APPROVAZIONE WHERE id_operazione = id;

    IF(autorePagina <> nomeUtente) THEN
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

CREATE OR REPLACE PROCEDURE ritiraProposta(id INT, nomeUtente USERNAME_DOMINIO)
LANGUAGE plpgsql
AS $$
DECLARE
    autorePagina USERNAME_DOMINIO;
    utenteProposta USERNAME_DOMINIO;
BEGIN
    SELECT autore INTO autorePagina FROM APPROVAZIONE WHERE id_operazione = id;
    SELECT utente INTO utenteProposta FROM OPERAZIONE WHERE id_operazione = id;

    IF(autorePagina = nomeUtente) THEN
        RAISE EXCEPTION 'Errore, l''utente indicato è l''autore della pagina';
    END IF;

    IF(utenteProposta <> nomeUtente) THEN
        RAISE EXCEPTION 'Errore, l''utente indicato non è colui che ha proposta l''operazione';
    END IF;

    IF(EXISTS(SELECT * FROM OPERAZIONE WHERE id_operazione = id) = false) THEN
        RAISE EXCEPTION 'Errore, l''operazione indicata non esiste';
    END IF;

    IF((SELECT proposta FROM OPERAZIONE WHERE id_operazione = id)=false) THEN
        RAISE EXCEPTION 'Errore, id indicato non è una proposta di un operazione';
    END IF;

    IF((SELECT Risposta FROM APPROVAZIONE WHERE id_operazione = id) IS NOT NULL) THEN
        RAISE EXCEPTION 'Errore, La proposta indicata ha già avuto risposta, non puoi ritirarla';
    END IF;

    DELETE FROM OPERAZIONE
    WHERE id_operazione = id AND utente = nomeUtente;
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
WHERE O.id_operazione = A.id_operazione 
AND A.Risposta=true)
ORDER BY id_pagina ASC, data ASC;

CREATE OR REPLACE VIEW listaproposte AS
SELECT O.*, A.data AS dataRisposta, A.risposta 
FROM OPERAZIONE O, APPROVAZIONE A
WHERE O.id_operazione = A.id_operazione;


/*
  ---------------------------------
    !INSERT->TABLE->UTENTE!
  ---------------------------------
*/
INSERT INTO UTENTE VALUES
    ('giovanni_rossi', 'giovanni.rossi@gmail.com', 'P@ssw0rd123', default),
    ('gianna_bianchi', 'gianna.bianchi@yahoo.it', 'GiannaBianchi!456', default),
    ('alessandro_verdi', 'alessandro.verdi@hotmail.it', 'SecurePwd789@', default),
    ('luisa_giorgi', 'luisa.giorgi@outlook.com', 'LuisaPass123@', default),
    ('michele_romano', 'michele.romano@libero.it', 'Romano123$', default),
    ('sara_ferrari', 'sara.ferrari@icloud.com', 'Sar@hFerr@r!678', default),
    ('cristina_rizzo', 'cristina.rizzo@gmail.com', 'RizzoPass456@', default),
    ('elena_fontana', 'elena.fontana@gmail.com', 'P@ssw0rdFontana', default),
    ('marco_casale', 'marco.casale@gmail.com', 'CasaleMar@co789', default),
    ('alessandra_colombo', 'alessandra.colombo@hotmail.it', 'ColomboPwd456@', default),
    ('davide_monti', 'davide.monti@gmail.com', 'D@v!dM0nt!123', default),
    ('simona_piazza', 'simona.piazza@gmail.com', 'PiazzaSim!789', default),
    ('enrico_martini', 'enrico.martini@gmail.com', 'M@rt!n!Enr1c0', default),
    ('giulia_russo', 'giulia.russo@gmai.com', 'RussoGiul!123@', default),
    ('fabio_marino', 'fabio.marino@gmail.com', 'MarinoF@b!0', default);


/*
  ---------------------------------
    !INSERT->TABLE->TEMA!
  ---------------------------------
*/

INSERT INTO TEMA VALUES
	(default, 'Enciclopedia generale'),
	(default, 'Cultura popolare'),
	(default, 'Scienze e tecnologie'),
	(default, 'Storia'),
	(default, 'Letteratura e arti visive'),
	(default, 'Cucina'),
	(default, 'Viaggi'),
	(default, 'Linguistica'),
	(default, 'Filosofia e pensiero'),
	(default, 'Ambiente e sostenibilità'),
	(default, 'Salute e benessere'),
	(default, 'Educazione'),
	(default, 'Diritto e giustizia'),
	(default, 'Economia e finanza'),
	(default, 'Politica e governance'),
	(default, 'Religione e spiritualità'),
	(default, 'Tecnologia dell''informazione');

/*
  ---------------------------------
    !INSERT->TABLE->PAGINA!
  ---------------------------------
*/
INSERT INTO PAGINA VALUES
	(default, 'Storia dell''Impero Romano', 4, '2024-02-08 09:00:00', 'giovanni_rossi'),
	(default, 'Teoria della relatività di Einstein', 3, '2024-02-07 10:30:00', 'sara_ferrari'),
	(default, 'Biografia di Leonardo da Vinci', 5, '2024-02-06 11:45:00', 'sara_ferrari'),
	(default, 'Economia degli Stati Uniti', 14, '2024-02-05 12:15:00', 'davide_monti'),
	(default, 'Filosofia del Rinascimento', 9, '2024-02-04 13:20:00', 'simona_piazza'),
	(default, 'Scienza della computazione', 17, '2024-02-03 14:30:00', 'simona_piazza'),
	(default, 'Arte moderna: Movimento surrealista', 5, '2024-02-02 15:45:00', 'fabio_marino'),
	(default, 'Medicina alternativa e tradizionale', 11, '2024-02-01 16:50:00', 'enrico_martini'),
	(default, 'Biologia molecolare: DNA e genetica', 3, '2024-01-31 17:00:00', 'elena_fontana'),
	(default, 'Storia dell''arte antica: Grecia classica', 5, '2024-01-30 18:15:00', 'luisa_giorgi');

/*
  ---------------------------------
        UTILIZZO PROCEDURE
  ---------------------------------
*/
-- Pagina 1
-- Inserimenti diretti da parte dell'autore
CALL inseriscifrase(1, 1, 1, 'L''Impero Romano è stato uno dei più influenti e duraturi nella storia dell''umanità.', 'giovanni_rossi');
CALL inseriscifrase(1, 1, 2, 'La politica, l''architettura e il diritto romani hanno lasciato un''impronta duratura sulla civiltà occidentale.', 'giovanni_rossi');
CALL inseriscifrase(1, 1, 3, 'La Pax Romana ha portato stabilità e prosperità a molte regioni sotto il dominio romano.', 'giovanni_rossi');

-- Proposte
CALL inseriscifrase(1, 2, 1, 'L''Impero Romano ha lasciato un''eredità culturale e politica che ancora influenza il mondo moderno.', 'sara_ferrari');
CALL inseriscifrase(1, 2, 2, 'Le conquiste romane hanno portato alla diffusione della lingua latina e dei valori romani in gran parte dell''Europa.', 'sara_ferrari');
CALL inseriscifrase(1, 2, 3, 'L''Impero Romano è stato un esempio di ingegneria militare e civile senza precedenti nella storia antica.', 'sara_ferrari');


-- Pagina 2
-- Inserimenti diretti da parte dell'autore
CALL inseriscifrase(2, 1, 1, 'E=mc²', 'sara_ferrari');
CALL inseriscifrase(2, 1, 2, 'Spiegazione della gravità come curvatura dello spazio-tempo.', 'sara_ferrari');
CALL inseriscifrase(2, 1, 3, 'Rivoluzionaria teoria sulla natura dello spazio e del tempo.', 'sara_ferrari');

-- Proposte
CALL inseriscifrase(2, 2, 1, 'Curvatura dello spazio-tempo', 'davide_monti');
CALL inseriscifrase(2, 2, 2, 'Luce come entità soggetta alla gravità', 'davide_monti');
CALL inseriscifrase(2, 2, 3, 'Teoria che unifica spazio, tempo e gravità', 'davide_monti');


-- Pagina 3
-- Inserimenti diretti da parte dell'autore
CALL inseriscifrase(3, 1, 1, 'Polimatia rinascimentale', 'sara_ferrari');
CALL inseriscifrase(3, 1, 2, 'Mente geniale del Rinascimento', 'sara_ferrari');
CALL inseriscifrase(3, 1, 3, 'Artista, scienziato e inventore', 'sara_ferrari');

-- Proposte
CALL inseriscifrase(3, 2, 1, 'Genio del Rinascimento', 'elena_fontana');
CALL inseriscifrase(3, 2, 2, 'Studio della natura e dell''anatomia', 'elena_fontana');
CALL inseriscifrase(3, 2, 3, 'Opere celebri come la "Gioconda"', 'elena_fontana');


-- Approva proposte
CALL approvaproposta(4, true, 'giovanni_rossi');
CALL approvaproposta(5, true, 'giovanni_rossi');
CALL approvaproposta(6, true, 'giovanni_rossi');

CALL approvaproposta(10, false, 'sara_ferrari');
CALL approvaproposta(11, false, 'sara_ferrari');
CALL approvaproposta(12, false, 'sara_ferrari');

-- Ritira proposte
CALL ritiraproposta(16, 'elena_fontana');
CALL ritiraproposta(17, 'elena_fontana');
CALL ritiraproposta(18, 'elena_fontana');

-- Inserisci collegamenti
CALL inseriscicollegamento(3, 1, 2, 1, 'sara_ferrari');
CALL inseriscicollegamento(3, 1, 3, 1, 'sara_ferrari');

-- Rimuovi collegamenti (proposta ed approvazione)
CALL rimuovicollegamento(3, 1, 3, 'giovanni_rossi');
CALL approvaproposta(21, true, 'sara_ferrari');

-- Modifica frase (proposta e rifiuto)
CALL modificaFrase(1, 1, 1, 'Inventore di macchine e dispositivi innovativi', 'sara_ferrari');
CALL approvaproposta(22, false, 'giovanni_rossi');

-- Rimuovi frase (proposta e rifiuto)
CALL rimuoviFrase(1, 1, 1, 'sara_ferrari');
CALL approvaproposta(23, false, 'giovanni_rossi');





CREATE USER silvio_barra PASSWORD 'ProgettoOOBD@'; -- Creazione Nuovo Utente 
GRANT ALL ON ALL TABLES IN SCHEMA public TO Silvio_Barra; -- Asse




  
