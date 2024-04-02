package Model;

import java.sql.Timestamp;

public class Inserimento extends Operazione {


    public Inserimento(Boolean proposta, Frase fraseInserita, Timestamp data, Utente utente, Storico storico, Pagina pagina) {
        super(proposta, fraseInserita, data, utente, storico, pagina);

    }


    public String getTipo()
    {
        return "Inserimento";
    }

    @Override
    public void stampa() //debug
    {
        System.out.println("Inserimento " + getProposta() + " " + super.getFraseCoinvolta().getRiga() +" "+ getData() + " " + getUtente().getUsername() +" " + super.getFraseCoinvolta().getContenuto());
    }
}
