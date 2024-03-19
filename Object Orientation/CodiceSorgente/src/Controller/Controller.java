package Controller;

import DAO.ListaOperazioneDAO;
import DAO.ListaPagineDAO;
import DAO.ListaUtentiDAO;
import ImplementazionePostgresDAO.ListaOperazioneImplementazionePostgresDAO;
import ImplementazionePostgresDAO.ListaPagineImplementazionePostgresDAO;
import ImplementazionePostgresDAO.ListaUtentiImplementazionePostgresDAO;
import Model.*;

import java.sql.CallableStatement;
import java.sql.Timestamp;
import java.util.ArrayList;

public class Controller {

    public ArrayList<Utente> ListaUtenti;
    public Utente utilizzatore;
    public ArrayList<Pagina> pagineCreate;

    public ArrayList<Operazione> proposteDaApprovare;
    public ArrayList<Operazione> storicoOperazioniUtente;

    public Pagina paginaAperta;


    //creare un array pagine caricate

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

    public void caricaProposteDaApprovare()
    {
        if(proposteDaApprovare!=null && !proposteDaApprovare.isEmpty())
            proposteDaApprovare.clear();

        ListaOperazioneDAO l= new ListaOperazioneImplementazionePostgresDAO();
        proposteDaApprovare= l.getProposteDaApprovareDB(pagineCreate, utilizzatore);
    }

