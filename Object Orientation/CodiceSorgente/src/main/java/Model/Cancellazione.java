package Model;

public class Cancellazione extends Operazione {
    private Frase fraseEliminata;

    public Cancellazione(Boolean proposta, int riga, String data, Utente utente, Frase fraseEliminata) {
        super(proposta, riga, data, utente);
        setFraseEliminata(fraseEliminata);
    }

    public Frase getFraseEliminata() { return fraseEliminata; }
    public void setFraseEliminata(Frase fraseEliminata) { this.fraseEliminata = fraseEliminata; }
}
