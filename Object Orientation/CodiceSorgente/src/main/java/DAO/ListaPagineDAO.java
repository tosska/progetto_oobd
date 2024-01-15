package DAO;

import Model.Frase;
import Model.Pagina;
import Model.Testo;
import Model.Utente;

import java.sql.Timestamp;
import java.util.ArrayList;

public interface ListaPagineDAO {
    public void addPaginaDB(String titolo, Timestamp data, String autore);
    public void addFraseDB(int idPagina, ArrayList<Frase> listaFrasi);
    public int recuperaIdPagina();

    public Pagina cercaPaginaDB(String titolo);

    public Testo getTestoDB(int idPagina,  Pagina p);
    public ArrayList<Pagina> getPagineCreateDB(Utente utilizzatore);
}
