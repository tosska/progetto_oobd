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
        ListaOperazioneDAO l= new ListaOperazioneImplementazionePostgresDAO();
        proposteDaApprovare= l.getProposteDaApprovareDB(utilizzatore);
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
    }


    public void creazionePagina(String titolo, String testo)
    {
        Pagina p = new Pagina(titolo, utilizzatore, testo); //creo la pagina

        ListaPagineDAO listaPagineDAO = new ListaPagineImplementazionePostgresDAO();
        listaPagineDAO.addPaginaDB(p.getTitolo(), p.getDataCreazione(), p.getAutore().getUsername());

        int idPagina = listaPagineDAO.recuperaIdPagina(); //da sostituire con getPaginaDB (da creare)

        listaPagineDAO.addTextDB(idPagina, p.getTestoRiferito().getListaFrasi(), utilizzatore);
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
        Pagina antem = new Pagina(temp.getPagina().getId(), temp.getPagina().getTitolo(), temp.getPagina().getTestoRiferito(),  //da valutare se ha senso il costruttore
                temp.getData(), temp.getUtente());

        for(Operazione proposta : proposteDaApprovare)
        {
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
                antem = new Pagina(temp.getPagina().getId(), temp.getPagina().getTitolo(), temp.getPagina().getTestoRiferito(), //da valutare se ha senso il costruttore
                        temp.getData(), proposta.getUtente());

            }

        }

        return anteprime;
    }
    private void inserisciProposta(Pagina pagina, Operazione proposta)
    {
        if(proposta instanceof Inserimento)
            pagina.getTestoRiferito().inserisciFrase(proposta.getFraseCoinvolta());
        else if (proposta instanceof Modifica) {
            pagina.getTestoRiferito().modificaFrase(proposta.getFraseCoinvolta(), ((Modifica) proposta).getFraseModificata());
        }
        else if (proposta instanceof Cancellazione) {
            pagina.getTestoRiferito().cancellaFrase(proposta.getFraseCoinvolta());
        }
    }



    public void approvaProposta(Operazione proposta, Boolean risposta)
    {
        ListaOperazioneDAO l= new ListaOperazioneImplementazionePostgresDAO();
        l.approvaPropostaDB(proposta, utilizzatore, risposta);
    }



}
