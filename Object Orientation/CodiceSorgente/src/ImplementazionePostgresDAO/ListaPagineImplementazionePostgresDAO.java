package ImplementazionePostgresDAO;

import DAO.ListaPagineDAO;
import DAO.ListaUtentiDAO;
import Database.ConnessioneDatabase;
import Model.*;

import java.sql.*;
import java.util.ArrayList;

public class ListaPagineImplementazionePostgresDAO implements ListaPagineDAO {
    private Connection connection;

    public ListaPagineImplementazionePostgresDAO() {
        try {
            connection = ConnessioneDatabase.getInstance().getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addPaginaDB(String titolo, Timestamp data, String autore) {
        try {
            PreparedStatement addPaginaPS = connection.prepareStatement("INSERT INTO pagina VALUES"
                    + "(default,'"+titolo+"','"+data+"', '"+autore+"')");
            addPaginaPS.executeUpdate();
            // connection.close();
        } catch (Exception e) {
            System.out.println("Errore: " + e.getMessage());
        }

    }

    @Override
    public int recuperaIdPagina() {
        int idPagina = 0;
        try {
            PreparedStatement queryControllo = connection.prepareStatement("SELECT MAX(id_pagina) FROM pagina");
            ResultSet rs = queryControllo.executeQuery();

            while (rs.next()) {
                idPagina = rs.getInt(1);
            }

            rs.close();
            queryControllo.close();
            // connection.close();


        } catch (Exception e) {
            System.out.println("Errore: " + e.getMessage());
        }
        return idPagina;
    }

    @Override
    public void addFraseDB(int idPagina, ArrayList<Frase> listaFrasi) {
        for (Frase f: listaFrasi)
        {
            try {
                PreparedStatement addFrasePS = connection.prepareStatement("INSERT INTO frase VALUES"
                        + "(default,'"+idPagina+"','"+f.getContenuto()+"', '"+f.getRiga()+"', '"+false+"')");
                addFrasePS.executeUpdate();
                // connection.close();
            } catch (Exception e) {
                System.out.println("Errore: " + e.getMessage());
            }
        }

    }

    @Override
    public Testo getTestoDB(Pagina p) {

        Testo t = new Testo(p);
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM FRASE WHERE id_Pagina=" + p.getId());
            ResultSet rs = ps.executeQuery();

            while(rs.next())
            {
                t.inserisciFrase(new Frase(rs.getInt("Ordine"), rs.getString("Contenuto"), rs.getInt("riga"), t));
            }
            rs.close();
        }
        catch (Exception e)
        {
            System.out.println("Errore: " + e.getMessage());
        }


        return t;
    }

    public ArrayList<Pagina> getPagineCreateDB(Utente utilizzatore) {
        ArrayList<Pagina> lista = new ArrayList<>();

        try {
            PreparedStatement listaPS = connection.prepareStatement("SELECT * FROM PAGINA WHERE userAutore =" + "'" + utilizzatore.getUsername() + "'");
            ResultSet rs = listaPS.executeQuery();

            while(rs.next())
            {
                Pagina p = new Pagina(rs.getInt(1), rs.getString("Titolo"), null, rs.getTimestamp("dataCreazione"), utilizzatore);
                p.setTestoRiferito(getTestoDB(p)); //mando l'id della pagina e l'oggetto pagina
                lista.add(p);
            }

            rs.close();
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return lista;

    }

    public Pagina cercaPaginaDB(String titolo)
    {
        Pagina p = null;

        try {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM PAGINA WHERE titolo=" + "'" + titolo + "'");
            ResultSet rs = ps.executeQuery();
            rs.next();//da capire se è possibile fare meglio
            ListaUtentiDAO l = new ListaUtentiImplementazionePostgresDAO();
            p = new Pagina(rs.getInt(1), rs.getString("Titolo"), null, rs.getTimestamp("DataCreazione"), l.getUtenteDB(rs.getString("userAutore")));
            p.setTestoRiferito(getTestoDB(p));
            rs.close();
        }
        catch (Exception e)
        {
            System.out.println("Errore: " + e.getMessage());
        }

        return p;
    }

    public Storico getStoricoDB(Pagina pagina)
    {
        Storico s = new Storico(pagina);

        try {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM OPERAZIONE WHERE id_pagina=" + pagina.getId());
            ResultSet rs = ps.executeQuery();

            while(rs.next())
            {
                ListaUtentiDAO l = new ListaUtentiImplementazionePostgresDAO();
                Utente utente = l.getUtenteDB(rs.getString("utente"));

                if(rs.getString("tipo").equals("I")) {
                    Inserimento inserimento = new Inserimento(rs.getBoolean("proposta"), rs.getInt("riga"),
                            rs.getString("data"), utente, rs.getString("frasecoinvolta"), s, pagina);
                    s.addOperazione(inserimento);
                }
                else if(rs.getString("tipo").equals("M"))
                {
                    Modifica modifica = new Modifica(rs.getBoolean("proposta"), rs.getInt("riga"),
                            rs.getString("data"), utente, rs.getString("frasecoinvolta"), rs.getString("frasemodificata"), s, pagina);
                    s.addOperazione(modifica);
                }
                else if(rs.getString("tipo").equals("C"))
                {
                    Cancellazione cancellazione = new Cancellazione(rs.getBoolean("proposta"), rs.getInt("riga"),
                            rs.getString("data"), utente, rs.getString("frasecoinvolta"), s, pagina);
                    s.addOperazione(cancellazione);
                }
            }

            rs.close();
        }
        catch (Exception e)
        {
            System.out.println("Errore: " + e.getMessage());
        }

        return s;
    }


}
