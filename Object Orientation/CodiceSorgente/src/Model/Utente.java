package Model;

import java.sql.Timestamp;
import java.util.ArrayList;

public class Utente {
    private String username;
    private String email;
    private String password;
    private Timestamp dataIscrizione;
    private Boolean autore; // indica se si tratta di un autore o no
    private ArrayList<Operazione> operazioniEffettuate;
    private ArrayList<Pagina> ListaPagineCreate;
    private ArrayList<Approvazione> proposteDaApprovare;
    public Utente (String user, String mail, String pass, Timestamp data) {
        setUsername(user);
        setEmail(mail);
        setPassword(pass);
        dataIscrizione = data;
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public Timestamp getDataIscrizione() { return dataIscrizione; }

    public ArrayList<Operazione> getOperazioniEffettuate() {
        return operazioniEffettuate;
    }

    public void setOperazioniEffettuate(ArrayList<Operazione> operazioniEffettuate) {
        this.operazioniEffettuate = operazioniEffettuate;
    }

    public ArrayList<Approvazione> getProposteDaApprovare() {
        return proposteDaApprovare;
    }

    public void setProposteDaApprovare(ArrayList<Approvazione> proposteDaApprovare) {
        this.proposteDaApprovare = proposteDaApprovare;
    }

    public ArrayList<Pagina> getListaPagineCreate() {
        return ListaPagineCreate;
    }

    public void setListaPagineCreate(ArrayList<Pagina> listaPagineCreate) {
        ListaPagineCreate = listaPagineCreate;
    }

    public Boolean getAutore() {
        return autore;
    }

    public void setAutore(Boolean autore) {
        this.autore = autore;
    }



}
