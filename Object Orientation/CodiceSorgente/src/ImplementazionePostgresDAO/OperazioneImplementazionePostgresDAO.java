package ImplementazionePostgresDAO;

import DAO.OperazioneDAO;
import DAO.PaginaDAO;
import DAO.UtenteDAO;
import Database.ConnessioneDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.Objects;

public class OperazioneImplementazionePostgresDAO implements OperazioneDAO {

    private Connection connection;

    public OperazioneImplementazionePostgresDAO() {
        try {
            connection = ConnessioneDatabase.getInstance().getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void getProposteDaApprovareDB(String username, ArrayList<Object> proposta)
    {

        try {
            PreparedStatement ps = connection.prepareStatement(
                    "SELECT O.* FROM OPERAZIONE O, APPROVAZIONE A WHERE O.id_operazione=A.id_operazione AND A.risposta IS NULL " +
                            "AND A.autore=" + "'" + username + "'" + "ORDER BY O.data DESC, O.id_pagina ASC");
            ResultSet rs = ps.executeQuery();

            while(rs.next())
            {
                proposta.add(rs.getInt(1)); //id
                proposta.add(rs.getString(2)); //tipo
                proposta.add(rs.getBoolean(3)); //proposta
                proposta.add(rs.getInt(4)); //riga
                proposta.add(rs.getInt(5)); //ordine
                proposta.add(rs.getString(6)); //frase coinvolta
                proposta.add(rs.getString(7)); //frase modificata
                proposta.add(rs.getTimestamp(8)); //data
                proposta.add(rs.getInt(9)); //id pagina

            }
            rs.close();
            ps.close();
        }
        catch (Exception e)
        {
            System.out.println("Errore: " + e.getMessage());
        }

    }

    public void getOperazioniDB(String username, int t, ArrayList<Object> operazioni)
    {

        String comandoSql = "SELECT O.* FROM OPERAZIONE O WHERE utente = " + "'" + username + "'";
        String comandoSql2 = "AND proposta=" ;
        int index=0;

        if(t==1) //per avere tutto
            comandoSql2 = " ORDER BY O.data DESC";
        else if(t==2) //per avere solo operazioni da autore
            comandoSql2 += "false";
        else if(t==3) //per avere solo proposte
            comandoSql2 += "true";

        comandoSql += comandoSql2;

        try {
            PreparedStatement ps = connection.prepareStatement(comandoSql);
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

            }
            rs.close();
            ps.close();
        }
        catch (Exception e)
        {
            System.out.println("Errore: " + e.getMessage());
        }

    }

    public void getApprovazioneDB(int idOperazione, ArrayList<Object> approvazione)
    {

        String comandoSql = "SELECT * FROM APPROVAZIONE WHERE id_operazione=" + idOperazione;
        PreparedStatement ps = null;

        try {
            ps = connection.prepareStatement(comandoSql);
            ResultSet rs = ps.executeQuery();

            if(rs.next()){
                Boolean risposta;
                if(rs.getObject("risposta")==null)
                    risposta=null;
                else
                    risposta = rs.getBoolean("risposta");

                approvazione.add(rs.getString("autore"));
                approvazione.add(rs.getTimestamp("data"));
                approvazione.add(risposta);
            }

            ps.close();
            rs.close();


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }




    }


    public void approvaPropostaDB(int idProposta, String utente, Boolean risposta)
    {
        try {
            CallableStatement cs = connection.prepareCall("CALL approvaproposta(?, ?, ?)");
            cs.setInt(1, idProposta);
            cs.setBoolean(2, risposta);
            cs.setString(3, utente);
            cs.execute();
            connection.close();
        } catch (Exception e) {
            System.out.println("Errore: " + e.getMessage());
        }
    }

    /*
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
     */

    public void removeActiveProposalDB(String username, int idPagina)
    {
        String sql = "DELETE FROM OPERAZIONE AS O USING APPROVAZIONE AS A WHERE O.id_operazione=A.id_operazione AND O.id_pagina=" +idPagina+ " AND O.utente= '"+ username + "'AND A.risposta IS NULL";

        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(sql);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }



}
