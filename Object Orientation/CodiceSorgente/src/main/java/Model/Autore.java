package Model;

import java.util.ArrayList;

public class Autore extends Utente {
    private ArrayList<Pagina> ListaPagineCreate;
    public Autore (String user, String mail, String pass) {
        super(user, mail, pass);
    }
    public void creaPagina(String titolo, String data) {
        ListaPagineCreate.add(new Pagina(titolo, data, this));
    }
}
