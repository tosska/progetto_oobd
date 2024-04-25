package DAO;

import Model.*;

import java.sql.Timestamp;
import java.util.ArrayList;

public interface PaginaDAO {
    public void addPaginaDB(String titolo, Timestamp data, String autore, int idTema);
    public void addTemaDB(String tema);
    public ArrayList<Tema> raccogliTemi();
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

    public void insertLinkDB(Pagina pagina, int riga, int ordine, Pagina paginaCollegamento, Utente utente);

    public void removeLinkDB(Pagina pagina, int riga, int ordine, Utente utente);
}
