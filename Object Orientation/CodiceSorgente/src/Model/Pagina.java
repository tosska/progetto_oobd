package Model;

import java.sql.Timestamp;
import java.util.ArrayList;

public class Pagina {

    private int id;
    private String titolo;
    private Tema tema;
    private Timestamp dataCreazione;
    private Utente autore;
    private Testo testoRiferito;
    private ArrayList<Operazione> storico;

    public Pagina(String titolo, Utente autore, String testo, Tema tema) { //creazione di un oggetto Pagina mandando testo di tipo String
                                                                // costruttore da chiamare quando viene creata una pagina;
        setTitolo(titolo);
        setDataCreazione();
        setAutore(autore);
        testoRiferito = new Testo(this);
        storico = new ArrayList<>();
        setTema(tema);
        setTestoString(testo);
    }

    public Pagina(int id, String titolo, Testo testo, Timestamp dataCreazione, Utente autore, Tema tema ) { //creazione di un oggetto Pagina mandando testo di tipo Testo
        // costruttore da chiamare quando viene recuperata una pagina dal DB
        this.id = id;
        setTitolo(titolo);
        setTestoRiferito(testo);
        this.dataCreazione = dataCreazione;
        setAutore(autore);
        testoRiferito = testo;
        setTema(tema);
        storico = new ArrayList<>();

    }

    public String getTitolo() { return titolo; }
    public void setTitolo(String titolo) { this.titolo = titolo; }

    public Timestamp getDataCreazione() { return dataCreazione; }
    public void setDataCreazione() { this.dataCreazione = new Timestamp(System.currentTimeMillis()); }
    public Utente getAutore() { return autore; }
    public void setAutore(Utente autore) { this.autore = autore; }

    public Testo getTestoRiferito() { return testoRiferito; }
    public void setTestoRiferito(Testo testoRiferito) { this.testoRiferito = testoRiferito; }

    public int getId() {return id;}

    public String getTestoString()
    {
        return testoRiferito.getTestoString();
    }

    public void setTestoString(String testo)
    {
        testoRiferito.setTestoString(testo);
    }

    public void setTema(Tema tema) {this.tema = tema;}

    public Tema getTema() {
        return tema;
    }

    public void setLunghezzaRiga(int n)
    {
        testoRiferito.setLunghezzaRiga(n);
    }

    public ArrayList<Operazione> getStorico() {
        return storico;
    }

    public void setStorico(ArrayList<Operazione> storico) {
        this.storico = storico;
    }
}
