package Model;

public class Inserimento extends Operazione {
    private Frase fraseInserita;

    public Inserimento(Boolean proposta, int riga, String data, Utente utente, Frase fraseInserita, Storico storico) {
        super(proposta, riga, data, utente, storico);
        setFraseInserita(fraseInserita);
    }

    public Frase getFraseInserita() { return fraseInserita; }
    public void setFraseInserita(Frase fraseInserita) { this.fraseInserita = fraseInserita; }
}
