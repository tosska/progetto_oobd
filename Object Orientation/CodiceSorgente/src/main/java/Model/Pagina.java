package Model;

public class Pagina {
    private String titolo;
    private String dataCreazione;
    private Utente autore;
    private Testo testoRiferito;
    private Storico storico;

    public Pagina(String titolo, String dataCreazione, Utente autore, String testo) {
        setTitolo(titolo);
        setDataCreazione(dataCreazione);
        setAutore(autore);
        testoRiferito = new Testo(this);
        storico = new Storico(this);
        inserisciTesto(testo);
    }

    public String getTitolo() { return titolo; }
    public void setTitolo(String titolo) { this.titolo = titolo; }

    public String getDataCreazione() { return dataCreazione; }
    public void setDataCreazione(String dataCreazione) { this.dataCreazione = dataCreazione; }
    public Utente getAutore() { return autore; }
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
