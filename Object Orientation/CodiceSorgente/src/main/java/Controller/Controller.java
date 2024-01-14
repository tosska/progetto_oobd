package Controller;

import DAO.ListaPagineDAO;
import DAO.ListaUtentiDAO;
import ImplementazionePostgresDAO.ListaPagineImplementazionePostgresDAO;
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

    public void creazionePagina(String titolo, String testo, String usernameAutore)
    {
        // utilizzatore = new Utente("dd", "aa", "ddsa");
        // Pagina p = new Pagina(titolo, "data", utilizzatore, testo);


        ListaUtentiDAO listaUtentiDAO = new ListaUtentiImplementazionePostgresDAO();
        Utente autore = listaUtentiDAO.cercaAutoreDB(usernameAutore);
        Pagina p = new Pagina(titolo, autore, testo);

        ListaPagineDAO listaPagineDAO = new ListaPagineImplementazionePostgresDAO();
        listaPagineDAO.addPaginaDB(p.getTitolo(), p.getDataCreazione(), p.getAutore());

        int idPagina = listaPagineDAO.recuperaIdPagina();

        listaPagineDAO.addFraseDB(idPagina, p.getTestoRiferito().getListaFrasi());
    }

    public void aggiungiUtente(String username, String email, String password)
    {
        ListaUtentiDAO l = new ListaUtentiImplementazionePostgresDAO();
        l.addUtenteDB(username, email, password);   // scrive sul DB
    }

    public boolean verificaUsername(String username)
    {
        ListaUtentiDAO l = new ListaUtentiImplementazionePostgresDAO();
        boolean result = l.verificaUsernameDB(username);
        return result;
    }

    public boolean verificaPassword(String username, String password)
    {
        ListaUtentiDAO l = new ListaUtentiImplementazionePostgresDAO();
        boolean result = l.verificaPasswordDB(username, password);
        return result;
    }


}
