package DAO;

public interface ListaUtentiDAO {
    void addUtenteDB(String username, String email, String password);
    boolean verificaUsernameDB(String username);
    boolean verificaPasswordDB(String username, String password);
}
