package Model;

public class Pagina {
    private String titolo;
    private String dataCreazione;
    public Utente autore;

    public Pagina(String title, String data, Utente author) {
        setTitolo(title);
        setDataCreazione(data);
        autore = author;
    }

    public String getTitolo() { return titolo; }
    public void setTitolo(String titolo) { this.titolo = titolo; }

    public String getDataCreazione() { return dataCreazione; }
    public void setDataCreazione(String dataCreazione) { this.dataCreazione = dataCreazione; }
}
