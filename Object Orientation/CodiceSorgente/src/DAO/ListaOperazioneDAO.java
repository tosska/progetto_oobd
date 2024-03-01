package DAO;

import Model.Operazione;
import Model.Utente;

import java.util.ArrayList;

public interface ListaOperazioneDAO {

    public ArrayList<Operazione> getProposteDaApprovareDB(Utente utilizzatore);
    public ArrayList<Operazione> getOperazioniDB(Utente utilizzatore);
}
