package Model;

import javax.swing.text.Utilities;
import java.util.ArrayList;

public class Testo implements Cloneable{
    private ArrayList<Frase> listaFrasi;
    private Pagina paginaRiferita;
    private int lunghezzaRiga; //da capire se cancellare o meno

    private String font; //da valutare l'idea

    public Testo(Pagina paginaRiferita) {

        setPaginaRiferita(paginaRiferita);
        listaFrasi = new ArrayList<>();
        lunghezzaRiga = 50;
    }

    public Pagina getPaginaRiferita() { return paginaRiferita; }
    public ArrayList<Frase> getListaFrasi() { return listaFrasi; }

    public void setListaFrasi(ArrayList<Frase> listaFrasi) {this.listaFrasi = listaFrasi;}
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
                //frasePulita = f.replace("\n", "");

                try {
                    while(f.charAt(0) == '\n') //se ad inizio frase vi è un new line incremento il valore di riga
                    {
                        riga++;
                        ordine=1;
                        f = f.substring(1); //li tolgo perchè non le devo più contare per far incrementare riga
                    }
                }
                catch (Exception eg){
                    System.out.println(eg.getMessage());
                }

                String[] frasiNewLine = f.split("\n");

                if(frasiNewLine.length==1) {
                    Frase frase = new Frase(riga, ordine, f + ".", this);
                    listaFrasi.add(frase);
                }
                else {
                    for(int i=0; i<frasiNewLine.length; i++)
                    {
                        String contenutoFrase = frasiNewLine[i];

                        if(!isStringaVuota(contenutoFrase))
                        {
                            if(i==frasiNewLine.length-1)
                                contenutoFrase += ". ";

                            Frase frase = new Frase(riga, ordine, contenutoFrase, this);
                            listaFrasi.add(frase);

                            if(i<frasiNewLine.length-1)
                                riga++;

                        }
                    }

                    ordine=0;
                }
            }
        }
    }

    public Testo clonaTesto()
    {
        Testo testo;
        try {
            testo = (Testo) super.clone();
            testo.listaFrasi = new ArrayList<>();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }

        for(Frase f : this.listaFrasi)
        {
            Frase clone;

            if(f instanceof Collegamento)
                clone = new Collegamento(f.getRiga(), f.getOrdine(), f.getContenuto(), testo, ((Collegamento) f).getPaginaCollegata());
            else
                clone = new Frase(f.getRiga(), f.getOrdine(), f.getContenuto(), testo);
            testo.listaFrasi.add(clone);
        }

        return testo;
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

            testo = testo + f.getContenuto() + " ";

        }
        return testo;


    }

    public ArrayList<String> getFrasiString()
    {
        int cursoreRiga=1;
        ArrayList<String> frasi = new ArrayList<>();

        for(Frase f : listaFrasi)
        {
            while(cursoreRiga<f.getRiga())
            {
                frasi.add("\n");
                cursoreRiga++;
            }

            if(f instanceof Collegamento)
                frasi.add(f.getContenuto() + "##l ");
            else
                frasi.add(f.getContenuto() + " ");
        }
        return frasi;
    }

    public void addListaFrasiCoda(Frase f)
    {
        listaFrasi.add(f);
    }

    //modificare forse indicando parametri di riga ordine e contenuto
    public void inserisciFrase(Frase f, Boolean anteprima)
    {
        if(anteprima)
        {
            if(f.getContenuto().contains("."))
                f.setContenuto(f.getContenuto().substring(0, f.getContenuto().length()-1) + "##i.");
            else
                f.setContenuto(f.getContenuto() + "##i");
        }

        int rigaSuccessiva = listaFrasi.getFirst().getRiga();
        int indiceSuccessivo = 0;

        //posiziniamoci sull'indice dove è presente la prima frase che ha la riga >= alla riga dove stiamo inserendo la frase
        while(rigaSuccessiva < f.getRiga() && indiceSuccessivo<listaFrasi.size()-1)
        {
            indiceSuccessivo++;
            rigaSuccessiva = listaFrasi.get(indiceSuccessivo).getRiga();
        }

        //esistono altre frasi sulla riga dove stiamo inserendo
        if(rigaSuccessiva==f.getRiga())
        {
            int ordineSuccessivo = listaFrasi.get(indiceSuccessivo).getOrdine();
            int elementoIniziale = rigaSuccessiva;

            //posizioniamoci sulla prima frase che ha ordine maggiore di quello di inserimento
            while(ordineSuccessivo < f.getOrdine() && rigaSuccessiva == f.getRiga() && indiceSuccessivo<listaFrasi.size()-1)
            {
                indiceSuccessivo++;
                ordineSuccessivo = listaFrasi.get(indiceSuccessivo).getOrdine();
                rigaSuccessiva = listaFrasi.get(indiceSuccessivo).getRiga();
            }

            if(indiceSuccessivo==listaFrasi.size()-1)
                listaFrasi.add(f);
            else
            {
                listaFrasi.add(indiceSuccessivo, f);
                indiceSuccessivo++;

                //riordinamento
                while(f.getRiga() == rigaSuccessiva && indiceSuccessivo < listaFrasi.size()-1)
                {
                    Frase temp = listaFrasi.get(indiceSuccessivo);
                    temp.setOrdine(temp.getOrdine()+1);
                    listaFrasi.set(indiceSuccessivo, temp);
                    indiceSuccessivo++;

                    rigaSuccessiva = listaFrasi.get(indiceSuccessivo).getRiga();
                }


            }
        }
        else //non esistono altre frasi sulla riga (è quindi la prima frase)
        {
            //è primo nella riga ed esistono altre frasi nel testo
            if(rigaSuccessiva>f.getRiga())
                listaFrasi.add(indiceSuccessivo, f);
            else //è il primo della riga ed è anche la prima frase del testo
                listaFrasi.add(f);
        }
    }

    //modificare forse indicando parametri di riga ordine e contenuto
    public void modificaFrase(Frase fraseModificata, boolean anteprima)
    {
        int posizione = getIndiceFrase(fraseModificata.getRiga(), fraseModificata.getOrdine());

        if(anteprima)
        {
            if(fraseModificata.getContenuto().contains("."))
                fraseModificata.setContenuto(fraseModificata.getContenuto().substring(0, fraseModificata.getContenuto().length()-1) + "##m.");
            else
                fraseModificata.setContenuto(fraseModificata.getContenuto() + "##m");
        }
        listaFrasi.set(posizione, fraseModificata);
    }

    //modificare forse indicando parametri di riga ordine e contenuto
    public void cancellaFrase(Frase f, boolean anteprima)
    {
        int posizione = getIndiceFrase(f.getRiga(), f.getOrdine());

        if(anteprima) {
            Frase frase = new Frase(f.getRiga(), f.getOrdine(), f.getContenuto(), this);

            if(frase.getContenuto().contains("."))
                frase.setContenuto(frase.getContenuto().substring(0, frase.getContenuto().length()-1) + "##c.");
            else
                frase.setContenuto(frase.getContenuto() + "##c");
            listaFrasi.set(posizione, frase);
        }
        else
            listaFrasi.remove(posizione);

    }

    public int getIndiceFrase(int riga, int ordine)
    {
        for(int i=0; i< listaFrasi.size(); i++)
        {
            if(listaFrasi.get(i).getRiga() == riga && listaFrasi.get(i).getOrdine() == ordine )
                return i;
        }

        return -1;
    }

    public Frase getFrase(int riga, int ordine)
    {
        for(Frase f : listaFrasi)
        {
            if(f.getRiga() == riga && f.getOrdine() == ordine)
            {
                return f;
            }
        }

        return null;
    }

