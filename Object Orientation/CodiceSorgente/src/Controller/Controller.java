package Controller;

import DAO.OperazioneDAO;
import DAO.PaginaDAO;
import DAO.UtenteDAO;
import ImplementazionePostgresDAO.OperazioneImplementazionePostgresDAO;
import ImplementazionePostgresDAO.PaginaImplementazionePostgresDAO;
import ImplementazionePostgresDAO.UtenteImplementazionePostgresDAO;
import Model.*;

import java.sql.Timestamp;
import java.util.ArrayList;

public class Controller {
    public Utente utilizzatore;
    public ArrayList<Pagina> pagineCreate;
    public ArrayList<Operazione> proposteDaApprovare;
    public ArrayList<Operazione> storicoOperazioniUtente;
    public Pagina paginaAperta;
    public Pagina paginaPrecedente;
    public Frase fraseSelezionata;
    public ArrayList<Pagina> anteprime;
    public ArrayList<Operazione> operazioniModifica;
    public ArrayList<Tema> ListaTemi;
    public Pagina anteprimaModifica;


    //creare un array pagine caricate
    public Controller()
    {
        ListaTemi = generaListaTemi();
    }

    public boolean checkAutore()
    {
        if (utilizzatore.getUsername().equals(paginaAperta.getAutore().getUsername()))
            return true;
        else
            return false;
    }

    public void caricaPagineCreate()
    {
        PaginaDAO l = new PaginaImplementazionePostgresDAO();
        pagineCreate = l.getPagineCreateDB(utilizzatore);
        stampaPagineCreate();
    }

    public void caricaProposteDaApprovare()
    {
        if(proposteDaApprovare!=null && !proposteDaApprovare.isEmpty())
            proposteDaApprovare.clear();

        OperazioneDAO l= new OperazioneImplementazionePostgresDAO();
        proposteDaApprovare= l.getProposteDaApprovareDB(pagineCreate, utilizzatore);
    }

    public void caricaStoricoOperazioniUtente()
    {
        OperazioneDAO l= new OperazioneImplementazionePostgresDAO();
        storicoOperazioniUtente = l.getOperazioniDB(utilizzatore, 1);
    }

    public void caricaStoricoDaPagina(Pagina pagina)
    {
        PaginaDAO l = new PaginaImplementazionePostgresDAO();
        Storico s = l.getStoricoDB(pagina);
        pagina.setStorico(s);
    }

    public void removeOldActiveProposal(){
        OperazioneDAO l= new OperazioneImplementazionePostgresDAO();
        l.removeActiveProposalDB(utilizzatore, paginaAperta);
        caricaStoricoOperazioniUtente();
    }

    public boolean isActivedProposal()
    {
        for(Operazione op : storicoOperazioniUtente)
        {
            if(op.getProposta() && op.getApprovazione()!=null && op.getApprovazione().getRisposta()==null)
                return true;
        }

        return false;
    }



    public void caricamentoAnteprimaModifica()
    {
        anteprimaModifica = creazioneAnteprimaPagina(paginaAperta, paginaAperta.getDataCreazione(), paginaAperta.getAutore(), paginaAperta.getTema());
        operazioniModifica = new ArrayList<>();
    }



    public void caricaModifichePagina(String testo, Boolean proposta)
    {
        Pagina paginaOriginale = paginaAperta;
        Testo testoModificato = new Testo(paginaAperta);
        testoModificato.setTestoString(testo);

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
                    Inserimento inserimento = new Inserimento(proposta, f, new Timestamp(System.currentTimeMillis()), utilizzatore, paginaOriginale.getStorico(), paginaOriginale);
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

        PuliziaOperazioni(listaOperazioni);

        //paginaOriginale.setTestoRiferito(testoModificato);
        //modifica pagina nel database (da fare)
        //paginaOriginale.getStorico().stampaOperazioni();
        PaginaDAO l = new PaginaImplementazionePostgresDAO();
        l.editPageDB(paginaOriginale, listaOperazioni);

        if(!proposta)
        {
            //recupero la pagina modificata dal db
            Pagina paginaPostModifica = l.getPaginaByIdDB(paginaOriginale.getId());
            aggiornaPagineCreate(paginaPostModifica);
        }


    }

