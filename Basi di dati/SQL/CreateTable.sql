--\Inizio File SQL DB Wiki Platform DB

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
CREATE DOMAIN USERNAME_DOMINIO

-- Vincolo Di Dominio: Email
CREATE DOMAIN EMAIL_DOMINIO

        



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
{
    Username VARCHAR(20),
    Email VARCHAR(50),
    Password PASSWORD_DOMINIO,
    Autore BOOLEAN,

    PRIMARY KEY(Username)
};

/*
    ---------------------------
        !Table-PAGINA!
    ---------------------------
*/
CREATE TABLE PAGINA 
{
    ID_Pagina SERIAL,
    Titolo VARCHAR(50),
    DataCreazione TIMESTAMP,
    UserAutore VARCHAR(20),

    PRIMARY KEY(ID_Pagina),
    FOREIGN KEY(UserAutore) REFERENCES UTENTE(Username)
    ON DELETE SET DEFAULT  -- ?
};

/*
    ---------------------------
        !Table-FRASE!
    ---------------------------
*/
CREATE TABLE FRASE 
{
    Ordine SERIAL,
    ID_Pagina SERIAL,
    Contenuto VARCHAR(100) NOT NULL,
    Riga INT NOT NULL,
    Collegamento BOOLEAN,

    PRIMARY KEY(Ordine, ID_Pagina),
    FOREIGN KEY(ID_Pagina) REFERENCES PAGINA(ID_Pagina)
    ON DELETE CASCADE
    ON UPDATE CASCADE   -- ?
};

/*
    ---------------------------
        !Table-COLLEGAMENTO!
    ---------------------------
*/
CREATE TABLE COLLEGAMENTO
{
    OrdineFrase SERIAL,
    ID_Pagina SERIAL,
    ID_PaginaCollegata SERIAL,

    PRIMARY KEY(OrdineFrase, ID_Pagina, ID_PaginaCollegata),
    FOREIGN KEY(OrdineFrase, ID_Pagina) REFERENCES FRASE(Ordine, ID_Pagina)
    ON DELETE CASCADE,
    FOREIGN KEY(ID_PaginaCollegata) REFERENCES PAGINA(ID_Pagina)
    ON DELETE CASCADE
    ON UPDATE CASCADE   -- ?
};

/*
    ---------------------------
        !Table-INSERIMENTO!
    ---------------------------
*/

CREATE TABLE INSERIMENTO
{
    ID_Inserimento SERIAL,
    Riga INT NOT NULL,
    FraseInserita VARCHAR(100) NOT NULL,
    Data TIMESTAMP,
    Proposta BOOLEAN NOT NULL,
    ID_Pagina SERIAL NOT NULL,
    Utente VARCHAR(20) NOT NULL,

    PRIMARY KEY(ID_Inserimento),
    FOREIGN KEY(ID_Pagina) REFERENCES PAGINA(ID_Pagina),
    FOREIGN KEY(Utente) REFERENCES UTENTE(Username)
};

/*
    ---------------------------
        !Table-MODIFICA!
    ---------------------------
*/

CREATE TABLE MODIFICA
{
    ID_Modifica SERIAL,
    Riga INT NOT NULL,
    FraseOriginale VARCHAR(100) NOT NULL,
    FraseModificata VARCHAR(100) NOT NULL,
    Data TIMESTAMP,
    Proposta BOOLEAN NOT NULL,
    ID_Pagina SERIAL NOT NULL,
    Utente VARCHAR(20) NOT NULL,

    PRIMARY KEY(ID_Modifica),
    FOREIGN KEY(ID_Pagina) REFERENCES PAGINA(ID_Pagina),
    FOREIGN KEY(Utente) REFERENCES UTENTE(Username)
};


/*
    ---------------------------
        !Table-CANCELLAZIONE!
    ---------------------------
*/

CREATE TABLE CANCELLAZIONE
{
    ID_Cancellazione SERIAL,
    Riga INT NOT NULL,
    FraseEliminata VARCHAR(100) NOT NULL,
    Data TIMESTAMP,
    Proposta BOOLEAN NOT NULL,
    ID_Pagina SERIAL NOT NULL,
    Utente VARCHAR(20) NOT NULL,

    PRIMARY KEY(ID_Cancellazione),
    FOREIGN KEY(ID_Pagina) REFERENCES PAGINA(ID_Pagina),
    FOREIGN KEY(Utente) REFERENCES UTENTE(Username)
};

