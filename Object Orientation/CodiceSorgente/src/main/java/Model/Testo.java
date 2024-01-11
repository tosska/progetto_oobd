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

            //troviamo la riga in cui è posizionata la frase
            if(testo.indexOf('\n', inizioFrase-1, inizioFrase) != -1 && inizioFrase < testo.length())
            {
                riga++;
            }

            frase = cancellaSpazi_NewLine(frase);
            System.out.println("frase:" + frase);
            inserisciFrase(frase, riga);

        }
        stampaFrasi();
    }

    private void inserisciFrase(String frase, int riga)
    {
        numFrasi++;
        Frase f = new Frase(numFrasi, frase, riga, this);
        listaFrasi.add(f);
    }

    private String cancellaSpazi_NewLine(String frase) //da valutare se è utile o cambiare approccio di lettura del testo
    {

        //cancello l'eventuale spazio dopo il punto
        if(frase.charAt(0) == ' ') {
            frase = frase.substring(1, frase.length());
        }

        //cancello tutti i possibili newline
        frase.replace("\n", "");

        return frase;
    }

    private void stampaFrasi()
    {
        for(Frase f : listaFrasi)
            f.stampa();
    }
}
