package Model;

public class Cancellazione extends Operazione {


    public Cancellazione(Boolean proposta, Frase fraseEliminata, String data, Utente utente, Storico storico, Pagina pagina) {
        super(proposta, fraseEliminata, data, utente, storico, pagina);

    }

    @Override
    public void stampa() //debug
    {
        System.out.println("Cancellazione: " + getProposta() + " " + super.getFraseCoinvolta().getContenuto() +" "+ getData() + " " + getUtente().getUsername());
    }
}
