package Model;

public class Operazione {
    private Boolean proposta;
    private int riga;
    private String data;
    private Utente utente;
    private Approvazione approvazione;
    private Storico storico;

    public Operazione(Boolean proposta, int riga, String data, Utente utente, Storico storico) {
        setProposta(proposta);
        setRiga(riga);
        setData(data);
        setUtente(utente);
        setStorico(storico);
    }

    public Boolean getProposta() { return proposta; }
    public void setProposta(Boolean proposta) { this.proposta = proposta; }
    public int getRiga() { return riga; }
    public void setRiga(int riga) { this.riga = riga; }
    public String getData() { return data; }
    public void setData(String data) { this.data = data; }
    public Utente getUtente() { return utente; }
    public void setUtente(Utente utente) { this.utente = utente; }
    public Storico getStorico() { return storico; }
    public void setStorico(Storico storico) { this.storico = storico; }
}
