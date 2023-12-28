package Model;

import java.util.ArrayList;

public class Autore extends Utente {
    private ArrayList<Pagina> ListaPagineCreate;
    private ArrayList<Approvazione> notifiche;
    public Autore (String user, String mail, String pass) {
        super(user, mail, pass);
    }
    public void creaPagina(String titolo, String data, Testo testo, Storico storico) {
        ListaPagineCreate.add(new Pagina(titolo, data, this, testo, storico));
    }
}
