package Model;

public class Modifica extends Operazione {
    private Frase fraseOriginale;
    private Frase fraseModificata;

    public Modifica(Boolean proposta, int riga, String data, Utente utente, Frase fraseOriginale, Frase fraseModificata) {
        super(proposta, riga, data, utente);
        setFraseOriginale(fraseOriginale);
        setFraseModificata(fraseModificata);
    }

    public Frase getFraseOriginale() { return fraseOriginale; }
    public void setFraseOriginale(Frase fraseOriginale) { this.fraseOriginale = fraseOriginale; }
    public Frase getFraseModificata() { return fraseModificata; }
    public void setFraseModificata(Frase fraseModificata) { this.fraseModificata = fraseModificata; }
}
