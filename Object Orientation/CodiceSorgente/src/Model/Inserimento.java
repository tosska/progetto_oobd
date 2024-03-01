package Model;

public class Inserimento extends Operazione {


    public Inserimento(Boolean proposta, Frase fraseInserita, String data, Utente utente, Storico storico, Pagina pagina) {
        super(proposta, fraseInserita, data, utente, storico, pagina);

    }


    @Override
    public void stampa() //debug
    {
        System.out.println("Inserimento " + getProposta() + " " + super.getFraseCoinvolta().getRiga() +" "+ getData() + " " + getUtente().getUsername() +" " + super.getFraseCoinvolta().getContenuto());
    }
}
