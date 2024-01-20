package DAO;

import Model.Utente;

import java.sql.Timestamp;

public interface ListaUtentiDAO {
    void addUtenteDB(String username, String email, String password, Timestamp data);
    boolean verificaUsernameDB(String username);
    boolean verificaPasswordDB(String username, String password);
    boolean modificaUsernameDB(String oldUsername, String newUsername);
    boolean modificaEmailDB(String oldUsername, String newUsername);
    boolean modificaPasswordDB(String oldPassword, String newPassword);
    boolean eliminaAccountDB(String username);

    Utente getUtenteDB(String username);
}
