package Model;

import java.sql.Timestamp;

public class Cancellazione extends Operazione {


    public Cancellazione(int id, Boolean proposta, Frase fraseEliminata, Timestamp data, Utente utente, Pagina pagina) {
        super(id, proposta, fraseEliminata, data, utente, pagina);
    }

    public String getTipo()
    {
        return "Cancellazione";
    }


}
