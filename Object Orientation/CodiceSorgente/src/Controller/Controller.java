package Controller;

import DAO.ListaOperazioneDAO;
import DAO.ListaPagineDAO;
import DAO.ListaUtentiDAO;
import ImplementazionePostgresDAO.ListaOperazioneImplementazionePostgresDAO;
import ImplementazionePostgresDAO.ListaPagineImplementazionePostgresDAO;
import ImplementazionePostgresDAO.ListaUtentiImplementazionePostgresDAO;
import Model.*;

import java.sql.Timestamp;
import java.util.ArrayList;

public class Controller {

    public ArrayList<Utente> ListaUtenti;
    public Utente utilizzatore;
    public ArrayList<Pagina> pagineCreate;
    public ArrayList<Operazione> proposteDaApprovare;
    public ArrayList<Operazione> storicoOperazioniUtente;
    public Pagina paginaAperta;
    public Pagina paginaPrecedente;
    public Frase fraseSelezionata;
    public ArrayList<Pagina> anteprime;

    public ArrayList<Operazione> operazioniModifica;

    public Pagina anteprimaModifica;


    //creare un array pagine caricate
    public Controller()
    {
        ListaUtenti = new ArrayList<Utente>();
    }

    public boolean checkAutore()
    {
        if(utilizzatore.getUsername().equals(paginaAperta.getAutore().getUsername()))
            return true;
        else
            return false;
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
        storicoOperazioniUtente = l.getOperazioniDB(utilizzatore, 1);
    }

    public void caricaStoricoDaPagina(Pagina pagina)
    {
        ListaPagineDAO l = new ListaPagineImplementazionePostgresDAO();
        Storico s = l.getStoricoDB(pagina);
        pagina.setStorico(s);
    }

    public Testo getTestoConProposte()
    {

        ArrayList<Operazione> listaProposte = getProposteUtenteUP(paginaAperta, utilizzatore);

        if(listaProposte==null)
            return null;

        Testo clone = paginaAperta.getTestoRiferito().clonaTesto();

        for(Operazione op : listaProposte)
        {
            Frase frase = new Frase(op.getFraseCoinvolta().getRiga(), op.getFraseCoinvolta().getOrdine(), op.getFraseCoinvolta().getContenuto(), clone);

            // e se mandassimo riga, ordine e contenuto senza creare l'oggetto Frase?
            if(op instanceof Inserimento)
                clone.inserisciFrase(frase, false);
            else if (op instanceof Modifica) {
                frase.setContenuto(((Modifica) op).getFraseModificata().getContenuto());
                clone.modificaFrase(frase, false);
            } else if (op instanceof Cancellazione) {
                clone.cancellaFrase(frase, false);

            }
        }

        return clone;

    }

    public void caricamentoAnteprimaModifica()
    {
        anteprimaModifica = creazioneAnteprimaPagina(paginaAperta, paginaAperta.getDataCreazione(), paginaAperta.getAutore(), paginaAperta.getTema());
        operazioniModifica = new ArrayList<>();
    }

