package Model;

import java.sql.Timestamp;

public abstract class Operazione { //fare interfaccia?
    private int id;
    private Boolean proposta;
    private Frase fraseCoinvolta;
    private Timestamp data;
    private Utente utente;
    private Approvazione approvazione;
    private Pagina pagina;

    public Operazione(int id, Boolean proposta, Frase fraseCoinvolta, Timestamp data, Utente utente,  Pagina pagina) {
        setId(id);
        setProposta(proposta);
        setFraseCoinvolta(fraseCoinvolta);
        setData(data);
        setUtente(utente);
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
    public Pagina getPagina(){return pagina;}
    public void setPagina(Pagina pagina) {this.pagina = pagina;}

    public void setApprovazione(Approvazione approvazione)
    {
        this.approvazione = approvazione;
    }

    public abstract void stampa();

    public Frase getFraseCoinvolta() {
        return fraseCoinvolta;
    }

    public void setFraseCoinvolta(Frase fraseCoinvolta) {
        this.fraseCoinvolta = fraseCoinvolta;
    }

    public Frase getFraseModificata(){
        return null;
    }

    public abstract String getTipo();

    public Approvazione getApprovazione() {
        return approvazione;
    }


}
