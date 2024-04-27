package DAO;

import java.sql.Timestamp;
import java.util.ArrayList;

public interface PaginaDAO {
    public void addPaginaDB(String titolo, Timestamp data, String autore, int idTema);
    public void addTemaDB(String tema);
    public void raccogliTemi(ArrayList<Integer> listaIdTemi, ArrayList<String> listaNomiTemi);

    public void addFraseDB(int idPagina, int riga, int ordine, String contenuto, String utente);
    public void removeFraseDB(int idPagina, int riga, int ordine, String utente);
    public void editFraseDB(int idPagina, int riga, int ordine, String contenuto, String utente);

    public int recuperaIdPagina();
    public void cercaPaginaDB(String titolo, ArrayList<String> pagina);
    public void getStoricoDB(int idPagina, ArrayList<Object> operazioni);
    public void getTestoDB(int idPagina, ArrayList<Integer> riga, ArrayList<Integer> ordine, ArrayList<String> contenuto, ArrayList<Boolean> collegamento);


    public void getPagineCreateDB(String utente, ArrayList<Integer> id,
                                  ArrayList<String> titolo, ArrayList<Integer> tema,
                                  ArrayList<Timestamp> dataCreazione, ArrayList<String> autore);

    public void getPaginaByIdDB(int idPagina, ArrayList<String> paginaInfo);

    public void insertLinkDB(int idPagina, int riga, int ordine, int idPaginaCollegata, String utente);

    public void removeLinkDB(int idPagina, int riga, int ordine, String utente);

    public String getTemaDB(int idTema);

    public int getIdCollegamentoDB(int id_pagina, int riga, int ordine);
}
