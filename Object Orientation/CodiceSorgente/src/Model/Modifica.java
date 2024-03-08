package Model;

import java.sql.Timestamp;

public class Modifica extends Operazione {
    private Frase fraseModificata;

    public Modifica(Boolean proposta, Frase fraseOriginale, Frase fraseModificata, Timestamp data, Utente utente, Storico storico, Pagina pagina) {
        super(proposta, fraseOriginale, data, utente, storico, pagina);

        setFraseModificata(fraseModificata);
    }


    public Frase getFraseModificata() { return fraseModificata; }
    public void setFraseModificata(Frase fraseModificata) { this.fraseModificata = fraseModificata; }

    @Override
    public void stampa() //debug
    {
        System.out.println("Modifica: " + getProposta() + " " + fraseModificata.getRiga() +" "+ getData() + " " + getUtente().getUsername() +" " + super.getFraseCoinvolta().getContenuto() + " "+fraseModificata.getContenuto());
    }
}
