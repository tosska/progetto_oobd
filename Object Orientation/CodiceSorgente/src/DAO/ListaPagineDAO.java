package DAO;

import Model.Frase;

import java.sql.Timestamp;
import java.util.ArrayList;

public interface ListaPagineDAO {
    public void addPaginaDB(String titolo, Timestamp data, String autore);
    public void addFraseDB(int idPagina, ArrayList<Frase> listaFrasi);
    public int recuperaIdPagina();
}
