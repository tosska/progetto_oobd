package Model;

import java.util.ArrayList;

public class Utente {
    private String username;
    private String email;
    private String password;
    private Boolean autore; // indica se si tratta di un autore o no
    private ArrayList<Operazione> operazioniEffettuate;
    private ArrayList<Pagina> ListaPagineCreate;
    private ArrayList<Approvazione> notifiche;

    public Utente (String user, String mail, String pass) {
        setUsername(user);
        setEmail(mail);
        setPassword(pass);
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    //da capire se cancellare o modificare
    /*public void creaPagina(String titolo, String data, Testo testo, Storico storico) {
        ListaPagineCreate.add(new Pagina(titolo, data, this));
    }*/


}
