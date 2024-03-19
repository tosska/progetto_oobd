package DAO;

import Model.*;

import java.sql.Timestamp;
import java.util.ArrayList;

public interface ListaPagineDAO {
    public void addPaginaDB(String titolo, Timestamp data, String autore, String tema);
    public void addTemaDB(String tema);
    public ArrayList<String> raccogliTemi();
    public void addTextDB(int idPagina, ArrayList<Frase> listaFrasi, Utente utilizzatore);

    public void addFraseDB(Pagina pagina, Inserimento inserimento);
    public void removeFraseDB(Pagina pagina, Cancellazione cancellazione);
    public void editFraseDB(Pagina pagina, Modifica modifica);

    public int recuperaIdPagina();
    public Pagina cercaPaginaDB(String titolo);
    public Storico getStoricoDB(Pagina p);
    public Testo getTestoDB(Pagina p);

    public void editPageDB(Pagina pagina, ArrayList<Operazione> listaOperazioni);

    public void editTextDB(Pagina pagina, ArrayList<Operazione> listaOperazioni);

    public ArrayList<Pagina> getPagineCreateDB(Utente utilizzatore);

    //public ArrayList<Operazione> getProposteDaApprovareDB(Utente utilizzatore);

    public Pagina getPaginaByIdDB(int idPagina);
}
