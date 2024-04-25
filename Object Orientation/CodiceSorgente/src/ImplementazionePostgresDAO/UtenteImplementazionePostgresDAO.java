package ImplementazionePostgresDAO;

import DAO.UtenteDAO;
import Database.ConnessioneDatabase;
import Model.Utente;

import java.sql.*;

public class UtenteImplementazionePostgresDAO implements UtenteDAO {
    private Connection connection;

    public UtenteImplementazionePostgresDAO() {
        try {
            connection = ConnessioneDatabase.getInstance().getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addUtenteDB(String username, String email, String password, Timestamp data) {
        try {
            PreparedStatement addUtentePS = connection.prepareStatement("INSERT INTO \"utente\" " + "(\"username\", \"email\", \"password\", \"dataiscrizione\")"
                    + "VALUES ('"+username+"', '"+email+"', '"+password+"', '"+data+"');");
            addUtentePS.executeUpdate();
            connection.close();
        } catch (Exception e) {
            System.out.println("Errore: " + e.getMessage());
        }
    }

    @Override
    public boolean modificaEmailDB(String oldEmail, String newEmail) {
        try {
            PreparedStatement queryControllo = connection.prepareStatement("UPDATE utente SET email = '" + newEmail + "'"
                    + " WHERE email = '" +oldEmail+ "'");
            queryControllo.executeUpdate();

            connection.close();

        } catch (Exception e)
        {
            System.out.println("Errore: " + e.getMessage());
            return false;
        }
        return true;
    }

    @Override
    public boolean modificaUsernameDB(String oldUsername, String newUsername)
    {
        try {
            PreparedStatement queryControllo = connection.prepareStatement("UPDATE utente SET username = '" + newUsername + "'"
                    + " WHERE username = '" +oldUsername+ "'");
            queryControllo.executeUpdate();

            connection.close();

        } catch (Exception e)
        {
            System.out.println("Errore: " + e.getMessage());
            return false;
        }
        return true;
    }

    @Override
    public boolean modificaPasswordDB(String oldPassword, String newPassword) {
        try {
            PreparedStatement queryControllo = connection.prepareStatement("UPDATE utente SET password = '" + newPassword + "'"
                    + " WHERE password = '" +oldPassword+ "'");
            queryControllo.executeUpdate();

            connection.close();

        } catch (Exception e)
        {
            System.out.println("Errore: " + e.getMessage());
            return false;
        }
        return true;
    }

    @Override
    public boolean eliminaAccountDB(String username) {
        try {
            PreparedStatement queryControllo = connection.prepareStatement("DELETE FROM utente WHERE username = '" + username + "'");
            queryControllo.executeUpdate();

            connection.close();

        } catch (Exception e)
        {
            System.out.println("Errore: " + e.getMessage());
            return false;
        }
        return true;
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
                Timestamp data = rs.getTimestamp("dataiscrizione");
                Utente u = new Utente(username, email, password, data);
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
