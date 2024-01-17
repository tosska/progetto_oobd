package Model;

import java.util.ArrayList;

public class Storico {
    private Pagina pagina;
    private ArrayList<Operazione> listaOperazioni;

    public Storico(Pagina pagina) {

        setPagina(pagina);
        listaOperazioni = new ArrayList<>();
    }

    public Pagina getPagina() { return pagina; }
    public void setPagina(Pagina pagina) { this.pagina = pagina; }

    public void addOperazione(Operazione operazione)
    {
        listaOperazioni.add(operazione);
    }

    public ArrayList<Operazione> getListaOperazioni()
    {
        return listaOperazioni;
    }

    public void stampaOperazioni()
    {
        for(Operazione op:listaOperazioni)
        {
            op.stampa();
        }
    }
}
