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
    DataCreazione DATE,
    OraCreazione TIME,
    UsernameAutore VARCHAR(20),

    PRIMARY KEY(ID_Pagina),
    FOREIGN KEY(UsernameAutore) REFERENCES UTENTE(Username)
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
    Testo VARCHAR(100),
    Riga INT,
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
    ID_Collegamento SERIAL,

    PRIMARY KEY(OrdineFrase, ID_Pagina, ID_Collegamento),
    FOREIGN KEY(OrdineFrase) REFERENCES FRASE(Ordine)
    ON DELETE CASCADE,
    FOREIGN KEY(ID_Pagina) REFERENCES PAGINA(ID_Pagina)
    ON DELETE CASCADE
    ON UPDATE CASCADE,    -- ?
    FOREIGN KEY(ID_Collegamento) REFERENCES PAGINA(ID_Pagina)
    ON DELETE CASCADE
    ON UPDATE CASCADE   -- ?
};

/*
    ---------------------------
        !Table-INSERIMENTO_AUTORE!
    ---------------------------
*/
CREATE TABLE INSERIMENTO_AUTORE
{
    ID_Inserimento SERIAL,
    Riga INT,
    DataInserimento DATE,
    OraInserimento TIME,
    FraseInserita VARCHAR(100),
    ID_Pagina SERIAL,
    UsernameAutore VARCHAR(20),

    PRIMARY KEY(ID_Inserimento),
    FOREIGN KEY(ID_Pagina) REFERENCES PAGINA(ID_Pagina)
    ON DELETE CASCADE
    ON UPDATE CASCADE,    -- ?
    FOREIGN KEY(UsernameAutore) REFERENCES AUTORE(Username)   -- ?
};

/*
    ---------------------------
        !Table-MODIFICA_AUTORE!
    ---------------------------
*/
CREATE TABLE MODIFICA_AUTORE
{
    ID_Modifica SERIAL,
    Riga INT,
    DataModifica DATE, 
    OraModifica TIME,
    FraseOriginale VARCHAR(100),
    FraseModificata VARCHAR(100),
    ID_Pagina SERIAL,
    UsernameAutore VARCHAR(20),

    PRIMARY KEY(ID_Modifica),
    FOREIGN KEY(ID_Pagina) REFERENCES PAGINA(ID_Pagina)
    ON DELETE CASCADE
    ON UPDATE CASCADE,    -- ?
    FOREIGN KEY(UsernameAutore) REFERENCES AUTORE(Username)   -- ?
}

/*
    ---------------------------
        !Table-CANCELLAZIONE_AUTORE!
    ---------------------------
*/
CREATE TABLE CANCELLAZIONE_AUTORE
{
    ID_Cancellazione SERIAL,
    Riga INT,
    DataCancellazione DATE,
    OraCancellazione TIME,
    FraseEliminata VARCHAR(100),
    ID_Pagina SERIAL,
    UsernameAutore VARCHAR(20),

    PRIMARY KEY(ID_Cancellazione),
    FOREIGN KEY(ID_Pagina) REFERENCES PAGINA(ID_Pagina)
    ON DELETE CASCADE
    ON UPDATE CASCADE,  -- ?
    FOREIGN KEY(UsernameAutore) REFERENCES AUTORE(Username)   -- ?
}

/*
    ---------------------------
        !Table-PROPOSTA_INSERIMENTO!
    ---------------------------
*/
CREATE TABLE PROPOSTA_INSERIMENTO
{
    ID_Proposta SERIAL,
    Riga INT,
    DataProposta DATE,
    OraProposta TIME,
    FraseInserita VARCHAR(100),
    ID_Pagina SERIAL,
    UsernameUtente VARCHAR(20),
    UsernameAutore VARCHAR(20),
    Approvato BOOLEAN,
    DataApprovazione DATE,
    OraApprovazione TIME,

    PRIMARY KEY(ID_Proposta),
    FOREIGN KEY(ID_Pagina) REFERENCES PAGINA(ID_Pagina)
    ON DELETE CASCADE
    ON UPDATE CASCADE,    -- ?
    FOREIGN KEY(UsernameUtente) REFERENCES UTENTE(Username),    -- ?
    FOREIGN KEY(UsernameAutore) REFERENCES UTENTE(Username)     -- ?
}

