package Model;

import java.util.ArrayList;

public class Utente {
    private String username;
    private String email;
    private String password;
    private boolean autore;
    public ArrayList<Pagina>ListaPagineCreate;

    public Utente (String user, String mail, String pass) {
        setUsername(user);
        setEmail(mail);
        setPassword(pass);
        this.autore = false;    // per diventare autore bisogna creare prima una pagina
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public void creaPagina(String titolo, String data) {
        ListaPagineCreate.add(new Pagina(titolo, data, this));
        this.autore = true;
    }

}
