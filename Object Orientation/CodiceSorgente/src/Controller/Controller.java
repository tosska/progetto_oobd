package Controller;

import DAO.OperazioneDAO;
import DAO.PaginaDAO;
import DAO.UtenteDAO;
import ImplementazionePostgresDAO.OperazioneImplementazionePostgresDAO;
import ImplementazionePostgresDAO.PaginaImplementazionePostgresDAO;
import ImplementazionePostgresDAO.UtenteImplementazionePostgresDAO;
import Model.*;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class Controller {
    public Utente utilizzatore;
    public ArrayList<Pagina> pagineCreate; //va sostituito con quello dell'utilizzatore
    public ArrayList<Operazione> proposteDaApprovare;
    public ArrayList<Operazione> storicoOperazioniUtente; //va sostituito con quello dell'utilizzatore
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
        pagineCreate = new ArrayList<>();
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
        ArrayList<Integer> id = new ArrayList<>();
        ArrayList<String> titolo = new ArrayList<>();
        ArrayList<Integer> tema = new ArrayList<>();
        ArrayList<Timestamp> dataCreazione = new ArrayList<>(); 
        ArrayList<String> autore = new ArrayList<>();

        PaginaDAO l = new PaginaImplementazionePostgresDAO();
        l.getPagineCreateDB(utilizzatore.getUsername(), id, titolo, tema, dataCreazione, autore);

        for(int i=0; i<id.size(); i++){
            
            Tema t = getTemaDB(tema.get(i));
            Utente utente = getUtenteDB(autore.get(i));
            Pagina pagina = new Pagina(id.get(i), titolo.get(i), null, dataCreazione.get(i), utente, t);
            pagina.setTestoRiferito(new Testo(pagina));
            setTestoFromDB(pagina);
            pagineCreate.add(pagina);
        }
        
        stampaPagineCreate();
    }

    public void caricaProposteDaApprovare()
    {
        if(proposteDaApprovare!=null && !proposteDaApprovare.isEmpty())
            proposteDaApprovare.clear();

        OperazioneDAO l= new OperazioneImplementazionePostgresDAO();
        ArrayList<Object> proposteAstratte = new ArrayList<>();
        ArrayList<Operazione> operazioni = new ArrayList<>();

        l.getProposteDaApprovareDB(utilizzatore.getUsername(), proposteAstratte);

        for(int i=0; i<proposteAstratte.size(); i=i+9)
        {
            int id = (int) proposteAstratte.get(0); //id
            String tipo = (String) proposteAstratte.get(1);
            boolean proposta = (boolean) proposteAstratte.get(2); //proposta
            int riga = (int) proposteAstratte.get(3);//riga
            int ordine = (int) proposteAstratte.get(4); //ordine
            String fc = (String) proposteAstratte.get(5); //frase coinvolta
            String fm = (String) proposteAstratte.get(6); //frase modificata
            Timestamp data = (Timestamp) proposteAstratte.get(7);//data
            int idPagina = (int) proposteAstratte.get(8);  //id pagina

            Pagina pagina = getPaginaByIdDB(idPagina);
            Frase fraseCoinvolta = new Frase(riga, ordine, fc, pagina.getTestoRiferito());

            if(tipo.equals("I")) {

                Inserimento inserimento = new Inserimento(id, proposta, fraseCoinvolta, data, utilizzatore, null, pagina);
                operazioni.add(inserimento);
            }
            else if(tipo.equals("M"))
            {
                Frase fraseModificata = new Frase(riga, ordine, fm, pagina.getTestoRiferito());
                Modifica modifica = new Modifica(id, proposta, fraseCoinvolta, fraseModificata, data, utilizzatore, null, pagina);
                operazioni.add(modifica);

            }
            else if(tipo.equals("C"))
            {
                Cancellazione cancellazione = new Cancellazione(id, proposta, fraseCoinvolta, data, utilizzatore, null, pagina);
                operazioni.add(cancellazione);
            }

            Approvazione approvazioneProposta = new Approvazione(null, null, operazioni.getLast(), utilizzatore);
            operazioni.getLast().setApprovazione(approvazioneProposta);

        }

        proposteDaApprovare = operazioni;

    }

    public void caricaStoricoOperazioniUtente()
    {
        ArrayList<Object> operazioniAstratte = new ArrayList<>();
        ArrayList<Operazione> operazioni = new ArrayList<>();

        OperazioneDAO l= new OperazioneImplementazionePostgresDAO();
        l.getOperazioniDB(utilizzatore.getUsername(),1, operazioniAstratte);

        for(int i=0; i<operazioniAstratte.size(); i=i+9)
        {
            int id = (int) operazioniAstratte.get(0); //id
            String tipo = (String) operazioniAstratte.get(1);
            boolean proposta = (boolean) operazioniAstratte.get(2); //proposta
            int riga = (int) operazioniAstratte.get(3);//riga
            int ordine = (int) operazioniAstratte.get(4); //ordine
            String fc = (String) operazioniAstratte.get(5); //frase coinvolta
            String fm = (String) operazioniAstratte.get(6); //frase modificata
            Timestamp data = (Timestamp) operazioniAstratte.get(7);//data
            int idPagina = (int) operazioniAstratte.get(8);  //id pagina

            Pagina pagina = getPaginaByIdDB(idPagina);
            Frase fraseCoinvolta = new Frase(riga, ordine, fc, pagina.getTestoRiferito());

            if(tipo.equals("I")) {

                Inserimento inserimento = new Inserimento(id, proposta, fraseCoinvolta, data, utilizzatore, null, pagina);
                operazioni.add(inserimento);
            }
            else if(tipo.equals("M"))
            {
                Frase fraseModificata = new Frase(riga, ordine, fm, pagina.getTestoRiferito());
                Modifica modifica = new Modifica(id, proposta, fraseCoinvolta, fraseModificata, data, utilizzatore, null, pagina);
                operazioni.add(modifica);

            }
            else if(tipo.equals("C"))
            {
                Cancellazione cancellazione = new Cancellazione(id, proposta, fraseCoinvolta, data, utilizzatore, null, pagina);
                operazioni.add(cancellazione);
            }

            if(proposta){
                Approvazione approvazioneProposta = getApprovazioneDB(operazioni.getLast());
                operazioni.getLast().setApprovazione(approvazioneProposta);
            }
        }

        utilizzatore.setOperazioniEffettuate(operazioni);
    }

    public Approvazione getApprovazioneDB(Operazione operazione){
        ArrayList<Object> approvazione = new ArrayList<>();

        OperazioneDAO l = new OperazioneImplementazionePostgresDAO();
        l.getApprovazioneDB(operazione.getId(), approvazione);

        String au = (String) approvazione.get(0);
        Timestamp dataRisposta = Timestamp.valueOf( (String) approvazione.get(1));
        Boolean risposta = (Boolean) approvazione.get(2);

        Utente autore = getUtenteDB(au);

        return new Approvazione(dataRisposta, risposta, operazione, autore);
    }

    public void caricaStoricoDaPagina(Pagina pagina)
    {
        PaginaDAO l = new PaginaImplementazionePostgresDAO();
        Storico storico = new Storico(pagina);
        ArrayList<Object> operazioniAstratte = new ArrayList<>();
        ArrayList<Operazione> operazioni = new ArrayList<>();

        l.getStoricoDB(pagina.getId(), operazioniAstratte);

        for(int i=0; i<operazioniAstratte.size(); i=i+10){

            int id = (int) operazioniAstratte.get(0); //id
            String tipo = (String) operazioniAstratte.get(1);
            boolean proposta = (boolean) operazioniAstratte.get(2); //proposta
            int riga = (int) operazioniAstratte.get(3);//riga
            int ordine = (int) operazioniAstratte.get(4); //ordine
            String fc = (String) operazioniAstratte.get(5); //frase coinvolta
            String fm = (String) operazioniAstratte.get(6); //frase modificata
            Timestamp data = (Timestamp) operazioniAstratte.get(7);//data
            int idPagina = (int) operazioniAstratte.get(8);  //id pagina
            String username = (String) operazioniAstratte.get(9); //utente

            Utente utente = getUtenteDB(username);

            Frase fraseCoinvolta = new Frase(riga, ordine, fc, pagina.getTestoRiferito());


            if(tipo.equals("I")) {

                Inserimento inserimento = new Inserimento(0, proposta, fraseCoinvolta, data, utente, storico, pagina);
                storico.addOperazione(inserimento);
            }
            else if(tipo.equals("M"))
            {
                Frase fraseModificata = new Frase(riga, ordine, fm, pagina.getTestoRiferito());
                Modifica modifica = new Modifica(0, proposta, fraseCoinvolta, fraseModificata, data, utente, storico, pagina);
                storico.addOperazione(modifica);

            }
            else if(tipo.equals("C"))
            {
                Cancellazione cancellazione = new Cancellazione(0, proposta, fraseCoinvolta, data, utente, storico, pagina);
                storico.addOperazione(cancellazione);
            }

        }

        pagina.setStorico(storico);
    }

    public void removeOldActiveProposal(){
        OperazioneDAO l= new OperazioneImplementazionePostgresDAO();
        l.removeActiveProposalDB(utilizzatore.getUsername(), paginaAperta.getId());
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
                    Cancellazione cancellazione = new Cancellazione(0, proposta,  fraseOld, new Timestamp(System.currentTimeMillis()), utilizzatore, paginaOriginale.getStorico(), paginaOriginale);
                    listaOperazioni.add(cancellazione);
                }
                else {
                    String contenutoNew = rowNew.getFirst().getContenuto().replace("\n", "");
                    Frase fraseNew = rowNew.getFirst();
                    rowNew.removeFirst();


                    if (!(contenutoOld.equals(contenutoNew))) {
                        Modifica modifica = new Modifica(0, proposta, fraseOld, fraseNew, new Timestamp(System.currentTimeMillis()), utilizzatore,  paginaOriginale.getStorico(), paginaOriginale);
                        listaOperazioni.add(modifica);
                    }
                }
            }

            if(!rowNew.isEmpty())
            {
                for(Frase f : rowNew)
                {
                    Inserimento inserimento = new Inserimento(0, proposta, f, new Timestamp(System.currentTimeMillis()), utilizzatore, paginaOriginale.getStorico(), paginaOriginale);
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

                    Cancellazione cancellazione = new Cancellazione(0, proposta, frase, new Timestamp(System.currentTimeMillis()), utilizzatore, paginaOriginale.getStorico(), paginaOriginale);
                    listaOperazioni.add(cancellazione);

                }

            }
        }

        puliziaOperazioni(listaOperazioni);

        //paginaOriginale.setTestoRiferito(testoModificato);
        //modifica pagina nel database (da fare)
        //paginaOriginale.getStorico().stampaOperazioni();
        PaginaDAO l = new PaginaImplementazionePostgresDAO();
        editPageDB(listaOperazioni);

        if(!proposta)
        {
            //recupero la pagina modificata dal db
            Pagina paginaPostModifica = getPaginaByIdDB(paginaOriginale.getId());
            aggiornaPagineCreate(paginaPostModifica);
        }
    }

    public void editPageDB(ArrayList<Operazione> listaOperazioni){

        PaginaDAO l = new PaginaImplementazionePostgresDAO();

        for(Operazione op : listaOperazioni)
        {
            int idPagina = op.getPagina().getId();
            int riga = op.getFraseCoinvolta().getRiga();
            int ordine = op.getFraseCoinvolta().getOrdine();
            String contenuto = op.getFraseCoinvolta().getContenuto();
            String username = op.getUtente().getUsername();

            if(op instanceof Inserimento)
                l.addFraseDB(idPagina, riga, ordine, contenuto, username);
            else if (op instanceof Modifica) {
                String contenutoM = ((Modifica) op).getFraseModificata().getContenuto();
                l.editFraseDB(idPagina, riga, ordine, contenutoM, username);
            }
            else if (op instanceof Cancellazione) {
                l.removeFraseDB(idPagina, riga, ordine, username);
            }
        }
    }

    public Pagina getPaginaByIdDB(int id){
        ArrayList<String> paginaInfo = new ArrayList<>();
        PaginaDAO l = new PaginaImplementazionePostgresDAO();
        UtenteDAO ul = new UtenteImplementazionePostgresDAO();
        Pagina pagina = null;

        l.getPaginaByIdDB(id, paginaInfo);
        if(!paginaInfo.isEmpty()) {

            String titolo = paginaInfo.get(0);
            Tema tema = getTemaDB(Integer.parseInt(paginaInfo.get(1)));
            Timestamp dataCreazione = Timestamp.valueOf(paginaInfo.get(2));
            Utente utente = getUtenteDB(paginaInfo.get(3));

            pagina = new Pagina(id, titolo, null, dataCreazione, utente, tema);
            pagina.setTestoRiferito(new Testo(pagina));
            setTestoFromDB(pagina);
        }

        return pagina;
    }

    public void setTestoFromDB(Pagina pagina){
        ArrayList<Integer> riga = new ArrayList<>();
        ArrayList<Integer> ordine = new ArrayList<>();
        ArrayList<String> contenuto = new ArrayList<>();
        ArrayList<Boolean> collegamento = new ArrayList<>();
        PaginaDAO l = new PaginaImplementazionePostgresDAO();

        l.getTestoDB(pagina.getId(), riga, ordine, contenuto, collegamento);

        ArrayList<Frase> tmp = new ArrayList<>();

        for(int i=0; i< riga.size(); i++)
        {
            if(!collegamento.get(i))
                tmp.add(new Frase(riga.get(i), ordine.get(i), contenuto.get(i), pagina.getTestoRiferito()));
            else {
                int idCollegamento = l.getIdCollegamentoDB(pagina.getId(), riga.get(i), ordine.get(i));
                tmp.add(new Collegamento(riga.get(i), ordine.get(i), contenuto.get(i), pagina.getTestoRiferito(), getPaginaByIdDB(idCollegamento)));
            }
        }

        pagina.getTestoRiferito().setListaFrasi(tmp);


    }

    public Tema getTemaDB(int id){

        PaginaDAO l = new PaginaImplementazionePostgresDAO();
        return new Tema(id, l.getTemaDB(id));
    }

    private void puliziaOperazioni(ArrayList<Operazione> listaOperazioni)
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

        addTextDB(idPagina, p.getTestoRiferito().getListaFrasi(), utilizzatore.getUsername());

    }

    public void addTextDB(int idPagina, ArrayList<Frase> listaFrasi, String utente){
        PaginaDAO l = new PaginaImplementazionePostgresDAO();

        for (Frase f : listaFrasi){
            l.addFraseDB(idPagina, f.getRiga(), f.getOrdine(), f.getContenuto(), utente);
        }
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

        Utente utente = new Utente(username, utenteInfo.get(0), utenteInfo.get(1), Timestamp.valueOf(utenteInfo.get(2)));
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

    public Pagina cercaPagina(String titolo) //gestire quando non si trova la pagina
    {
        ArrayList<String> paginaInfo = new ArrayList<>();
        PaginaDAO l = new PaginaImplementazionePostgresDAO();
        l.cercaPaginaDB(titolo, paginaInfo);

        Tema tema = getTemaDB(Integer.parseInt(paginaInfo.get(1)));
        Utente autore = getUtenteDB(paginaInfo.get(3));

        Pagina pagina = new Pagina(Integer.parseInt(paginaInfo.get(0)), titolo, null, Timestamp.valueOf(paginaInfo.get(2)), autore, tema);
        pagina.setTestoRiferito(new Testo(pagina)); //da valutare con lorenzo
        setTestoFromDB(pagina);

        return pagina;

    }

    public void searchAndOpenPage(String title)
    {
        Pagina pagina = cercaPagina(title);
        paginaAperta = pagina;
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
        l.approvaPropostaDB(proposta.getId(), utilizzatore.getUsername(), risposta);
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

    public void insertLink(int riga, int ordine, String titolo)
    {
        PaginaDAO l= new PaginaImplementazionePostgresDAO();
        Pagina paginaCollegamento = cercaPagina(titolo);
        l.insertLinkDB(paginaAperta.getId(), riga, ordine, paginaCollegamento.getId(), utilizzatore.getUsername());
    }

    public void removeLink(int riga, int ordine)  {

        PaginaDAO l= new PaginaImplementazionePostgresDAO();
        l.removeLinkDB(paginaAperta.getId(), riga, ordine, utilizzatore.getUsername());
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