/*
    ---------------------------
        !Table-PROPOSTA_MODIFICA!
    ---------------------------
*/
CREATE TABLE PROPOSTA_MODIFICA
{
    ID_Proposta SERIAL,
    Riga INT,
    DataProposta DATE,
    OraProposta TIME,
    FraseOriginale VARCHAR(100),
    FraseModificata VARCHAR(100),
    ID_Pagina SERIAL,
    UsernameUtente VARCHAR(20),
    UsernameAutore VARCHAR(20),
    Approvato BOOLEAN,
    DataApprovazione DATE,
    OraApprovazione TIME,

    PRIMARY KEY(ID_Proposta),
    FOREIGN KEY(ID_Pagina) REFERENCES PAGINA(ID_Pagina),
    ON DELETE CASCADE
    ON UPDATE CASCADE,    -- ?
    FOREIGN KEY(UsernameUtente) REFERENCES UTENTE(Username),    -- ?
    FOREIGN KEY(UsernameAutore) REFERENCES UTENTE(Username)     -- ?

}

/*
    ---------------------------
        !Table-PROPOSTA_CANCELLAZIONE!
    ---------------------------
*/
CREATE TABLE PROPOSTA_CANCELLAZIONE
{
    ID_Proposta SERIAL,
    Riga INT,
    DataProposta DATE,
    OraProposta TIME,
    FraseEliminata VARCHAR(100),
    ID_Pagina SERIAL,
    UsernameUtente VARCHAR(20),
    UsernameAutore VARCHAR(20),
    Approvato BOOLEAN,
    DataApprovazione DATE,
    OraApprovazione TIME,

    PRIMARY KEY(ID_Proposta),
    FOREIGN KEY(ID_Pagina) REFERENCES PAGINA(ID_Pagina)
    ON DELETE CASCADE
    ON UPDATE CASCADE,  -- ?
    FOREIGN KEY(UsernameAutore) REFERENCES UTENTE(Username),  -- ?
    FOREIGN KEY(UsernameUtente) REFERENCES UTENTE(Username)   -- ?
} 




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
    !INSERT->TABLE->INSERIMENTO_AUTORE!
  ---------------------------------
*/
INSERT INTO INSERIMENTO_AUTORE VALUES


/*
  ---------------------------------
    !INSERT->TABLE->MODIFICA_AUTORE!
  ---------------------------------
*/
INSERT INTO MODIFICA_AUTORE VALUES


/*
  ---------------------------------
    !INSERT->TABLE->CANCELLAZIONE_AUTORE!
  ---------------------------------
*/
INSERT INTO CANCELLAZIONE_AUTORE VALUES


/*
  ---------------------------------
    !INSERT->TABLE->PROPOSTA_INSERIMENTO!
  ---------------------------------
*/
INSERT INTO PROPOSTA_INSERIMENTO VALUES


/*
  ---------------------------------
    !INSERT->TABLE->PROPOSTA_MODIFICA!
  ---------------------------------
*/
INSERT INTO PROPOSTA_MODIFICA VALUES


/*
  ---------------------------------
    !INSERT->TABLE->PROPOSTA_CANCELLAZIONE!
  ---------------------------------
*/
INSERT INTO PROPOSTA_CANCELLAZIONE VALUES








CREATE USER silvio_barra PASSWORD 'ProgettoOOBD@'; -- Creazione Nuovo Utente 
GRANT ALL ON ALL TABLES IN SCHEMA public TO Silvio_Barra; -- Assegnazione Privileggi

/* ----------------------------------- */

