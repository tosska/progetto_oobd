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
CREATE DOMAIN USERNAME_DOMINIO AS VARCHAR(20); -- da fare

-- Vincolo Di Dominio: Email
CREATE DOMAIN EMAIL_DOMINIO AS VARCHAR(50)
  CHECK (VALUE LIKE '%_@%_.__%');

-- Vincolo Di Dominio: Tipo di operazione: I: Inserimento, M: Modifica, C: Cancellazione
CREATE DOMAIN TIPO_OPERAZIONE AS CHAR(1)
  CHECK (VALUE LIKE 'I' OR VALUE LIKE 'M' OR VALUE LIKE 'C'); 

        
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
    Username VARCHAR(20),
    Email EMAIL_DOMINIO NOT NULL,
    Password PASSWORD_DOMINIO NOT NULL,
    Autore BOOLEAN DEFAULT FALSE,

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
    DataCreazione TIMESTAMP NOT NULL,
    UserAutore VARCHAR(20) NOT NULL,

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
    Ordine INT,
    ID_Pagina SERIAL,
    Contenuto VARCHAR(100) NOT NULL,
    Riga INT NOT NULL,
    Collegamento BOOLEAN NOT NULL,

    PRIMARY KEY(Ordine, ID_Pagina),
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
    OrdineFrase INT,
    ID_Pagina SERIAL,
    ID_PaginaCollegata SERIAL,

    PRIMARY KEY(OrdineFrase, ID_Pagina, ID_PaginaCollegata),
    FOREIGN KEY(OrdineFrase, ID_Pagina) REFERENCES FRASE(Ordine, ID_Pagina)
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
    FraseCoinvolta VARCHAR(100) NOT NULL,
    FraseModificata VARCHAR(100),
    Data TIMESTAMP,
    ID_Pagina SERIAL NOT NULL,
    Utente VARCHAR(20) NOT NULL DEFAULT 'Unknown',

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
    Autore VARCHAR(20), 
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
ADD CONSTRAINT controlloIC CHECK(NOT(Tipo LIKE 'I' OR Tipo LIKE 'C' AND FraseModificata IS NOT NULL));

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
    !INSERT->TABLE->UTENTE!
  ---------------------------------
*/
INSERT INTO UTENTE VALUES;


/*
  ---------------------------------
    !INSERT->TABLE->PAGINA!
  ---------------------------------
*/
INSERT INTO PAGINA VALUES;


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




  
