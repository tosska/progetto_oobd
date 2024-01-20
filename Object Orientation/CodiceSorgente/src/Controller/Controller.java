package Controller;

import DAO.ListaPagineDAO;
import DAO.ListaUtentiDAO;
import ImplementazionePostgresDAO.ListaPagineImplementazionePostgresDAO;
import ImplementazionePostgresDAO.ListaUtentiImplementazionePostgresDAO;
import Model.*;

import java.sql.Timestamp;
import java.util.ArrayList;

public class Controller {

    public ArrayList<Utente> ListaUtenti;
    public Utente utilizzatore;
    public ArrayList<Pagina> pagineCreate;

    public Controller()
    {
        ListaUtenti = new ArrayList<Utente>();
    }

    public void caricaPagineCreate()
    {
        ListaPagineDAO l = new ListaPagineImplementazionePostgresDAO();
        pagineCreate = l.getPagineCreateDB(utilizzatore);
        stampaPagineCreate();
    }

    public void caricaStoricoDaPagina(Pagina pagina)
    {
        ListaPagineDAO l = new ListaPagineImplementazionePostgresDAO();
        Storico s = l.getStoricoDB(pagina);
        pagina.setStorico(s);
    }

    public void caricaModifichePagina(Pagina paginaOriginale, Testo testoModificato, Boolean proposta)
    {
        Testo testoOriginale = paginaOriginale.getTestoRiferito();
        int sizeTestoOriginale = testoOriginale.getListaFrasi().size(); //2
        int sizeTestoModificato = testoModificato.getListaFrasi().size();//1
        int limite;
        String frasePrecedente;

        if(sizeTestoOriginale<=sizeTestoModificato)
            limite = sizeTestoOriginale;
        else
            limite = sizeTestoModificato;

        for(int i=0; i<limite; i++)
        {
            Frase frase1 = testoOriginale.getListaFrasi().get(i);
            Frase frase2 = testoModificato.getListaFrasi().get(i);

            if(!(frase1.getContenuto().equals(frase2.getContenuto()))) //se non sono uguali
            {
                Modifica modifica = new Modifica(proposta, frase1.getRiga(), "data", utilizzatore, frase1.getContenuto(), frase2.getContenuto(),
                        paginaOriginale.getStorico(), paginaOriginale);

                paginaOriginale.getStorico().addOperazione(modifica);
            }
        }

        if(sizeTestoOriginale<sizeTestoModificato) //è avvenuta una o più aggiunte
        {
            for(int i=sizeTestoOriginale; i<sizeTestoModificato; i++)
            {
                Frase fraseAggiunta = testoModificato.getListaFrasi().get(i);

                Inserimento inserimento = new Inserimento(proposta, fraseAggiunta.getRiga(), "data", utilizzatore, fraseAggiunta.getContenuto(),
                        paginaOriginale.getStorico(), paginaOriginale);
                paginaOriginale.getStorico().addOperazione(inserimento);
            }
        }
        else if(sizeTestoOriginale>sizeTestoModificato) //è avvenuta una o più cancellazioni
        {
            for(int i=sizeTestoModificato; i<sizeTestoOriginale; i++)
            {
                Frase fraseEliminata = testoOriginale.getListaFrasi().get(i);

                Cancellazione cancellazione = new Cancellazione(proposta, fraseEliminata.getRiga(), "data", utilizzatore, fraseEliminata.getContenuto(),
                        paginaOriginale.getStorico(), paginaOriginale);
                paginaOriginale.getStorico().addOperazione(cancellazione);
            }
        }

        paginaOriginale.setTestoRiferito(testoModificato);
        System.out.println("------------------------------------------------------------"); //debug
        paginaOriginale.getStorico().stampaOperazioni();
    }


    public void creazionePagina(String titolo, String testo)
    {
        Pagina p = new Pagina(titolo, utilizzatore, testo); //creo la pagina

        ListaPagineDAO listaPagineDAO = new ListaPagineImplementazionePostgresDAO();
        listaPagineDAO.addPaginaDB(p.getTitolo(), p.getDataCreazione(), p.getAutore());

        int idPagina = listaPagineDAO.recuperaIdPagina(); //da sostituire con getPaginaDB (da creare)

        listaPagineDAO.addFraseDB(idPagina, p.getTestoRiferito().getListaFrasi());
    }

    public void aggiungiUtente(String username, String email, String password, Timestamp data)
    {
        ListaUtentiDAO l = new ListaUtentiImplementazionePostgresDAO();
        l.addUtenteDB(username, email, password, data);   // scrive sul DB
    }

    public boolean modificaUsername(String oldUsername, String newUsername)
    {
        ListaUtentiDAO l = new ListaUtentiImplementazionePostgresDAO();
        boolean result = l.modificaUsernameDB(oldUsername, newUsername);
        return result;
    }

    public boolean modificaEmail(String oldEmail, String newEmail)
    {
        ListaUtentiDAO l = new ListaUtentiImplementazionePostgresDAO();
        boolean result = l.modificaEmailDB(oldEmail, newEmail);
        return result;
    }

    public boolean eliminaAccount(String username)
    {
        ListaUtentiDAO l = new ListaUtentiImplementazionePostgresDAO();
        boolean result = l.eliminaAccountDB(username);
        return result;
    }

    public boolean modificaPassword(String oldPassword, String newPassword)
    {
        ListaUtentiDAO l = new ListaUtentiImplementazionePostgresDAO();
        boolean result = l.modificaPasswordDB(oldPassword, newPassword);
        return result;
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

    public void impostaUtilizzatore(String username)
    {
        ListaUtentiDAO l = new ListaUtentiImplementazionePostgresDAO();
        utilizzatore = l.getUtenteDB(username);
    }

    public void stampaPagineCreate() //debug
    {
        for(Pagina p : pagineCreate)
            System.out.println(p.getTitolo());
    }

    public Pagina cercaPagina(String titolo)
    {
        ListaPagineDAO l = new ListaPagineImplementazionePostgresDAO();
        return l.cercaPaginaDB(titolo);
    }


}
