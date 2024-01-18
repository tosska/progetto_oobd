package Model;

import java.util.ArrayList;

public class Testo {
    private ArrayList<Frase> listaFrasi;
    private Pagina paginaRiferita;

    private int numFrasi;

    public Testo(Pagina paginaRiferita) {

        setPaginaRiferita(paginaRiferita);
        listaFrasi = new ArrayList<>();
        numFrasi=0;
    }

    public Pagina getPaginaRiferita() { return paginaRiferita; }
    public ArrayList<Frase> getListaFrasi() { return listaFrasi; }
    public void setPaginaRiferita(Pagina paginaRiferita) { this.paginaRiferita = paginaRiferita; }

    public void inserisciTesto(String testo)
    {
        int inizioFrase=0;
        String frase;
        int fineFrase;
        int riga=1;
        System.out.println("sono in testo");
        while( (fineFrase = testo.indexOf('.', inizioFrase, testo.length())) != -1)
        {
            frase = testo.substring(inizioFrase, fineFrase+1);
            inizioFrase = inizioFrase + frase.length();
            System.out.println(frase.length());

            System.out.println("frase:" + frase);
            inserisciFrase(frase, riga);

            //troviamo la riga in cui è posizionata la frase
            if(inizioFrase < testo.length() && testo.indexOf('\n', inizioFrase, inizioFrase+2) != -1
                    || frase.indexOf('\n', 1, frase.length()) != -1)
                riga++;
        }
        stampaFrasi();
    }

    private void inserisciFrase(String frase, int riga)
    {
        numFrasi++;
        Frase f = new Frase(numFrasi, cancellaSpazi_NewLine(frase), riga, this);
        listaFrasi.add(f);
    }

    private String cancellaSpazi_NewLine(String frase) //da valutare se è utile o cambiare approccio di lettura del testo
    {
        String temp = frase;
        //cancello l'eventuale spazio dopo il punto
        if(frase.charAt(0) == ' ') {
            frase = frase.substring(1, frase.length());
        }

        //cancello tutti i possibili newline
        //frase.replace("\n", "");

        return temp;
    }

    private void stampaFrasi()
    {
        for(Frase f : listaFrasi)
            f.stampa();
    }

}
