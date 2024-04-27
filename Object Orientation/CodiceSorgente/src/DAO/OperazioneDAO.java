package DAO;


import java.util.ArrayList;

public interface OperazioneDAO {

    public void getProposteDaApprovareDB(String username, ArrayList<Object> proposta);
    public void getOperazioniDB(String username, int t, ArrayList<Object> operazioni);
    public void approvaPropostaDB(int idProposta, String utente, Boolean risposta);

    //public ArrayList<Operazione> getProposteUP_DB(Pagina pagina, Utente utente);

    public void removeActiveProposalDB(String username, int idPagina);

    public void getApprovazioneDB(int idOperazione, ArrayList<Object> approvazione);

}
