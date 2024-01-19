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
        testo = testo + "\n"; // da valutare se la soluzione migliore
        String[] splitLine = testo.split("\\n");
        String textFormatted="";
        int riga=1;
        int numFrasi=0;

        for(String line : splitLine)
        {
            while(line.length() > 50)
            {
                textFormatted = textFormatted + line.substring(0, 51) + "\n";
                line = line.substring(51, line.length());
            }

            textFormatted = textFormatted + line + "\n";
        }

        System.out.println("Sono il testo formattato: " + textFormatted);
        
        for(String f : textFormatted.split("\\."))
        {
            numFrasi++;

            System.out.println("Sono: "  + f);
            f = f.strip(); // cancello gli spazi ad inizio e fine frase
            System.out.println("Sono la frase senza spazi" + f);

            try {
                if (f.charAt(0) == '\n') //se ad inizio frase vi è un new line incremento il valore di riga
                    riga++;
            }
            catch (Exception eg){
                System.out.println(eg.getMessage());
            }

            //creo la frase
            Frase frase = new Frase(numFrasi, f.replace("\n", ""), riga, this);
            listaFrasi.add(frase);

            String temp = f.substring(1);
            int occurences = getNumOccurences(temp, '\n'); //conto quante righe si estende la frase considerata
            if(occurences > 0)
                riga = riga + occurences;

        }

        stampaFrasi();

    }

    public String getTestoString()
    {
        String testo="";

        for(Frase f: listaFrasi)
        {
            testo = testo + f.getRiga() + f.getContenuto();
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

    private int getNumOccurences(String line, char occurence)
    {
        int num=0;

        for(int i=0; i<line.length(); i++)
        {
            if(line.charAt(i) == occurence)
                num++;
        }

        return num;
    }

}
