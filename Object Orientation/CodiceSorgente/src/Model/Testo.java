package Model;

import javax.swing.text.Utilities;
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

    public void setTestoString(String testo)
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
            inserisciFrasePura(frase, riga);

            //troviamo la riga in cui è posizionata la frase
            try {
                if (inizioFrase < testo.length() && testo.indexOf('\n', inizioFrase, inizioFrase + 2) != -1
                        || frase.indexOf('\n', 1, frase.length()) != -1)
                    riga++;
            }
            catch (Exception e) {
                System.out.println("carattere di new line non trovato");
            }

        }
        stampaFrasi();
    }

    public String getTestoString()
    {
        String testo="";

        for(Frase f: listaFrasi)
        {
            testo = testo + f.getContenuto();
        }

        return testo;
    }

    public void inserisciFrase(Frase f)
    {
        f.setOrdine(numFrasi);
        listaFrasi.add(f);
        numFrasi++;
    }
    private void inserisciFrasePura(String frase, int riga)
    {
        Frase f;

        frase = cancellaSpazi_NewLine(frase);

        if(frase.charAt(0)== '#' && frase.charAt(frase.length()+1)=='#')
        {
            frase = frase.substring(frase.indexOf("#")+1, frase.lastIndexOf('#'));
            f = new Collegamento(numFrasi, frase, riga, this, null); //da cambiare
        }
        else
            f = new Frase(numFrasi, frase, riga, this);

        listaFrasi.add(f);
        numFrasi++;
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