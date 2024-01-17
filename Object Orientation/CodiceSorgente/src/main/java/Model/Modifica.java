package Model;

public class Modifica extends Operazione {
    private String fraseOriginale;
    private String fraseModificata;

    public Modifica(Boolean proposta, int riga, String data, Utente utente, String fraseOriginale, String fraseModificata, Storico storico, Pagina pagina) {
        super(proposta, riga, data, utente, storico, pagina);
        setFraseOriginale(fraseOriginale);
        setFraseModificata(fraseModificata);
    }

    public String getFraseOriginale() { return fraseOriginale; }
    public void setFraseOriginale(String fraseOriginale) { this.fraseOriginale = fraseOriginale; }
    public String getFraseModificata() { return fraseModificata; }
    public void setFraseModificata(String fraseModificata) { this.fraseModificata = fraseModificata; }

    @Override
    public void stampa() //debug
    {
        System.out.println("Modifica: " + getProposta() + " " + getRiga() +" "+ getData() + " " + getUtente().getUsername() +" " + fraseOriginale + " "+fraseModificata);
    }
}
