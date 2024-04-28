package Model;

import java.sql.Timestamp;

public class Modifica extends Operazione {
    private Frase fraseModificata;

    public Modifica(int id, Boolean proposta, Frase fraseOriginale, Frase fraseModificata, Timestamp data, Utente utente,Pagina pagina) {
        super(id, proposta, fraseOriginale, data, utente, pagina);

        setFraseModificata(fraseModificata);
    }


    public String getTipo()
    {
        return "Modifica";
    }

    public Frase getFraseModificata() { return fraseModificata; }
    public void setFraseModificata(Frase fraseModificata) { this.fraseModificata = fraseModificata; }

    @Override
    public void stampa() //debug
    {
        System.out.println("Modifica: " + getProposta() + " " + fraseModificata.getRiga() +" "+ getData() + " " + getUtente().getUsername() +" " + super.getFraseCoinvolta().getContenuto() + " "+fraseModificata.getContenuto());
    }
}
