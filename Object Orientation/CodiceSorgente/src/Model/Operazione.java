package Model;

import java.sql.Timestamp;

public class Operazione { //fare interfaccia?
    private int id;
    private Boolean proposta;
    private Frase fraseCoinvolta;
    private Timestamp data;
    private Utente utente;
    private Approvazione approvazione;
    private Storico storico;
    private Pagina pagina;

    public Operazione(Boolean proposta, Frase fraseCoinvolta, Timestamp data, Utente utente, Storico storico, Pagina pagina) {
        setProposta(proposta);
        setFraseCoinvolta(fraseCoinvolta);
        setData(data);
        setUtente(utente);
        setStorico(storico);
        setPagina(pagina);
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public Boolean getProposta() { return proposta; }
    public void setProposta(Boolean proposta) { this.proposta = proposta; }
    public Timestamp getData() { return data; }
    public void setData(Timestamp data) { this.data = data; }
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