/*
    ---------------------------
        !Table-APPROVAZIONE_INSERIMENTO!
    ---------------------------
*/

CREATE TABLE APPROVAZIONE_INSERIMENTO
{
    ID_Inserimento SERIAL,
    Autore VARCHAR(20),
    Data TIMESTAMP,
    Risposta BOOLEAN,

    PRIMARY KEY(ID_Inserimento, Autore),
    FOREIGN KEY(ID_Inserimento) REFERENCES INSERIMENTO(ID_Inserimento),
    FOREIGN KEY(Autore) REFERENCES UTENTE(Username),
};

/*
    ---------------------------
        !Table-APPROVAZIONE_MODIFICA!
    ---------------------------
*/

CREATE TABLE APPROVAZIONE_MODIFICA
{
    ID_Modifica SERIAL,
    Autore VARCHAR(20),
    Data TIMESTAMP,
    Risposta BOOLEAN,

    PRIMARY KEY(ID_Modifica, Autore),
    FOREIGN KEY(ID_Modifica) REFERENCES MODIFICA(ID_Modifica),
    FOREIGN KEY(Autore) REFERENCES UTENTE(Username),
};


/*
    ---------------------------
        !Table-APPROVAZIONE_CANCELLAZIONE!
    ---------------------------
*/

CREATE TABLE APPROVAZIONE_CANCELLAZIONE
{
    ID_Cancellazione SERIAL,
    Autore VARCHAR(20),
    Data TIMESTAMP,
    Risposta BOOLEAN,

    PRIMARY KEY(ID_Cancellazione, Autore),
    FOREIGN KEY(ID_Cancellazione) REFERENCES CANCELLAZIONE(ID_Cancellazione),
    FOREIGN KEY(Autore) REFERENCES UTENTE(Username),
};




/*
  ---------------------------------
    !INSERT->TABLE->UTENTE!
  ---------------------------------
*/
INSERT INTO UTENTE VALUES


/*
  ---------------------------------
    !INSERT->TABLE->PAGINA!
  ---------------------------------
*/
INSERT INTO PAGINA VALUES


/*
  ---------------------------------
    !INSERT->TABLE->FRASE!
  ---------------------------------
*/
INSERT INTO FRASE VALUES


/*
  ---------------------------------
    !INSERT->TABLE->COLLEGAMENTO!
  ---------------------------------
*/
INSERT INTO COLLEGAMENTO VALUES


/*
  ---------------------------------
    !INSERT->TABLE->INSERIMENTO!
  ---------------------------------
*/
INSERT INTO INSERIMENTO VALUES


/*
  ---------------------------------
    !INSERT->TABLE->MODIFICA !
  ---------------------------------
*/
INSERT INTO MODIFICA VALUES


/*
  ---------------------------------
    !INSERT->TABLE->CANCELLAZIONE !
  ---------------------------------
*/
INSERT INTO CANCELLAZIONE VALUES


/*
  ---------------------------------
    !INSERT->TABLE->APPROVAZIONE_INSERIMENTO!
  ---------------------------------
*/
INSERT INTO APPROVAZIONE_INSERIMENTO VALUES


/*
  ---------------------------------
    !INSERT->TABLE->APPROVAZIONE_MODIFICA!
  ---------------------------------
*/
INSERT INTO APPROVAZIONE_MODIFICA VALUES


/*
  ---------------------------------
    !INSERT->TABLE->APPROVAZIONE_CANCELLAZIONE!
  ---------------------------------
*/
INSERT INTO APPROVAZIONE_CANCELLAZIONE VALUES








CREATE USER silvio_barra PASSWORD 'ProgettoOOBD@'; -- Creazione Nuovo Utente 
GRANT ALL ON ALL TABLES IN SCHEMA public TO Silvio_Barra; -- Assegnazione Privilegi

/* ----------------------------------- */

