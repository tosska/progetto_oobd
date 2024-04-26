package DAO;


import java.sql.Timestamp;
import java.util.ArrayList;

public interface UtenteDAO {
    void addUtenteDB(String username, String email, String password, Timestamp data);
    boolean verificaUsernameDB(String username);
    boolean verificaPasswordDB(String username, String password);
    boolean modificaUsernameDB(String oldUsername, String newUsername);
    boolean modificaEmailDB(String oldUsername, String newUsername);
    boolean modificaPasswordDB(String oldPassword, String newPassword);
    boolean eliminaAccountDB(String username);

    void getUtenteDB(String username, ArrayList<String> utente);
}
