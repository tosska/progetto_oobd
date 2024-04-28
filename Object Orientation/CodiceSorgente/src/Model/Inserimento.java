package Model;

import java.sql.Timestamp;

public class Inserimento extends Operazione {


    public Inserimento(int id, Boolean proposta, Frase fraseInserita, Timestamp data, Utente utente, Pagina pagina) {
        super(id, proposta, fraseInserita, data, utente, pagina);

    }


    public String getTipo()
    {
        return "Inserimento";
    }


}
