package Model;

public class Inserimento extends Operazione {
    private String fraseInserita;

    public Inserimento(Boolean proposta, int riga, String data, Utente utente, String fraseInserita, Storico storico, Pagina pagina) {
        super(proposta, riga, data, utente, storico, pagina);
        setFraseInserita(fraseInserita);
    }

    public String getFraseInserita() { return fraseInserita; }
    public void setFraseInserita(String fraseInserita) { this.fraseInserita = fraseInserita; }

    @Override
    public void stampa() //debug
    {
        System.out.println("Inserimento " + getProposta() + " " + getRiga() +" "+ getData() + " " + getUtente().getUsername() +" " + fraseInserita);
    }
}
