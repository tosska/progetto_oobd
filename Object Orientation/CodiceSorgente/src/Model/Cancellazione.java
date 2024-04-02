package Model;

import java.sql.Timestamp;

public class Cancellazione extends Operazione {


    public Cancellazione(Boolean proposta, Frase fraseEliminata, Timestamp data, Utente utente, Storico storico, Pagina pagina) {
        super(proposta, fraseEliminata, data, utente, storico, pagina);
    }

    public String getTipo()
    {
        return "Cancellazione";
    }

    @Override
    public void stampa() //debug
    {
        System.out.println("Cancellazione: " + getProposta() + " " + super.getFraseCoinvolta().getContenuto() +" "+ getData() + " " + getUtente().getUsername());
    }
}
