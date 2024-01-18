package Model;

public class Collegamento extends Frase{
    private Pagina paginaCollegata;

    public Collegamento(int ordine, String contenuto, int riga, Testo testo, Pagina paginaCollegata) {
        super(ordine, contenuto, riga, testo);
        setPaginaCollegata(paginaCollegata);
    }

    public Pagina getPaginaCollegata() { return paginaCollegata; }
    public void setPaginaCollegata(Pagina paginaCollegata) { this.paginaCollegata = paginaCollegata; }
}
