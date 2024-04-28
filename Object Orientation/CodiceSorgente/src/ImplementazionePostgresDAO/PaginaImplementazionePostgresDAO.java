package ImplementazionePostgresDAO;

import DAO.PaginaDAO;
import DAO.UtenteDAO;
import Database.ConnessioneDatabase;

import java.sql.*;
import java.util.ArrayList;

public class PaginaImplementazionePostgresDAO implements PaginaDAO {
    private Connection connection;


    public PaginaImplementazionePostgresDAO() {
        try {
            connection = ConnessioneDatabase.getInstance().getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void getPaginaByIdDB(int idPagina, ArrayList<String> paginaInfo)
    {

        try {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM PAGINA WHERE id_pagina=" + idPagina);
            ResultSet rs = ps.executeQuery();

            rs.next();

            paginaInfo.add(rs.getString(2)); //titolo
            paginaInfo.add(Integer.toString(rs.getInt(3))); //tema
            paginaInfo.add( rs.getString(4)); //data
            paginaInfo.add(rs.getString(5)); //username


            rs.close();
            ps.close();

        } catch (Exception e) {
            System.out.println("Errore: " + e.getMessage());
        }

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

    public void raccogliTemi(ArrayList<Integer> listaIdTemi, ArrayList<String> listaNomiTemi)
    {
        try {
            // Esecuzione della query per ottenere i temi
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM tema");


            // Aggiunta dei temi al menu a tendina
            while (resultSet.next()) {
                listaIdTemi.add(resultSet.getInt("idTema"));
                listaNomiTemi.add(resultSet.getString("nome"));
            }

            resultSet.close();

        } catch (Exception e){
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


    public void getTestoDB(int idPagina, ArrayList<Integer> riga, ArrayList<Integer> ordine, ArrayList<String> contenuto, ArrayList<Boolean> collegamento) {


        try {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM FRASE WHERE id_Pagina=" + idPagina + " ORDER BY riga, ordine");
            ResultSet rs = ps.executeQuery();

            while(rs.next())
            {
                riga.add(rs.getInt(1)); //riga
                ordine.add(rs.getInt(2)); //ordine
                contenuto.add(rs.getString(4)); //contenuto
                collegamento.add(rs.getBoolean(5));
            }
            rs.close();
        }
        catch (Exception e)
        {
            System.out.println("Errore: " + e.getMessage());
        }


    }

    public int getIdCollegamentoDB(int id_pagina, int riga, int ordine)
    {
        int idCollegamento=-1;
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT id_paginacollegata FROM COLLEGAMENTO WHERE id_Pagina="+id_pagina+ " AND rigafrase="+ riga + " AND ordinefrase=" + ordine);
            ResultSet rs = ps.executeQuery();
            rs.next();

            idCollegamento = rs.getInt(1);

            rs.close();
            ps.close();

        }
        catch (Exception e) //fare exception not found
        {
            System.out.println("Errore: " + e.getMessage());
        }

        return idCollegamento;
    }

    public void getPagineCreateDB(String utente, ArrayList<Integer> id,
                                  ArrayList<String> titolo, ArrayList<Integer> tema,
                                  ArrayList<Timestamp> dataCreazione, ArrayList<String> autore) {


        try {
            PreparedStatement listaPS = connection.prepareStatement("SELECT * FROM PAGINA WHERE userAutore =" + "'" + utente + "'");
            ResultSet rs = listaPS.executeQuery();

            while(rs.next())
            {
                id.add(rs.getInt(1));
                titolo.add(rs.getString(2));
                tema.add(rs.getInt(3));
                dataCreazione.add(rs.getTimestamp(4));
                autore.add(rs.getString(5));
            }

            rs.close();
            listaPS.close();
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

    public void cercaPaginaDB(String titolo, ArrayList<String> pagina)
    {

        try {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM PAGINA WHERE titolo=" + "'" + titolo + "'");
            ResultSet rs = ps.executeQuery();

            if(rs.next())
            {
                pagina.add(Integer.toString(rs.getInt(1))); //id
                pagina.add(Integer.toString(rs.getInt(3))); //tema
                pagina.add(rs.getString(4)); //data
                pagina.add(rs.getString(5)); //autore
            }


            rs.close();
            ps.close();
        }
        catch (Exception e)
        {
            System.out.println("Errore: " + e.getMessage());
        }


    }

    public String getTemaDB(int idTema)
    {
        String nome = null;

        try {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM TEMA WHERE idTema = " + "'" + idTema + "'");
            ResultSet rs = ps.executeQuery();
            rs.next();

            nome = rs.getString("nome");

            rs.close();
            ps.close();

        }
        catch (Exception e) {
            System.out.println("Errore: " + e.getMessage());
        }

        return nome;
    }

    public void getStoricoDB(int idPagina, ArrayList<Object> operazioni)
    {

        try {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM storicopagine WHERE id_pagina=" + idPagina);
            ResultSet rs = ps.executeQuery();

            while(rs.next()) {

                operazioni.add(rs.getInt(1)); //id
                operazioni.add(rs.getString(2)); //tipo
                operazioni.add(rs.getBoolean(3)); //proposta
                operazioni.add(rs.getInt(4)); //riga
                operazioni.add(rs.getInt(5)); //ordine
                operazioni.add(rs.getString(6)); //frase coinvolta
                operazioni.add(rs.getString(7)); //frase modificata
                operazioni.add(rs.getTimestamp(8)); //data
                operazioni.add(rs.getInt(9)); //id pagina
                operazioni.add(rs.getString(10)); //utente
            }


            rs.close();
            ps.close();
        }
        catch (Exception e)
        {
            System.out.println("Errore: " + e.getMessage());
        }


    }



    public void addFraseDB(int idPagina, int riga, int ordine, String contenuto, String utente)
    {
        try {

            CallableStatement cs = connection.prepareCall("CALL inserisciFrase(?, ?, ?, ?, ?)");
            cs.setInt(1, idPagina);
            cs.setInt(2, riga);
            cs.setInt(3, ordine);
            cs.setString(4, contenuto);
            cs.setString(5, utente);
            cs.execute();

            cs.close();
        }
        catch (Exception e)
        {
            System.out.println("Errore: " + e.getMessage());
        }
    }
    public void removeFraseDB(int idPagina, int riga, int ordine, String utente)
    {

        try {

            CallableStatement cs = connection.prepareCall("CALL rimuoviFrase(?, ?, ?, ?)");
            cs.setInt(1, idPagina);
            cs.setInt(2, riga);
            cs.setInt(3, ordine);
            cs.setString(4, utente);
            cs.execute();

            cs.close();

        }
        catch (Exception e)
        {
            System.out.println("Errore: " + e.getMessage());
        }
    }
    public void editFraseDB(int idPagina, int riga, int ordine, String contenuto, String utente)
    {

        try {

            CallableStatement cs = connection.prepareCall("CALL modificaFrase(?, ?, ?, ?, ?)");
            cs.setInt(1, idPagina);
            cs.setInt(2, riga);
            cs.setInt(3, ordine);
            cs.setString(4, contenuto);
            cs.setString(5, utente);
            cs.execute();

            cs.close();

        }
        catch (Exception e)
        {
            System.out.println("Errore: " + e.getMessage());
        }
    }

    public void insertLinkDB(int idPagina, int riga, int ordine, int idPaginaCollegata, String utente)
    {

        try {

            CallableStatement cs = connection.prepareCall("CALL inserisciCollegamento(?, ?, ?, ?, ?)");
            cs.setInt(1, idPagina);
            cs.setInt(2, riga);
            cs.setInt(3, ordine);
            cs.setInt(4, idPaginaCollegata);
            cs.setString(5, utente);
            cs.execute();

            cs.close();

        }
        catch (Exception e)
        {
            System.out.println("Errore: " + e.getMessage());
        }
    }

    public void removeLinkDB(int idPagina, int riga, int ordine, String utente)
    {
        try {

            CallableStatement cs = connection.prepareCall("CALL rimuoviCollegamento(?, ?, ?, ?)");
            cs.setInt(1, idPagina);
            cs.setInt(2, riga);
            cs.setInt(3, ordine);
            cs.setString(4, utente);
            cs.execute();

            cs.close();

        }
        catch (Exception e)
        {
            System.out.println("Errore: " + e.getMessage());
        }
    }

    /*

    public Approvazione getApprovazioneDB(int id_operazione, Operazione operazione)
    {
        Approvazione approvazione = null;

        try
        {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM APPROVAZIONE WHERE id_operazione=" + id_operazione);
            ResultSet rs = ps.executeQuery();
            rs.next();

            UtenteDAO l = new UtenteImplementazionePostgresDAO();
            Utente autore = l.getUtenteDB(rs.getString("autore"));

            approvazione = new Approvazione(rs.getTimestamp("data"), rs.getBoolean("risposta"), operazione, autore);
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }

        return approvazione;
    }

*/




}