    private void PuliziaOperazioni(ArrayList<Operazione> listaOperazioni)
    {
        Modifica mod=null;
        Modifica precedente=null;
        int position = 0;
        for(int i=0; i<listaOperazioni.size(); i++)
        {
            if(listaOperazioni.get(i) instanceof Modifica && mod==null){
                mod = (Modifica) listaOperazioni.get(i);
                precedente = mod;
                position = i;
            } else if (listaOperazioni.get(i) instanceof Modifica && mod!=null){

                if(((Modifica) listaOperazioni.get(i)).getFraseModificata().getContenuto().equals(precedente.getFraseCoinvolta().getContenuto())) {
                    precedente = (Modifica) listaOperazioni.get(i);
                    listaOperazioni.set(i, null);
                } else if (listaOperazioni.get(i).getFraseCoinvolta().getContenuto().equals(precedente.getFraseModificata().getContenuto())){
                    precedente = (Modifica) listaOperazioni.get(i);
                    listaOperazioni.set(i, null);
                }
                else
                    mod = null;

            } else if(listaOperazioni.get(i) instanceof Inserimento && mod!=null){
                Inserimento tmp = (Inserimento) listaOperazioni.get(i);
                tmp.setFraseCoinvolta(mod.getFraseModificata());
                listaOperazioni.set(i, tmp);
                listaOperazioni.set(position, null);
                mod = null;
            } else if(listaOperazioni.get(i) instanceof Cancellazione && mod!=null){
                Cancellazione tmp = (Cancellazione) listaOperazioni.get(i);
                tmp.setFraseCoinvolta(mod.getFraseCoinvolta());
                listaOperazioni.set(i, tmp);
                listaOperazioni.set(position, null);
                mod = null;
            } else {
                mod = null;
            }
        }

        for(int i=0; i<listaOperazioni.size(); i++)
        {
            if(listaOperazioni.get(i)==null)
                listaOperazioni.remove(i);
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
        PaginaDAO paginaDAO = new PaginaImplementazionePostgresDAO();
        paginaDAO.addPaginaDB(p.getTitolo(), p.getDataCreazione(), p.getAutore().getUsername(), tema.getIdTema());

        int idPagina = paginaDAO.recuperaIdPagina(); //da sostituire con getPaginaDB (da creare)

        paginaDAO.addTextDB(idPagina, p.getTestoRiferito().getListaFrasi(), utilizzatore);


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
        PaginaDAO l = new PaginaImplementazionePostgresDAO();
        l.addTemaDB(tema);
    }

    public ArrayList<Tema> generaListaTemi()
    {
        ArrayList<Integer> listaIdTemi = new ArrayList<>();
        ArrayList<String> listaNomiTemi = new ArrayList<>();

        ArrayList<Tema> listaTemi = new ArrayList<>();
        PaginaDAO l = new PaginaImplementazionePostgresDAO();
        l.raccogliTemi(listaIdTemi, listaNomiTemi);

        for (int i = 0; i < listaIdTemi.size(); i++)
        {
            listaTemi.add(new Tema(listaIdTemi.get(i), listaNomiTemi.get(i)));
        }

        return listaTemi;
    }

    public void aggiungiUtente(String username, String email, String password, Timestamp data)
    {
        UtenteDAO l = new UtenteImplementazionePostgresDAO();
        l.addUtenteDB(username, email, password, data);   // scrive sul DB
    }

    public boolean modificaUsername(String oldUsername, String newUsername)
    {
        UtenteDAO l = new UtenteImplementazionePostgresDAO();
        boolean result = l.modificaUsernameDB(oldUsername, newUsername);
        return result;
    }

    public boolean modificaEmail(String oldEmail, String newEmail)
    {
        UtenteDAO l = new UtenteImplementazionePostgresDAO();
        boolean result = l.modificaEmailDB(oldEmail, newEmail);
        return result;
    }

    public boolean eliminaAccount(String username)
    {
        UtenteDAO l = new UtenteImplementazionePostgresDAO();
        boolean result = l.eliminaAccountDB(username);
        return result;
    }

    public boolean modificaPassword(String oldPassword, String newPassword)
    {
        UtenteDAO l = new UtenteImplementazionePostgresDAO();
        boolean result = l.modificaPasswordDB(oldPassword, newPassword);
        return result;
    }

    public boolean verificaUsername(String username)
    {
        UtenteDAO l = new UtenteImplementazionePostgresDAO();
        boolean result = l.verificaUsernameDB(username);
        return result;
    }

    public boolean verificaPassword(String username, String password)
    {
        UtenteDAO l = new UtenteImplementazionePostgresDAO();
        boolean result = l.verificaPasswordDB(username, password);
        return result;
    }

    public Utente getUtenteDB(String username)
    {
        UtenteDAO l = new UtenteImplementazionePostgresDAO();
        ArrayList<String> utenteInfo = new ArrayList<>();
        l.getUtenteDB(username, utenteInfo);

        Utente utente = new Utente(username, utenteInfo.get(0), utenteInfo.get(1), utenteInfo.get(2));
        return utente;
    }

    public void impostaUtilizzatore(String username)
    {
        utilizzatore = getUtenteDB(username);
    }

    public void stampaPagineCreate() //debug
    {
        for(Pagina p : pagineCreate)
            System.out.println(p.getTitolo());
    }

    public Pagina cercaPagina(String titolo)
    {
        PaginaDAO l = new PaginaImplementazionePostgresDAO();
        return l.cercaPaginaDB(titolo);
    }


    public void creaAnteprime()
    {
        ArrayList<Pagina> anteprime = new ArrayList<>();
        Operazione temp = proposteDaApprovare.getFirst();
        Pagina antem = creazioneAnteprimaPagina(temp.getPagina(), temp.getData(), temp.getUtente(), temp.getPagina().getTema());

        for(Operazione proposta : proposteDaApprovare) {
            if(temp.getPagina().getId() == proposta.getPagina().getId() && temp.getUtente().getUsername().equals(proposta.getUtente().getUsername())) {
                inserisciProposta(antem, proposta);
            }
            else {
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
        OperazioneDAO l= new OperazioneImplementazionePostgresDAO();
        l.approvaPropostaDB(proposta, utilizzatore, risposta);
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
        PaginaDAO l= new PaginaImplementazionePostgresDAO();
        l.insertLinkDB(pagina, riga, ordine, paginaLink, utente);
    }

    public void removeLink(Pagina pagina, int riga, int ordine, Utente utente)  {

        PaginaDAO l= new PaginaImplementazionePostgresDAO();
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
