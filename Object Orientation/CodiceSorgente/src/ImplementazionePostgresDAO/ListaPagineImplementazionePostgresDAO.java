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

    public Pagina getPaginaByIdDB(int idPagina)
    {
        Pagina pagina = null;

        try {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM PAGINA WHERE id_pagina=" + idPagina);
            ResultSet rs = ps.executeQuery();

            rs.next();
            ListaUtentiDAO l = new ListaUtentiImplementazionePostgresDAO();
            pagina = new Pagina(rs.getInt(1), rs.getString("Titolo"), null, rs.getTimestamp("DataCreazione"), l.getUtenteDB(rs.getString("userAutore")));
            pagina.setTestoRiferito(getTestoDB(pagina));

            rs.close();
            ps.close();

        } catch (Exception e) {
            System.out.println("Errore: " + e.getMessage());
        }

        return pagina;
    }

    @Override
    public void addPaginaDB(String titolo, Timestamp data, String autore) {
        try {
            PreparedStatement addPaginaPS = connection.prepareStatement("INSERT INTO pagina VALUES"
                    + "(default,'"+titolo+"',1, '"+data+"', '"+autore+"')");
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
    public void addTextDB(int idPagina, ArrayList<Frase> listaFrasi, Utente utilizzatore) {
        for (Frase f: listaFrasi)
        {
            try {
                System.out.println("Sono arrivato");
                CallableStatement cs = connection.prepareCall("CALL inserisciFrase(?, ?, null, ?, ?)");
                cs.setInt(1, idPagina);
                cs.setInt(2, f.getRiga());
                cs.setString(3, f.getContenuto());
                cs.setString(4, utilizzatore.getUsername());
                cs.execute();

                /*PreparedStatement addFrasePS = connection.prepareStatement("INSERT INTO frase VALUES"
                        + "('"+f.getRiga()+"','"+1+"','"+idPagina+"', '"+f.getContenuto()+"', '"+false+"')");*/
                //addFrasePS.executeUpdate();
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

    public ArrayList<Pagina> getPagineCreateDB(Utente utilizzatore) { //da migliorare chiamando getPaginaByID
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
            ps.close();
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

    public void editPageDB(Pagina pagina, ArrayList<Operazione> listaOperazioni)
    {
        try {
            PreparedStatement ps = connection.prepareStatement("UPDATE PAGINA SET titolo=" + "'" + pagina.getTitolo() + "'" + "WHERE id_pagina= " + pagina.getId());
            ps.executeUpdate();

            editTextDB(pagina, listaOperazioni);
        }
        catch (Exception e)
        {
            System.out.println("Errore: " + e.getMessage());
        }
    }

    public void editTextDB(Pagina pagina, ArrayList<Operazione> listaOperazioni)
    {
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM FRASE WHERE id_pagina=" + pagina.getId());
            ResultSet rs = ps.executeQuery();

            for(Operazione op : listaOperazioni)
            {
                rs.next();


            }

            rs.close();

        }
        catch (Exception e)
        {
            System.out.println("Errore: " + e.getMessage());
        }

    }

    public void addFraseDB(int idPagina, Frase fraseInserita)
    {
        try {
            PreparedStatement ps = connection.prepareStatement("INSERT INTO FRASE(ordine)");
            ps.executeUpdate();


        }
        catch (Exception e)
        {
            System.out.println("Errore: " + e.getMessage());
        }
    }
    public void removeFraseDB(int idPagina, Frase fraseEliminata)
    {

    }
    public void editFraseDB(int idPagina, Frase fraseOriginale, Frase fraseModificata)
    {

    }


    public Approvazione getApprovazioneDB(int id_operazione, Operazione operazione)
    {
        Approvazione approvazione = null;

        try
        {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM APPROVAZIONE WHERE id_operazione=" + id_operazione);
            ResultSet rs = ps.executeQuery();
            rs.next();

            ListaUtentiDAO l = new ListaUtentiImplementazionePostgresDAO();
            Utente autore = l.getUtenteDB(rs.getString("autore"));

            approvazione = new Approvazione(rs.getTimestamp("data"), rs.getBoolean("risposta"), operazione, autore);
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }

        return approvazione;
    }

    public ArrayList<Operazione> getProposteDaApprovareDB(Utente utilizzatore)
    {
        ArrayList<Operazione> proposte = new ArrayList<>();

        try {
            PreparedStatement ps = connection.prepareStatement(
                    "SELECT O.* FROM OPERAZIONE O, APPROVAZIONE A WHERE O.id_operazione=A.id_operazione AND A.risposta IS NULL AND A.autore=" + "'" + utilizzatore.getUsername() + "'" );
            ResultSet rs = ps.executeQuery();

            while(rs.next())
            {
                Pagina pagina = getPaginaByIdDB(rs.getInt("id_pagina")); //preleviamo la pagina che fa riferimento la proposta
                ListaUtentiDAO l= new ListaUtentiImplementazionePostgresDAO();
                Utente generico = l.getUtenteDB(rs.getString("utente")); //preleviamo l'utente che ha proposto la modifica
                Operazione operazione = null;

                if(rs.getString("tipo").equals("I")) {
                    operazione = new Inserimento(rs.getBoolean("proposta"), rs.getInt("riga"),
                            rs.getString("data"), generico, rs.getString("frasecoinvolta"), null, pagina);
                    proposte.add(operazione);
                }
                else if(rs.getString("tipo").equals("M"))
                {
                    operazione = new Modifica(rs.getBoolean("proposta"), rs.getInt("riga"),
                            rs.getString("data"), generico, rs.getString("frasecoinvolta"), rs.getString("frasemodificata"), null, pagina);
                    proposte.add(operazione);
                }
                else if(rs.getString("tipo").equals("C"))
                {
                    operazione = new Cancellazione(rs.getBoolean("proposta"), rs.getInt("riga"),
                            rs.getString("data"), generico, rs.getString("frasecoinvolta"), null, pagina);
                    proposte.add(operazione);
                }

                Approvazione approvazione = getApprovazioneDB(rs.getInt("id_operazione"), operazione);
                operazione.setApprovazione(approvazione);

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


}
