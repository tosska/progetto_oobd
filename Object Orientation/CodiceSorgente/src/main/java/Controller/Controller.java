package Controller;

import Model.Pagina;
import Model.Utente;

import java.util.ArrayList;

public class Controller {

    public ArrayList<Utente> ListaUtenti;
    public Utente utilizzatore;
    public ArrayList<Pagina> pagine = new ArrayList<>();

    public Controller()
    {
        ListaUtenti = new ArrayList<Utente>();
    }

    public void creazionePagina(String titolo, String testo)
    {
        utilizzatore = new Utente("dd", "aa", "ddsa");
        Pagina p = new Pagina(titolo, "data", utilizzatore, testo);
    }

    public void aggiungiUtente(String username, String email, String password)
    {
        ListaUtenti.add(new Utente(username, email, password));
    }

    public boolean verificaUsername(String username)
    {
        for (Utente u : ListaUtenti)
        {
            if (u.getUsername().equals(username))
            {
                return true;
            }
        }

        return false;
    }

    public boolean verificaPassword(String username, String password)
    {
        for (Utente u : ListaUtenti)
        {
            if (u.getUsername().equals(username))
            {
                if (u.getPassword().equals(password))
                {
                    return true;
                }
            }

        }

        return false;
    }


}
