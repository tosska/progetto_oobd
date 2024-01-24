package Model;

import javax.swing.text.Utilities;
import java.util.ArrayList;

public class Testo {
    private ArrayList<Frase> listaFrasi;
    private Pagina paginaRiferita;

    private int numFrasi;

    private int dimensioneCaratteri; //da capire se cancellare o meno

    public Testo(Pagina paginaRiferita) {

        setPaginaRiferita(paginaRiferita);
        listaFrasi = new ArrayList<>();
        numFrasi=0;
        dimensioneCaratteri = 48;
    }

    public Pagina getPaginaRiferita() { return paginaRiferita; }
    public ArrayList<Frase> getListaFrasi() { return listaFrasi; }
    public void setPaginaRiferita(Pagina paginaRiferita) { this.paginaRiferita = paginaRiferita; }

    public int getNumRighe()
    {
        return listaFrasi.getLast().getRiga();
    }

    public ArrayList<Frase> getFrasiInRiga(int riga)
    {
        ArrayList<Frase> frasi = new ArrayList<>();

        for(Frase f: listaFrasi)
        {
            if(f.getRiga()==riga)
                frasi.add(f);
        }

        return frasi;
    }

    public void setTestoString(String testo)
    {
        if(!listaFrasi.isEmpty())
        {
            listaFrasi.clear();
        }

        int riga=1;
        int numFrasi=0;
        String textFormatted = formattaTesto(testo);

        textFormatted = textFormatted.substring(0, textFormatted.length()-1); //cancelliamo l'ultimo new line

        for(String f : textFormatted.split("\\."))
        {
            if(!f.equals(" "))
            {
                String frasePulita;
                numFrasi++;

                f = cancellaSpazi(f); // cancello gli spazi ad inizio e fine frase
                frasePulita = f;

                try {

                    //if(f.charAt(0)=='\n')
                    //frasePulita = frasePulita.substring(1);

                    while(f.charAt(0) == '\n') //se ad inizio frase vi è un new line incremento il valore di riga
                    {
                        riga++;
                        f = f.substring(1); //li tolgo perchè non le devo più contare per far incrementare riga
                    }

                    //if(f.charAt(f.length()-1)=='\n')
                    //frasePulita = f.substring(0, f.length()-1);


                }
                catch (Exception eg){
                    System.out.println(eg.getMessage());
                }

                Frase frase = new Frase(numFrasi, frasePulita, riga, this);
                listaFrasi.add(frase);


                int occurences = getNumOccurences(f, '\n'); //conto quante righe si estende la frase considerata
                if(occurences > 0)
                    riga = riga + occurences;

                System.out.println("Ho come newline: " + occurences);

            }

            stampaFrasi();
            }


    }

    public String getTestoString()
    {
        String testo="";

        for(Frase f : listaFrasi)
        {
            testo = testo + f.getContenuto() + ". ";
        }
        return testo;

        /*
        String testo="";
        Frase frasePrecedente = new Frase(1, "", 1, null);
        int riga=1; //la riga su cui stiamo scrivendo la frase

        for(Frase f: listaFrasi){

            System.out.println("Frase presa:" + f.getContenuto());


            if(riga!=f.getRiga()) {
                testo = testo + "\n";
                riga = f.getRiga();
            }
            System.out.println("Riga su cui stiamo scrivendo: " + riga);
            testo = testo + f.getContenuto() + ". ";
            riga = riga+getNumOccurences(f.getContenuto(), '\n');//significa che la frase si estende su più righe perchè in setTestoString abbiamo rimosso i neline ad inizio e fine frase




        }

        return testo;
        */

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

        //frase = cancellaSpazi_NewLine(frase);

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

    private String formattaTesto(String testo)
    {
        testo = testo + "\n"; // da valutare se la soluzione migliore
        String[] splitLine = testo.split("\\n");
        String textFormatted="";
        String aCapo;

        for(String line : splitLine)
        {
            while(line.length() > dimensioneCaratteri-1)
            {
                if(line.charAt(dimensioneCaratteri-1)!=' ' && line.charAt(dimensioneCaratteri-1)!='\n' && line.charAt(dimensioneCaratteri-1)!='.')
                    aCapo = "-\n";
                else
                    aCapo = "\n";


                textFormatted = textFormatted + line.substring(0, dimensioneCaratteri) + aCapo;
                line = line.substring(dimensioneCaratteri, line.length());
            }

            textFormatted = textFormatted + line + "\n";
        }

        System.out.println("Sono il testo formattato: " + textFormatted);
        return textFormatted;
    }


    private String cancellaSpazi(String str)
    {
        try {
            if (str.charAt(0) == ' ')
                str = str.substring(1);

            if (str.charAt(str.length() - 1) == ' ')
                str = str.substring(0, str.length() - 1);
        }
        catch (Exception e)
        {
            System.out.println("non trovati gli spazi");
        }


        return str;
    }
}
