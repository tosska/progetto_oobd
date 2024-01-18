package Model;

import java.sql.Timestamp;

public class Pagina {
    private String titolo;
    private Timestamp dataCreazione;
    private Utente autore;
    private Testo testoRiferito;
    private Storico storico;

    public Pagina(String titolo, Utente autore, String testo) {
        setTitolo(titolo);
        setDataCreazione();
        setAutore(autore);
        testoRiferito = new Testo(this);
        // storico = new Storico(this);
        inserisciTesto(testo);
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

    public void inserisciTesto(String testo)
    {
        System.out.println("sono in pagina");
        testoRiferito.inserisciTesto(testo);
    }
}