    public void inserisciFrasePostCreazione(String frase, int riga, int ordine)
    {
        Boolean proposta = !checkAutore();
        Frase fraseI = new Frase(riga, ordine, frase, anteprimaModifica.getTestoRiferito());


        anteprimaModifica.getTestoRiferito().inserisciFrase(fraseI, true);
        anteprimaModifica.getTestoRiferito().aggiorna();

        //storico deve essere opzionale ? quindi non va messa nel costruttore?
        Inserimento inserimento = new Inserimento(proposta, fraseI, new Timestamp(System.currentTimeMillis()), utilizzatore, paginaAperta.getStorico(), paginaAperta);
        operazioniModifica.add(inserimento);
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


    public void creazionePagina(String titolo, String testo, Tema tema)
    {
        Pagina p = new Pagina(titolo, utilizzatore, testo, tema); //creo la pagina

        //aggiungo la pagina creata nell'array delle pagine create dall'utilizzatore
        aggiornaPagineCreate(p);

        //la memorizzo nel database
        ListaPagineDAO listaPagineDAO = new ListaPagineImplementazionePostgresDAO();
        listaPagineDAO.addPaginaDB(p.getTitolo(), p.getDataCreazione(), p.getAutore().getUsername(), tema.getIdTema());

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

    public void creaTema(String tema)
    {
        ListaPagineDAO l = new ListaPagineImplementazionePostgresDAO();
        l.addTemaDB(tema);   // scrive sul DB
    }

    public ArrayList<Tema> generaListaTemi()
    {
        ArrayList<Tema> listaTemi;
        ListaPagineDAO l = new ListaPagineImplementazionePostgresDAO();
        listaTemi = l.raccogliTemi();   // scrive sul DB

        return listaTemi;
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

    public Pagina searchPageById(int id)
    {
        ListaPagineDAO l = new ListaPagineImplementazionePostgresDAO();
        return l.getPaginaByIdDB(id);
    }


    public void creaAnteprime()
    {
        ArrayList<Pagina> anteprime = new ArrayList<>();


        Operazione temp = proposteDaApprovare.getFirst();
        Pagina antem = creazioneAnteprimaPagina(temp.getPagina(), temp.getData(), temp.getUtente(), temp.getPagina().getTema());

        for(Operazione proposta : proposteDaApprovare)
        {

            if(temp.getPagina().getId() == proposta.getPagina().getId() && temp.getUtente().getUsername().equals(proposta.getUtente().getUsername()))
            {
                inserisciProposta(antem, proposta);
            }
            else
            {
                anteprime.add(antem);
                temp = proposta;
                antem = creazioneAnteprimaPagina(temp.getPagina(), temp.getData(), temp.getUtente(), temp.getPagina().getTema());

            }
        }

        if(!proposteDaApprovare.isEmpty())
            anteprime.add(antem);





        this.anteprime = anteprime;
    }

    private Pagina creazioneAnteprimaPagina(Pagina p, Timestamp data, Utente utente, Tema tema)
    {
        Pagina antem = new Pagina(p.getId(), p.getTitolo(), null, //da valutare se ha senso il costruttore
                data, utente, p.getTema());

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

    public ArrayList<Operazione> getProposteUtenteUP(Pagina pagina, Utente utente)
    {
        ArrayList<Operazione> listaProposte;

        ListaOperazioneDAO l= new ListaOperazioneImplementazionePostgresDAO();
        listaProposte = l.getProposteUP_DB(pagina, utente);

        return listaProposte;
    }

    public int getNumOccurences(String line, char occurence)
    {
        int num=0;

        for(int i=0; i<line.length(); i++)
        {
            if(line.charAt(i) == occurence)
                num++;
        }

        return num;
    }

    public void insertLink(Pagina pagina, int riga, int ordine, Pagina paginaLink, Utente utente)
    {
        ListaPagineDAO l= new ListaPagineImplementazionePostgresDAO();
        l.insertLinkDB(pagina, riga, ordine, paginaLink, utente);
    }

    public void removeLink(Pagina pagina, int riga, int ordine, Utente utente)  {

        ListaPagineDAO l= new ListaPagineImplementazionePostgresDAO();
        l.removeLinkDB(pagina, riga, ordine, utente);
    }

    public void selezionaFrase(int posizione, boolean anteprima)
    {
        int riga=0;
        int ordine=0;
        String testo;
        Pagina versionePagina;

        if(!anteprima)
            versionePagina = paginaAperta;
        else{
            versionePagina = anteprimaModifica;
        }

        testo = versionePagina.getTestoString();
        System.out.println(posizione);

        if(testo.charAt(posizione)!='\n')
        {
            int index1 = testo.indexOf(".", posizione);
            int index2 = testo.indexOf("\n", posizione);
            String subTesto;

            if(index1<index2 || index2==-1)
                subTesto= testo.substring(0,index1);
            else
                subTesto= testo.substring(0,index2);

            riga = getNumOccurences(subTesto, '\n')+1;
            String[] testoDiviso = subTesto.split("\n");
            String rigaSelezionata = testoDiviso[testoDiviso.length-1];
            ordine = getNumOccurences(rigaSelezionata, '.')+1;
        }

        if(riga!=0 && ordine!=0)
        {
            fraseSelezionata = versionePagina.getTestoRiferito().getFrase(riga, ordine);
        }
        else
            fraseSelezionata = null;

    }

    public boolean PhraseIsLink()
    {
        //forse il controllo va fatto con il polimorfismo: fare un metodo isLink in frase che viene poi sovrascritto in collegamento
        return fraseSelezionata instanceof Collegamento;
    }

    public boolean PhraseIsSelected()
    {
        return fraseSelezionata != null;
    }

    public int getRowSelectedPhrase()
    {
        return fraseSelezionata.getRiga();
    }

    public int getOrderSelectedPhrase()
    {
        return fraseSelezionata.getOrdine();
    }

    //da vedere meglio
    public void attivazioneCollegamento()  {
        if(fraseSelezionata instanceof Collegamento)
        {
            paginaPrecedente = paginaAperta;
            paginaAperta = ((Collegamento) fraseSelezionata).getPaginaCollegata();
        }

    }



    public ArrayList<String> getFrasiPaginaAperta()
    {
        return paginaAperta.getTestoRiferito().getFrasiString();
    }


}
