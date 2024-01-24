package Controller;

import DAO.ListaPagineDAO;
import DAO.ListaUtentiDAO;
import ImplementazionePostgresDAO.ListaPagineImplementazionePostgresDAO;
import ImplementazionePostgresDAO.ListaUtentiImplementazionePostgresDAO;
import Model.*;

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
        Testo testoOriginale  = paginaOriginale.getTestoRiferito();
        Storico storico = paginaOriginale.getStorico();

        for(int riga=1; riga<=testoModificato.getNumRighe(); riga++)
        {
            ArrayList<Frase> rowOld = testoOriginale.getFrasiInRiga(riga);
            ArrayList<Frase> rowNew = testoModificato.getFrasiInRiga(riga);

            for(Frase fraseOld : rowOld)
            {
                String contenutoOld = fraseOld.getContenuto().replace("\n", "");
                contenutoOld = contenutoOld.replace("-", "");

                if(rowNew.isEmpty())
                {
                    Cancellazione cancellazione = new Cancellazione(proposta, riga, "data", utilizzatore, contenutoOld, storico, paginaOriginale);
                    storico.addOperazione(cancellazione);
                }
                else {
                    String contenutoNew = rowNew.getFirst().getContenuto().replace("\n", "");
                    contenutoNew = contenutoNew.replace("-", "");
                    rowNew.removeFirst();

                    if (!(contenutoOld.equals(contenutoNew))) {
                        Modifica modifica = new Modifica(proposta, riga, "data", utilizzatore, contenutoOld, contenutoNew, storico, paginaOriginale);
                        storico.addOperazione(modifica);
                    }
                }

            }

            if(!rowNew.isEmpty())
            {
                for(Frase f : rowNew)
                {
                    String contenuto = f.getContenuto().replace("\n", "");
                    contenuto = contenuto.replace("-", "");

                    Inserimento inserimento = new Inserimento(proposta, riga, "data", utilizzatore, contenuto, storico, paginaOriginale);
                    storico.addOperazione(inserimento);
                }
            }

        }

        if(testoOriginale.getListaFrasi().size()>testoModificato.getListaFrasi().size())
        {
            for(int riga= testoModificato.getNumRighe()+1; riga<= testoOriginale.getNumRighe(); riga++)
            {
                ArrayList<Frase> frasiEliminate = testoOriginale.getFrasiInRiga(riga);

                for(Frase frase: frasiEliminate)
                {
                    String contenuto = frase.getContenuto().replace("\n", "");
                    contenuto = contenuto.replace("-", "");

                    Cancellazione cancellazione = new Cancellazione(proposta, riga, "data", utilizzatore, contenuto, storico, paginaOriginale);
                    storico.addOperazione(cancellazione);
                }

            }
        }

        paginaOriginale.setTestoRiferito(testoModificato);
        //modifica pagina nel database (da fare)
        paginaOriginale.getStorico().stampaOperazioni();

    }


    public void creazionePagina(String titolo, String testo)
    {
        Pagina p = new Pagina(titolo, utilizzatore, testo); //creo la pagina

        ListaPagineDAO listaPagineDAO = new ListaPagineImplementazionePostgresDAO();
        listaPagineDAO.addPaginaDB(p.getTitolo(), p.getDataCreazione(), p.getAutore());

        int idPagina = listaPagineDAO.recuperaIdPagina(); //da sostituire con getPaginaDB (da creare)

        listaPagineDAO.addTextDB(idPagina, p.getTestoRiferito().getListaFrasi());
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