//controllo se una stringa ha solo caratteri speciali come " " o "\n"
    private boolean isStringaVuota(String s)
    {
        s = s.replace("\n", "");
        s = s.replace(" ", "");

        return s.isEmpty();
    }


    private String formattaTesto(String testo)
    {
        testo = testo + "\n"; // da valutare se la soluzione migliore
        String[] splitLine = testo.split("\\n");
        String textFormatted="";
        String aCapo;
        int posizioneNewLine ;

        for(String line : splitLine)
        {
            while(line.length() > lunghezzaRiga-1)
            {
                posizioneNewLine = lunghezzaRiga;

                if(line.charAt(lunghezzaRiga-1)!=' ' && line.charAt(lunghezzaRiga-1)!='\n' && line.charAt(lunghezzaRiga-1)!='.' && line.contains(" ")) {
                    while(line.charAt(posizioneNewLine-1) != ' ')
                        posizioneNewLine--;
                }


                textFormatted = textFormatted + line.substring(0, posizioneNewLine) + "\n";
                line = line.substring(posizioneNewLine, line.length());
            }

            textFormatted = textFormatted + line + "\n";
        }

        System.out.println("Sono il testo formattato: " + textFormatted);
        return textFormatted;
    }

    public void aggiorna()
    {
        this.setTestoString(this.getTestoString());
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

    public int getLunghezzaRiga() {
        return lunghezzaRiga;
    }

    public void setLunghezzaRiga(int lunghezzaRiga) {
        this.lunghezzaRiga = lunghezzaRiga;
    }

    public Testo getTestoSelezionabile()
    {
        Testo t = clonaTesto();

        for(int i=0; i<t.listaFrasi.size(); i++)
        {
            Frase f = t.listaFrasi.get(i);
            f.setContenuto("\u21921;2\u2192" + f.getContenuto());
            t.listaFrasi.set(i, f);
        }

        return t;
    }
}
