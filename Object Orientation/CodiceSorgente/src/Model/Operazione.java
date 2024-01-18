package Model;

public class Operazione { //fare interfaccia?
    private Boolean proposta;
    private int riga;
    private String data;
    private Utente utente;
    private Approvazione approvazione;
    private Storico storico;

    private Pagina pagina;

    public Operazione(Boolean proposta, int riga, String data, Utente utente, Storico storico, Pagina pagina) {
        setProposta(proposta);
        setRiga(riga);
        setData(data);
        setUtente(utente);
        setStorico(storico);
        setPagina(pagina);
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
    public Pagina getPagina(){return pagina;}
    public void setPagina(Pagina pagina) {this.pagina = pagina;}

    public void stampa()
    {
        //da capire se operazione farla classe o interfaccia
    }
}
