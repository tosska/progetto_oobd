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
        int ordine=0;
        String textFormatted = formattaTesto(testo);

        textFormatted = textFormatted.substring(0, textFormatted.length()-1); //cancelliamo l'ultimo new line

        for(String f : textFormatted.split("\\."))
        {
            if(!isStringaVuota(f))
            {
                String frasePulita;
                ordine++;

                f = cancellaSpazi(f); // cancello gli spazi ad inizio e fine frase
                frasePulita = f.replace("\n", "");

                try {

                    //if(f.charAt(0)=='\n')
                    //frasePulita = frasePulita.substring(1);

                    while(f.charAt(0) == '\n') //se ad inizio frase vi è un new line incremento il valore di riga
                    {
                        riga++;
                        ordine=1;
                        f = f.substring(1); //li tolgo perchè non le devo più contare per far incrementare riga
                    }

                    //if(f.charAt(f.length()-1)=='\n')
                    //frasePulita = f.substring(0, f.length()-1);


                }
                catch (Exception eg){
                    System.out.println(eg.getMessage());
                }

                Frase frase = new Frase(riga, ordine, frasePulita, this);
                listaFrasi.add(frase);


                int occurences = getNumOccurences(f, '\n'); //conto quante righe si estende la frase considerata
                if(occurences > 0) {
                    riga = riga + occurences;

                    ordine = 0;
                }

                System.out.println("Ho come newline: " + occurences);

            }

            stampaFrasi();
            }


    }

    public String getTestoString()
    {
        int cursoreRiga=1;
        String testo="";

        for(Frase f : listaFrasi)
        {
            while(cursoreRiga<f.getRiga())
            {
                testo = testo + '\n';
                cursoreRiga++;
            }

            testo = testo + f.getContenuto() + ". ";

        }
        return testo;


    }

    public void addListaFrasiCoda(Frase f)
    {
        listaFrasi.add(f);
    }

    public void inserisciFrase(Frase f)
    {
        int rigaSuccessiva = listaFrasi.getFirst().getRiga();
        int indiceSuccessivo = 0;

        while(rigaSuccessiva < f.getRiga())
        {
            indiceSuccessivo++;
            rigaSuccessiva = listaFrasi.get(indiceSuccessivo).getRiga();
        }

        if(rigaSuccessiva==f.getRiga())
        {
            int ordineSuccessivo = listaFrasi.get(indiceSuccessivo).getOrdine();

            while(ordineSuccessivo < f.getOrdine() && listaFrasi.get(indiceSuccessivo).getRiga() == f.getRiga())
            {
                indiceSuccessivo++;
                ordineSuccessivo = listaFrasi.get(indiceSuccessivo).getOrdine();
            }

            listaFrasi.add(indiceSuccessivo, f);
            indiceSuccessivo++;
            rigaSuccessiva = listaFrasi.get(indiceSuccessivo).getRiga();
            ordineSuccessivo = listaFrasi.get(indiceSuccessivo).getOrdine();

            if(ordineSuccessivo == f.getOrdine())
            {
                while(f.getRiga() == rigaSuccessiva)
                {
                    Frase temp = listaFrasi.get(indiceSuccessivo);
                    temp.setOrdine(temp.getOrdine()+1);
                    listaFrasi.set(indiceSuccessivo, temp);
                    indiceSuccessivo++;

                    rigaSuccessiva = listaFrasi.get(indiceSuccessivo).getRiga();
                }
            }
        }
        else
        {
            listaFrasi.add(indiceSuccessivo, f);
        }



    }
    public void modificaFrase(Frase fraseCoinvolta, Frase fraseModificata)
    {
        listaFrasi.remove(fraseCoinvolta);
        listaFrasi.add(fraseModificata);
    }
    public void cancellaFrase(Frase f)
    {
        listaFrasi.remove(f);
    }

    private void stampaFrasi()
    {
        for(Frase f : listaFrasi)
            f.stampa();
    }
//controllo se una stringa ha solo caratteri speciali come " " o "\n"
    private boolean isStringaVuota(String s)
    {
        s = s.replace("\n", "");
        s = s.replace(" ", "");

        return s.isEmpty();
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
        String temp;
        int count=0;
        try {
            //conto per trovare la posizione di dove effettivamente comincia la frase
            while(str.charAt(count)== ' ' || str.charAt(count)=='\n')
            {
                count++;
            }

            //cancello tutti gli spazi presenti prima della frase e poi li concateno con la stringa che comincia dalla prima lettera della frase
            temp = str.substring(0, count);
            temp = temp.replace(" ", "");
            str = temp + str.substring(count);

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
