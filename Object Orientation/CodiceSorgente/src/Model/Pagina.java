package Model;

import java.sql.Timestamp;

public class Pagina {

    private int id;
    private String titolo;
    private String tema; //da implementare sia da codice che da UML
    private Timestamp dataCreazione;
    private Utente autore;
    private Testo testoRiferito;
    private Storico storico;

    public Pagina(String titolo, Utente autore, String testo) { //creazione di un oggetto Pagina mandando testo di tipo String
                                                                // costruttore da chiamare quando viene creata una pagina;
        setTitolo(titolo);
        setDataCreazione();
        setAutore(autore);
        testoRiferito = new Testo(this);
        storico = new Storico(this);
        setTestoString(testo);
    }

    public Pagina(int id, String titolo, Testo testo, Timestamp dataCreazione, Utente autore ) { //creazione di un oggetto Pagina mandando testo di tipo Testo
                                                                                                // costruttore da chiamare quando viene recuperata una pagina dal DB
        this.id = id;
        setTitolo(titolo);
        setTestoRiferito(testo);
        this.dataCreazione = dataCreazione;
        setAutore(autore);
        testoRiferito = testo;
        // storico = new Storico(this);

        System.out.println(autore.getUsername());
    }

    public String getTitolo() { return titolo; }
    public void setTitolo(String titolo) { this.titolo = titolo; }

    public Timestamp getDataCreazione() { return dataCreazione; }
    public void setDataCreazione() { this.dataCreazione = new Timestamp(System.currentTimeMillis()); }
    public String getAutore() { return autore.getUsername(); }
    public void setAutore(Utente autore) { this.autore = autore; }

    public Testo getTestoRiferito() { return testoRiferito; }
    public void setTestoRiferito(Testo testoRiferito) { this.testoRiferito = testoRiferito; }
    public Storico getStorico() { return storico; }
    public void setStorico(Storico storico) { this.storico = storico; }

    public int getId() {return id;}

    public String getTestoString() //da cancellare?
    {
        return testoRiferito.getTestoString();
    }

    public void setTestoString(String testo)
    {
        testoRiferito.setTestoString(testo);
    }
}