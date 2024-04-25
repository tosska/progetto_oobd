package DAO;

import Model.Approvazione;
import Model.Operazione;
import Model.Pagina;
import Model.Utente;

import java.util.ArrayList;

public interface OperazioneDAO {

    public ArrayList<Operazione> getProposteDaApprovareDB(ArrayList<Pagina> pagineUtilizzatore, Utente utilizzatore);
    public ArrayList<Operazione> getOperazioniDB(Utente utilizzatore, int tipo);
    public void approvaPropostaDB(Operazione proposta, Utente utilizzatore, Boolean risposta);

    public ArrayList<Operazione> getProposteUP_DB(Pagina pagina, Utente utente);

    public void removeActiveProposalDB(Utente utente, Pagina pagina);

    public Approvazione getApprovazioneDB(Operazione operazione);

}
