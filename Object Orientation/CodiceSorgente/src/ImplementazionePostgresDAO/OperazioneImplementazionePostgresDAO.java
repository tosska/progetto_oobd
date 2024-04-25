package ImplementazionePostgresDAO;

import DAO.OperazioneDAO;
import DAO.PaginaDAO;
import DAO.UtenteDAO;
import Database.ConnessioneDatabase;
import Model.*;

import java.sql.*;
import java.util.ArrayList;

public class OperazioneImplementazionePostgresDAO implements OperazioneDAO {

    private Connection connection;

    public OperazioneImplementazionePostgresDAO() {
        try {
            connection = ConnessioneDatabase.getInstance().getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Operazione> getProposteDaApprovareDB(ArrayList<Pagina> pagineUtilizzatore, Utente utilizzatore)
    {

        ArrayList<Operazione> proposte = new ArrayList<>();

        try {
            PreparedStatement ps = connection.prepareStatement(
                    "SELECT O.*, A.data AS dataRisposta, A.risposta FROM OPERAZIONE O, APPROVAZIONE A WHERE O.id_operazione=A.id_operazione AND A.risposta IS NULL " +
                            "AND A.autore=" + "'" + utilizzatore.getUsername() + "'" + "ORDER BY O.data DESC, O.id_pagina ASC");
            ResultSet rs = ps.executeQuery();

            while(rs.next())
            {
                //recupero le informazioni sull'operazione
                PaginaDAO lPagina = new PaginaImplementazionePostgresDAO();
                int idOperazione = rs.getInt("id_operazione");
                int idPagina = rs.getInt("id_pagina"); //preleviamo la pagina che fa riferimento la proposta
                UtenteDAO lUtente= new UtenteImplementazionePostgresDAO();
                Utente generico = lUtente.getUtenteDB(rs.getString("utente")); //preleviamo l'utente che ha proposto la modifica
                Timestamp data = rs.getTimestamp("data");
                Boolean proposta = rs.getBoolean("proposta");
                Operazione operazione = null;

                //recupero la pagina in cui avviene l'operazione, già precedentemente caricata dal db
                Pagina pagina=null;
                boolean trovato=false;
                for(int i=0; i<pagineUtilizzatore.size() && !trovato; i++)
                {
                    pagina = pagineUtilizzatore.get(i);

                    if(pagina.getId() == idPagina)
                        trovato=true;
                }

                //recupero le informazioni sulla frase coinvolta nell'operazione
                int riga = rs.getInt("riga");
                int ordine = rs.getInt("ordine");
                String contenuto = rs.getString("fraseCoinvolta");
                Frase fraseCoinvolta;

                if((fraseCoinvolta = pagina.getTestoRiferito().getFrase(riga, ordine)) == null)
                    fraseCoinvolta = new Frase(riga, ordine, contenuto, pagina.getTestoRiferito());


                if(rs.getString("tipo").equals("I")) {
                    operazione = new Inserimento(proposta, fraseCoinvolta, data, generico, pagina.getStorico(), pagina);
                    operazione.setId(idOperazione);

                }
                else if(rs.getString("tipo").equals("M"))
                {
                    Frase fraseModificata = new Frase(riga, ordine, rs.getString("fraseModificata"), pagina.getTestoRiferito());
                    operazione = new Modifica(proposta, fraseCoinvolta, fraseModificata, data, generico, pagina.getStorico(), pagina);
                    operazione.setId(idOperazione);
                }
                else if(rs.getString("tipo").equals("C"))
                {
                    operazione = new Cancellazione(proposta, fraseCoinvolta, data, generico, pagina.getStorico(), pagina);
                    operazione.setId(idOperazione);
                }

                Approvazione approvazione = new Approvazione(rs.getTimestamp("dataRisposta"), null, operazione, utilizzatore);
                operazione.setApprovazione(approvazione);
                proposte.add(operazione);
            }
            rs.close();
            ps.close();
        }
        catch (Exception e)
        {
            System.out.println("Errore: " + e.getMessage());
        }

        return proposte;

    }

    public ArrayList<Operazione> getOperazioniDB(Utente utilizzatore, int tipo)
    {
        ArrayList<Operazione> operazioni = new ArrayList<>();
        String comandoSql = "SELECT O.* FROM OPERAZIONE O WHERE utente = " + "'" + utilizzatore.getUsername() + "'";
        String comandoSql2 = "AND proposta=" ;

        if(tipo==1) //per avere tutto
            comandoSql2 = " ORDER BY O.data DESC";
        else if(tipo==2) //per avere solo operazioni da autore
            comandoSql2 += "false";
        else if(tipo==3) //per avere solo proposte
            comandoSql2 += "true";

        comandoSql += comandoSql2;

        try {
            PreparedStatement ps = connection.prepareStatement(comandoSql);
            ResultSet rs = ps.executeQuery();

            while(rs.next())
            {
                //recupero le informazioni sull'operazione
                int idOperazione = rs.getInt("id_operazione");
                PaginaDAO lPagina = new PaginaImplementazionePostgresDAO();
                Pagina pagina = lPagina.getPaginaByIdDB(rs.getInt("id_pagina")); //preleviamo la pagina che fa riferimento la proposta
                UtenteDAO lUtente= new UtenteImplementazionePostgresDAO();
                Utente generico = lUtente.getUtenteDB(rs.getString("utente")); //preleviamo l'utente che ha proposto la modifica
                Timestamp data = rs.getTimestamp("data");
                Boolean proposta = rs.getBoolean("proposta");
                Operazione operazione = null;

                //recupero le informazioni sulla frase coinvolta nell'operazione
                int riga = rs.getInt("riga");
                int ordine = rs.getInt("ordine");
                String contenuto = rs.getString("fraseCoinvolta");
                Frase fraseCoinvolta = new Frase(riga, ordine, contenuto, pagina.getTestoRiferito());



                if(rs.getString("tipo").equals("I")) {
                    operazione = new Inserimento(proposta, fraseCoinvolta, data, generico, pagina.getStorico(), pagina);
                    operazione.setId(idOperazione);

                }
                else if(rs.getString("tipo").equals("M"))
                {
                    Frase fraseModificata = new Frase(riga, ordine, rs.getString("fraseModificata"), pagina.getTestoRiferito());
                    operazione = new Modifica(proposta, fraseCoinvolta, fraseModificata, data, generico, pagina.getStorico(), pagina);
                    operazione.setId(idOperazione);

                }
                else if(rs.getString("tipo").equals("C"))
                {
                    operazione = new Cancellazione(proposta, fraseCoinvolta, data, generico, pagina.getStorico(), pagina);
                    operazione.setId(idOperazione);
                }


                operazioni.add(operazione);
            }
            rs.close();
            ps.close();
        }
        catch (Exception e)
        {
            System.out.println("Errore: " + e.getMessage());
        }

        return operazioni;


    }



    public void approvaPropostaDB(Operazione proposta, Utente utilizzatore, Boolean risposta)
    {
        try {
            CallableStatement cs = connection.prepareCall("CALL approvaproposta(?, ?, ?)");
            cs.setInt(1, proposta.getId());
            cs.setBoolean(2, risposta);
            cs.setString(3, utilizzatore.getUsername());
            cs.execute();
            connection.close();
        } catch (Exception e) {
            System.out.println("Errore: " + e.getMessage());
        }
    }

    public ArrayList<Operazione> getProposteUP_DB(Pagina pagina, Utente utente)
    {
        ArrayList<Operazione> operazioni = new ArrayList<>();

        try {
            PreparedStatement ps = connection.prepareStatement("SELECT O.* FROM OPERAZIONE O WHERE utente = " + "'" + utente.getUsername() + "' AND id_pagina=" + pagina.getId()
                                                                + " AND proposta=true");
            ResultSet rs = ps.executeQuery();

            while(rs.next())
            {
                //recupero le informazioni sull'operazione
                int idOperazione = rs.getInt("id_operazione");
                PaginaDAO lPagina = new PaginaImplementazionePostgresDAO();
                UtenteDAO lUtente= new UtenteImplementazionePostgresDAO();
                Timestamp data = rs.getTimestamp("data");
                Boolean proposta = rs.getBoolean("proposta");
                Operazione operazione = null;

                //recupero le informazioni sulla frase coinvolta nell'operazione
                int riga = rs.getInt("riga");
                int ordine = rs.getInt("ordine");
                String contenuto = rs.getString("fraseCoinvolta");
                Frase fraseCoinvolta = new Frase(riga, ordine, contenuto, pagina.getTestoRiferito());


                if(rs.getString("tipo").equals("I")) {
                    operazione = new Inserimento(proposta, fraseCoinvolta, data, utente, pagina.getStorico(), pagina);
                    operazione.setId(idOperazione);

                }
                else if(rs.getString("tipo").equals("M"))
                {
                    Frase fraseModificata = new Frase(riga, ordine, rs.getString("fraseModificata"), pagina.getTestoRiferito());
                    operazione = new Modifica(proposta, fraseCoinvolta, fraseModificata, data, utente, pagina.getStorico(), pagina);
                    operazione.setId(idOperazione);

                }
                else if(rs.getString("tipo").equals("C"))
                {
                    operazione = new Cancellazione(proposta, fraseCoinvolta, data, utente, pagina.getStorico(), pagina);
                    operazione.setId(idOperazione);
                }


                operazioni.add(operazione);
            }
            rs.close();
            ps.close();
        }
        catch (Exception e)
        {
            System.out.println("Errore: " + e.getMessage());
        }

        return operazioni;
    }



}
