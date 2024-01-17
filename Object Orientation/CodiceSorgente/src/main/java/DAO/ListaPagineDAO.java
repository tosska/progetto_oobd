package DAO;

import Model.*;

import java.sql.Timestamp;
import java.util.ArrayList;

public interface ListaPagineDAO {
    public void addPaginaDB(String titolo, Timestamp data, String autore);
    public void addFraseDB(int idPagina, ArrayList<Frase> listaFrasi);
    public int recuperaIdPagina();

    public Pagina cercaPaginaDB(String titolo);

    public Storico getStoricoDB(Pagina p);

    public Testo getTestoDB(Pagina p);
    public ArrayList<Pagina> getPagineCreateDB(Utente utilizzatore);
}
