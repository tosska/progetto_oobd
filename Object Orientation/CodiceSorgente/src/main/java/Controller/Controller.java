package Controller;

import DAO.ListaUtentiDAO;
import ImplementazionePostgresDAO.ListaUtentiImplementazionePostgresDAO;
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
        // ListaUtenti.add(new Utente(username, email, password)); // in memoria
        ListaUtentiDAO l = new ListaUtentiImplementazionePostgresDAO();
        l.addUtenteDB(username, email, password);   // scrive sul DB
    }

    public boolean verificaUsername(String username)
    {
        /*
        for (Utente u : ListaUtenti)
        {
            if (u.getUsername().equals(username))
            {
                return true;
            }
        }

        return false;
         */

        ListaUtentiDAO l = new ListaUtentiImplementazionePostgresDAO();
        boolean result = l.verificaUsernameDB(username);
        return result;
    }

    public boolean verificaPassword(String username, String password)
    {
        /*
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
         */
        ListaUtentiDAO l = new ListaUtentiImplementazionePostgresDAO();
        boolean result = l.verificaPasswordDB(username, password);
        return result;
    }


}
