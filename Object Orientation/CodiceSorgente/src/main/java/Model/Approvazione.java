package Model;

public class Approvazione {
    private String data;
    private Boolean risposta;
    private Operazione operazione;
    private Utente autore;

    public Approvazione(String data, Boolean risposta, Operazione operazione, Utente autore) {
        setData(data);
        setRisposta(risposta);
        setOperazione(operazione);
        setAutore(autore);
    }

    public String getData() { return data; }
    public void setData(String data) { this.data = data; }
    public Boolean getRisposta() { return risposta; }
    public void setRisposta(Boolean risposta) { this.risposta = risposta; }
    public Operazione getOperazione() { return operazione; }
    public void setOperazione(Operazione operazione) { this.operazione = operazione; }
    public Utente getAutore() { return autore; }
    public void setAutore(Utente autore) { this.autore = autore; }

}
