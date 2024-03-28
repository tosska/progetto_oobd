package Model;

public class Tema {


    private int idTema;
    private String nome;

    public Tema(int idTema, String nome) {
        setIdTema(idTema);
        setNome(nome);
    }

    public int getIdTema() {
        return idTema;
    }

    public void setIdTema(int idTema) {
        this.idTema = idTema;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }


}
