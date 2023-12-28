package Model;

public class Collegamento extends Frase{
    private Pagina paginaCollegata;

    public Collegamento(int ordine, String contenuto, int riga, Testo testo, Pagina pagina) {
        super(ordine, contenuto, riga, testo);
        paginaCollegata = pagina;
    }
}
