package Controller;

import Model.Pagina;
import Model.Utente;

import java.util.ArrayList;

public class Controller {

    public Utente utilizzatore;
    public ArrayList<Pagina> pagine = new ArrayList<>();

    public void creazionePagina(String titolo, String testo)
    {
        utilizzatore = new Utente("dd", "aa", "ddsa");
        Pagina p = new Pagina(titolo, "data", utilizzatore, testo);
    }
}
