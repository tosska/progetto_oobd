package Model;

import java.sql.Timestamp;

public class Approvazione {
    private Timestamp data;
    private Boolean risposta;
    private Operazione operazione;
    private Utente autore;

    public Approvazione(Timestamp data, Boolean risposta, Operazione operazione, Utente autore) {
        setData(data);
        setRisposta(risposta);
        setOperazione(operazione);
        setAutore(autore);
    }

    public Timestamp getData() { return data; }
    public void setData(Timestamp data) { this.data = data; }
    public Boolean getRisposta() { return risposta; }
    public void setRisposta(Boolean risposta) { this.risposta = risposta; }
    public Operazione getOperazione() { return operazione; }
    public void setOperazione(Operazione operazione) { this.operazione = operazione; }
    public Utente getAutore() { return autore; }
    public void setAutore(Utente autore) { this.autore = autore; }

}
