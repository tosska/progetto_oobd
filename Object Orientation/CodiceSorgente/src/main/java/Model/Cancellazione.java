package Model;

public class Cancellazione extends Operazione {
    private String fraseEliminata;

    public Cancellazione(Boolean proposta, int riga, String data, Utente utente, String fraseEliminata, Storico storico, Pagina pagina) {
        super(proposta, riga, data, utente, storico, pagina);
        setFraseEliminata(fraseEliminata);
    }

    public String getFraseEliminata() { return fraseEliminata; }
    public void setFraseEliminata(String fraseEliminata) { this.fraseEliminata = fraseEliminata; }

    @Override
    public void stampa() //debug
    {
        System.out.println("Cancellazione: " + getProposta() + " " + getRiga() +" "+ getData() + " " + getUtente().getUsername() +" " + fraseEliminata);
    }
}
