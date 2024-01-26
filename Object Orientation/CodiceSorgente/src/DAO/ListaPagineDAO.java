package DAO;

import Model.*;

import java.sql.Timestamp;
import java.util.ArrayList;

public interface ListaPagineDAO {
    public void addPaginaDB(String titolo, Timestamp data, String autore);
    public void addTextDB(int idPagina, ArrayList<Frase> listaFrasi);

    public void addFraseDB(int idPagina, Frase fraseInserita);
    public void removeFraseDB(int idPagina, Frase fraseEliminata);
    public void editFraseDB(int idPagina, Frase fraseOriginale, Frase fraseModificata);

    public int recuperaIdPagina();
    public Pagina cercaPaginaDB(String titolo);
    public Storico getStoricoDB(Pagina p);
    public Testo getTestoDB(Pagina p);

    public void editPageDB(Pagina pagina, ArrayList<Operazione> listaOperazioni);

    public void editTextDB(Pagina pagina, ArrayList<Operazione> listaOperazioni);

    public ArrayList<Pagina> getPagineCreateDB(Utente utilizzatore);

    public ArrayList<Operazione> getProposteDaApprovareDB(Utente utilizzatore);

    public Pagina getPaginaByIdDB(int idPagina);
}
