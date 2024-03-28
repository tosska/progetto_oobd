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

            Tema tema = getTemaDB(rs.getInt("tema"));    // vado a recuperare il tema

            pagina = new Pagina(rs.getInt(1), rs.getString("Titolo"), null, rs.getTimestamp("DataCreazione"), l.getUtenteDB(rs.getString("userAutore")),
                    tema);
            pagina.setTestoRiferito(getTestoDB(pagina));

            rs.close();
            ps.close();

        } catch (Exception e) {
            System.out.println("Errore: " + e.getMessage());
        }

        return pagina;
    }

    @Override
    public void addPaginaDB(String titolo, Timestamp data, String autore, int idTema) {
        try {
            PreparedStatement addPaginaPS = connection.prepareStatement("INSERT INTO pagina VALUES"
                    + "(default,'"+titolo+"','"+idTema+"', '"+data+"', '"+autore+"')");
            addPaginaPS.executeUpdate();
            // connection.close();
        } catch (Exception e) {
            System.out.println("Errore: " + e.getMessage());
        }

    }

    public void addTemaDB(String tema) {
        try {
            PreparedStatement addTemaPS = connection.prepareStatement("INSERT INTO tema VALUES"
            + "(default, '"+tema+"')");
            addTemaPS.executeUpdate();
            connection.close();
        } catch (Exception e) {
            System.out.println("Errore: " + e.getMessage());
        }
    }

    public ArrayList<Tema> raccogliTemi()
    {
        ArrayList<Tema> listaTemi = new ArrayList<>();

        try {
            // Esecuzione della query per ottenere i temi
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM tema");

            // Aggiunta dei temi al menu a tendina
            while (resultSet.next()) {
                Tema attuale = new Tema(resultSet.getInt("idTema"), resultSet.getString("nome"));
                listaTemi.add(attuale);
            }

            resultSet.close();

        } catch (Exception e){
            System.out.println("Errore: " + e.getMessage());
        }


        return listaTemi;
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
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM FRASE WHERE id_Pagina=" + p.getId() + " ORDER BY riga, ordine");
            ResultSet rs = ps.executeQuery();

            while(rs.next())
            {
                t.addListaFrasiCoda(new Frase(rs.getInt("riga"),rs.getInt("Ordine"), rs.getString("Contenuto"), t));
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
                Tema tema = getTemaDB(rs.getInt("tema"));

                Pagina p = new Pagina(rs.getInt(1), rs.getString("Titolo"), null, rs.getTimestamp("dataCreazione"), utilizzatore, tema);
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

            Tema tema = getTemaDB(rs.getInt("tema"));

            p = new Pagina(rs.getInt(1), rs.getString("Titolo"), null, rs.getTimestamp("DataCreazione"), l.getUtenteDB(rs.getString("userAutore")), tema);
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

    public Tema getTemaDB(int idTema)
    {
        Tema tema = null;

        try {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM TEMA WHERE idTema = " + "'" + idTema + "'");
            ResultSet rs = ps.executeQuery();
            rs.next();

            tema = new Tema(rs.getInt("idTema"), rs.getString("nome"));

            rs.close();
            ps.close();

        }
        catch (Exception e) {
            System.out.println("Errore: " + e.getMessage());
        }

        return tema;
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

                int riga = rs.getInt("riga");
                int ordine = rs.getInt("ordine");
                String contenuto = rs.getString("fraseCoinvolta");
                Frase fraseCoinvolta = new Frase(riga, ordine, contenuto, pagina.getTestoRiferito());
                Boolean proposta = rs.getBoolean("proposta");
                Timestamp data = rs.getTimestamp("data");


                if(rs.getString("tipo").equals("I")) {

                    Inserimento inserimento = new Inserimento(proposta, fraseCoinvolta, data, utente, s, pagina);
                    s.addOperazione(inserimento);
                }
                else if(rs.getString("tipo").equals("M"))
                {
                    Frase fraseModificata = new Frase(riga, ordine, rs.getString("fraseModificata"), pagina.getTestoRiferito());
                    Modifica modifica = new Modifica(proposta, fraseCoinvolta, fraseModificata, data, utente, s, pagina);
                    s.addOperazione(modifica);

                }
                else if(rs.getString("tipo").equals("C"))
                {
                    Cancellazione cancellazione = new Cancellazione(proposta, fraseCoinvolta, data, utente, s, pagina);
                    s.addOperazione(cancellazione);
                }
            }

            rs.close();
            ps.close();
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
        for(Operazione op : listaOperazioni)
        {
            if(op instanceof Inserimento)
                addFraseDB(pagina, (Inserimento) op);
            else if (op instanceof Modifica) {
                editFraseDB(pagina, (Modifica) op);
            }
            else if (op instanceof Cancellazione) {
                removeFraseDB(pagina, (Cancellazione) op);
            }
        }
    }

    public void addFraseDB(Pagina pagina, Inserimento inserimento)
    {
        String comandoSql;
        Frase fraseCoinvolta = inserimento.getFraseCoinvolta();
        try {

            CallableStatement cs = connection.prepareCall("CALL inserisciFrase(?, ?, ?, ?, ?)");
            cs.setInt(1, pagina.getId());
            cs.setInt(2, fraseCoinvolta.getRiga());
            cs.setInt(3, fraseCoinvolta.getOrdine());
            cs.setString(4, fraseCoinvolta.getContenuto());
            cs.setString(5, inserimento.getUtente().getUsername());
            cs.execute();

            cs.close();
        }
        catch (Exception e)
        {
            System.out.println("Errore: " + e.getMessage());
        }
    }
    public void removeFraseDB(Pagina pagina, Cancellazione cancellazione)
    {
        String comandoSql;
        Frase fraseCoinvolta = cancellazione.getFraseCoinvolta();
        try {

            CallableStatement cs = connection.prepareCall("CALL rimuoviFrase(?, ?, ?, ?)");
            cs.setInt(1, pagina.getId());
            cs.setInt(2, fraseCoinvolta.getRiga());
            cs.setInt(3, fraseCoinvolta.getOrdine());
            cs.setString(4, cancellazione.getUtente().getUsername());
            cs.execute();

            cs.close();

        }
        catch (Exception e)
        {
            System.out.println("Errore: " + e.getMessage());
        }
    }
    public void editFraseDB(Pagina pagina, Modifica modifica)
    {
        String comandoSql;
        Frase fraseModificata = modifica.getFraseModificata();

        try {

            CallableStatement cs = connection.prepareCall("CALL modificaFrase(?, ?, ?, ?, ?)");
            cs.setInt(1, pagina.getId());
            cs.setInt(2, fraseModificata.getRiga());
            cs.setInt(3, fraseModificata.getOrdine());
            cs.setString(4, fraseModificata.getContenuto());
            cs.setString(5, modifica.getUtente().getUsername());
            cs.execute();

        }
        catch (Exception e)
        {
            System.out.println("Errore: " + e.getMessage());
        }
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






}