    public void caricaStoricoOperazioniUtente()
    {
        ListaOperazioneDAO l= new ListaOperazioneImplementazionePostgresDAO();
        storicoOperazioniUtente = l.getOperazioniDB(utilizzatore);
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
        ArrayList<Operazione> listaOperazioni = new ArrayList<>();

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
                    Cancellazione cancellazione = new Cancellazione(proposta,  fraseOld, new Timestamp(System.currentTimeMillis()), utilizzatore, paginaOriginale.getStorico(), paginaOriginale);
                    listaOperazioni.add(cancellazione);
                }
                else {
                    String contenutoNew = rowNew.getFirst().getContenuto().replace("\n", "");
                    Frase fraseNew = rowNew.getFirst();
                    rowNew.removeFirst();

                    if (!(contenutoOld.equals(contenutoNew))) {
                        Modifica modifica = new Modifica(proposta, fraseOld, fraseNew, new Timestamp(System.currentTimeMillis()), utilizzatore,  paginaOriginale.getStorico(), paginaOriginale);
                        listaOperazioni.add(modifica);
                    }
                }
            }

            if(!rowNew.isEmpty())
            {
                for(Frase f : rowNew)
                {
                    String contenuto = f.getContenuto().replace("\n", "");
                    contenuto = contenuto.replace("-", "");

                    Inserimento inserimento = new Inserimento(proposta,  f, new Timestamp(System.currentTimeMillis()), utilizzatore, paginaOriginale.getStorico(), paginaOriginale);
                    listaOperazioni.add(inserimento);
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

                    Cancellazione cancellazione = new Cancellazione(proposta, frase, new Timestamp(System.currentTimeMillis()), utilizzatore, paginaOriginale.getStorico(), paginaOriginale);
                    listaOperazioni.add(cancellazione);

                }

            }
        }

        //paginaOriginale.setTestoRiferito(testoModificato);
        //modifica pagina nel database (da fare)
        //paginaOriginale.getStorico().stampaOperazioni();
        ListaPagineDAO l = new ListaPagineImplementazionePostgresDAO();
        l.editPageDB(paginaOriginale, listaOperazioni);

        if(!proposta)
        {
            //recupero la pagina modificata dal db
            Pagina paginaPostModifica = l.getPaginaByIdDB(paginaOriginale.getId());
            aggiornaPagineCreate(paginaPostModifica);
        }


    }

    private void aggiornaPagineCreate(Pagina pagina)
    {
        boolean trovato=false;

        for(int i=0; i<pagineCreate.size() && !trovato; i++)
        {
            if(pagineCreate.get(i).getId() == pagina.getId()) {
                pagineCreate.set(i, pagina);
                trovato = true;
            }
        }

        if(!trovato)
        {
            pagineCreate.add(pagina);
        }

    }


    public void creazionePagina(String titolo, String testo)
    {
        Pagina p = new Pagina(titolo, utilizzatore, testo); //creo la pagina

        //aggiungo la pagina creata nell'array delle pagine create dall'utilizzatore
        aggiornaPagineCreate(p);

        //la memorizzo nel database
        ListaPagineDAO listaPagineDAO = new ListaPagineImplementazionePostgresDAO();
        listaPagineDAO.addPaginaDB(p.getTitolo(), p.getDataCreazione(), p.getAutore().getUsername());

        int idPagina = listaPagineDAO.recuperaIdPagina(); //da sostituire con getPaginaDB (da creare)

        listaPagineDAO.addTextDB(idPagina, p.getTestoRiferito().getListaFrasi(), utilizzatore);


    }

    /*restituisce la pagina con id mandato come parametro, presente nell'array list "pagineCreate" ossia le pagine
     create dall'utilizzatore del programma */
    public Pagina getPaginaUtilizzatore(int id)
    {
        for(Pagina p : pagineCreate)
        {
            if(p.getId() == id)
                return p;
        }

        //forse va mandata come eccezione
        return null;
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


    public ArrayList<Pagina> creaAnteprime()
    {
        ArrayList<Pagina> anteprime = new ArrayList<>();


        Operazione temp = proposteDaApprovare.getFirst();
        Pagina antem = creazioneAnteprimaPagina(temp.getPagina(), temp.getData(), temp.getUtente());

        for(Operazione proposta : proposteDaApprovare)
        {
            //togliamo dal timestamp i millisecondi
            String s1 = temp.getData().toString().split("\\.")[0];
            String s2 = proposta.getData().toString().split("\\.")[0];

            if(s1.equals(s2) && temp.getPagina().getId() == proposta.getPagina().getId() && temp.getUtente().getUsername().equals(proposta.getUtente().getUsername()))
            {
                inserisciProposta(antem, proposta);
            }
            else
            {
                anteprime.add(antem);
                temp = proposta;
                antem = creazioneAnteprimaPagina(temp.getPagina(), temp.getData(), temp.getUtente());

            }
        }

        if(!proposteDaApprovare.isEmpty())
            anteprime.add(antem);





        return anteprime;
    }

    private Pagina creazioneAnteprimaPagina(Pagina p, Timestamp data, Utente utente)
    {
        Pagina antem = new Pagina(p.getId(), p.getTitolo(), null, //da valutare se ha senso il costruttore
                data, utente);

        Testo testo = p.getTestoRiferito().clonaTesto();
        testo.setPaginaRiferita(antem);
        antem.setTestoRiferito(testo);

        return antem;
    }

    private void inserisciProposta(Pagina pagina, Operazione proposta)
    {
        if(proposta instanceof Inserimento) {
            Frase frase = new Frase(proposta.getFraseCoinvolta().getRiga(), proposta.getFraseCoinvolta().getOrdine(), proposta.getFraseCoinvolta().getContenuto(), pagina.getTestoRiferito());
            pagina.getTestoRiferito().inserisciFrase(frase, true);
        }
        else if (proposta instanceof Modifica) {
            Frase frase = ((Modifica) proposta).getFraseModificata();
            frase = new Frase(frase.getRiga(), frase.getOrdine(), frase.getContenuto(), pagina.getTestoRiferito());
            pagina.getTestoRiferito().modificaFrase(frase, true);
        }
        else if (proposta instanceof Cancellazione) {
            Frase frase = new Frase(proposta.getFraseCoinvolta().getRiga(), proposta.getFraseCoinvolta().getOrdine(), proposta.getFraseCoinvolta().getContenuto(), pagina.getTestoRiferito());
            pagina.getTestoRiferito().cancellaFrase(frase, true);
        }
    }



    public void approvaProposta(Operazione proposta, Boolean risposta)
    {
        ListaOperazioneDAO l= new ListaOperazioneImplementazionePostgresDAO();
        l.approvaPropostaDB(proposta, utilizzatore, risposta);
    }



}
