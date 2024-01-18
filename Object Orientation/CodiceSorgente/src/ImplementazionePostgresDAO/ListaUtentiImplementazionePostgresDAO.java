package ImplementazionePostgresDAO;

import DAO.ListaUtentiDAO;
import Database.ConnessioneDatabase;
import Model.Utente;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ListaUtentiImplementazionePostgresDAO implements ListaUtentiDAO {
    private Connection connection;

    public ListaUtentiImplementazionePostgresDAO() {
        try {
            connection = ConnessioneDatabase.getInstance().getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addUtenteDB(String username, String email, String password) {
        try {
            PreparedStatement addUtentePS = connection.prepareStatement("INSERT INTO \"utente\" " + "(\"username\", \"email\", \"password\")"
                    + "VALUES ('"+username+"', '"+email+"', + '"+password+"');");
            addUtentePS.executeUpdate();
            connection.close();
        } catch (Exception e) {
            System.out.println("Errore: " + e.getMessage());
        }
    }

    @Override
    public boolean verificaUsernameDB(String username) {
        try {
            PreparedStatement queryControllo = connection.prepareStatement("SELECT * from utente where username = '" + username + "'");
            ResultSet rs = queryControllo.executeQuery();

            if(rs.next())
            {
                return true;
            }

            connection.close();

        } catch (Exception e)
        {
            System.out.println("Errore: " + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean verificaPasswordDB(String username, String password) {
        try {
            PreparedStatement queryControllo = connection.prepareStatement("SELECT * from utente where username = '" + username + "' AND password = '" + password + "'");
            ResultSet rs = queryControllo.executeQuery();
            if(rs.next())
            {
                return true;
            }

            connection.close();

        } catch (Exception e)
        {
            System.out.println("Errore: " + e.getMessage());
        }
        return false;
    }

    @Override
    public Utente getUtenteDB(String username) {
        try {
            PreparedStatement queryControllo = connection.prepareStatement("SELECT * from utente where username = '" + username + "'");
            ResultSet rs = queryControllo.executeQuery();

            if(rs.next())
            {
                String email = rs.getString("email");
                String password = rs.getString("password");
                Utente u = new Utente(username, email, password);
                return u;
            }

            connection.close();

        } catch (Exception e)
        {
            System.out.println("Errore: " + e.getMessage());
        }
        return null;
    }
}
