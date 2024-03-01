package Model;

public class Frase {

    private int riga;
    private int ordine;
    private String contenuto;

    private Testo testo;

    public Frase(int riga, int ordine, String contenuto,  Testo testo) {
        setOrdine(ordine);
        setContenuto(contenuto);
        setRiga(riga);
        this.testo = testo;
    }

    public String getContenuto() { return contenuto; }
    public void setContenuto(String contenuto) { this.contenuto = contenuto; }
    public int getOrdine() { return ordine; }
    public void setOrdine(int ordine) { this.ordine = ordine; }
    public int getRiga() { return riga; }
    public void setRiga(int riga) { this.riga = riga; }

    public void stampa()  //da cancellare
    {
        System.out.println("Riga: " + riga + "->  " + contenuto);
    }
}
