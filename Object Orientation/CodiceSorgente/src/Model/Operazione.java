package Model;

public class Operazione { //fare interfaccia?
    private Boolean proposta;
    private Frase fraseCoinvolta;
    private String data;
    private Utente utente;
    private Approvazione approvazione;
    private Storico storico;
    private Pagina pagina;

    public Operazione(Boolean proposta, Frase fraseCoinvolta, String data, Utente utente, Storico storico, Pagina pagina) {
        setProposta(proposta);
        setFraseCoinvolta(fraseCoinvolta);
        setData(data);
        setUtente(utente);
        setStorico(storico);
        setPagina(pagina);
    }

    public Boolean getProposta() { return proposta; }
    public void setProposta(Boolean proposta) { this.proposta = proposta; }
    public String getData() { return data; }
    public void setData(String data) { this.data = data; }
    public Utente getUtente() { return utente; }
    public void setUtente(Utente utente) { this.utente = utente; }
    public Storico getStorico() { return storico; }
    public void setStorico(Storico storico) { this.storico = storico; }
    public Pagina getPagina(){return pagina;}
    public void setPagina(Pagina pagina) {this.pagina = pagina;}

    public void setApprovazione(Approvazione approvazione)
    {
        this.approvazione = approvazione;
    }

    public void stampa()
    {
        //da capire se operazione farla classe o interfaccia
    }

    public Frase getFraseCoinvolta() {
        return fraseCoinvolta;
    }

    public void setFraseCoinvolta(Frase fraseCoinvolta) {
        this.fraseCoinvolta = fraseCoinvolta;
    }
}
