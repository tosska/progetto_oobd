package ImplementazionePostgresDAO;

import DAO.ListaOperazioneDAO;
import DAO.ListaPagineDAO;
import DAO.ListaUtentiDAO;
import Database.ConnessioneDatabase;
import Model.*;

import java.sql.*;
import java.util.ArrayList;

public class ListaOperazioneImplementazionePostgresDAO implements ListaOperazioneDAO {

    private Connection connection;

    public ListaOperazioneImplementazionePostgresDAO() {
        try {
            connection = ConnessioneDatabase.getInstance().getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Operazione> getProposteDaApprovareDB(Utente utilizzatore)
    {

        ArrayList<Operazione> proposte = new ArrayList<>();

        try {
            PreparedStatement ps = connection.prepareStatement(
                    "SELECT O.*, A.data AS dataRisposta, A.risposta FROM OPERAZIONE O, APPROVAZIONE A WHERE O.id_operazione=A.id_operazione AND A.risposta IS NULL " +
                            "AND A.autore=" + "'" + utilizzatore.getUsername() + "'" + "ORDER BY O.data DESC");
            ResultSet rs = ps.executeQuery();

            while(rs.next())
            {
                //recupero le informazioni sull'operazione
                ListaPagineDAO lPagina = new ListaPagineImplementazionePostgresDAO();
                Pagina pagina = lPagina.getPaginaByIdDB(rs.getInt("id_pagina")); //preleviamo la pagina che fa riferimento la proposta
                ListaUtentiDAO lUtente= new ListaUtentiImplementazionePostgresDAO();
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
                    operazione = new Inserimento(proposta, fraseCoinvolta, data.toString(), generico, pagina.getStorico(), pagina);

                }
                else if(rs.getString("tipo").equals("M"))
                {
                    Frase fraseModificata = new Frase(riga, ordine, rs.getString("fraseModificata"), pagina.getTestoRiferito());
                    operazione = new Modifica(proposta, fraseCoinvolta, fraseModificata, data.toString(), generico, pagina.getStorico(), pagina);

                }
                else if(rs.getString("tipo").equals("C"))
                {
                    operazione = new Cancellazione(proposta, fraseCoinvolta, data.toString(), generico, pagina.getStorico(), pagina);
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

    public ArrayList<Operazione> getOperazioniDB(Utente utilizzatore)
    {

        ArrayList<Operazione> operazioni = new ArrayList<>();

        try {
            PreparedStatement ps = connection.prepareStatement(
                    "SELECT O.* FROM OPERAZIONE O " +
                            "WHERE utente = " + "'" + utilizzatore.getUsername() + "'" + "ORDER BY O.data DESC");
            ResultSet rs = ps.executeQuery();

            while(rs.next())
            {
                //recupero le informazioni sull'operazione
                ListaPagineDAO lPagina = new ListaPagineImplementazionePostgresDAO();
                Pagina pagina = lPagina.getPaginaByIdDB(rs.getInt("id_pagina")); //preleviamo la pagina che fa riferimento la proposta
                ListaUtentiDAO lUtente= new ListaUtentiImplementazionePostgresDAO();
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
                    operazione = new Inserimento(proposta, fraseCoinvolta, data.toString(), generico, pagina.getStorico(), pagina);

                }
                else if(rs.getString("tipo").equals("M"))
                {
                    Frase fraseModificata = new Frase(riga, ordine, rs.getString("fraseModificata"), pagina.getTestoRiferito());
                    operazione = new Modifica(proposta, fraseCoinvolta, fraseModificata, data.toString(), generico, pagina.getStorico(), pagina);

                }
                else if(rs.getString("tipo").equals("C"))
                {
                    operazione = new Cancellazione(proposta, fraseCoinvolta, data.toString(), generico, pagina.getStorico(), pagina);
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
