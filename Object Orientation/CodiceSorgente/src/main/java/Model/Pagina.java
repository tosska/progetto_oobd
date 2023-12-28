package Model;

public class Pagina {
    private String titolo;
    private String dataCreazione;
    private Autore autore;
    private Testo testoRiferito;
    private Storico storico;

    public Pagina(String titolo, String dataCreazione, Autore autore, Testo testoRiferito, Storico storico) {
        setTitolo(titolo);
        setDataCreazione(dataCreazione);
        setAutore(autore);
        setTestoRiferito(testoRiferito);
        setStorico(storico);
    }

    public String getTitolo() { return titolo; }
    public void setTitolo(String titolo) { this.titolo = titolo; }

    public String getDataCreazione() { return dataCreazione; }
    public void setDataCreazione(String dataCreazione) { this.dataCreazione = dataCreazione; }
    public Autore getAutore() { return autore; }
    public void setAutore(Autore autore) { this.autore = autore; }

    public Testo getTestoRiferito() { return testoRiferito; }
    public void setTestoRiferito(Testo testoRiferito) { this.testoRiferito = testoRiferito; }
    public Storico getStorico() { return storico; }
    public void setStorico(Storico storico) { this.storico = storico; }
}
