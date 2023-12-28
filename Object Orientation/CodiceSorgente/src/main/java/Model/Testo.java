package Model;

import java.util.ArrayList;

public class Testo {
    private ArrayList<Frase> ListaFrasi;
    private Pagina paginaRiferita;

    public Testo(Pagina paginaRiferita) {
        setPaginaRiferita(paginaRiferita);
    }

    public Pagina getPaginaRiferita() { return paginaRiferita; }
    public void setPaginaRiferita(Pagina paginaRiferita) { this.paginaRiferita = paginaRiferita; }
}
