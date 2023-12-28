package Model;

import java.util.ArrayList;

public class Storico {
    private Pagina pagina;
    private ArrayList<Operazione> listaOperazioni;

    public Storico(Pagina pagina) {
        setPagina(pagina);
    }

    public Pagina getPagina() { return pagina; }
    public void setPagina(Pagina pagina) { this.pagina = pagina; }
}
