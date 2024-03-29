package GUI;

import Controller.Controller;
import Model.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.util.ArrayList;

public class StoricoGUI {

    private JFrame frame = new JFrame();
    private JFrame frameChiamante;
    private JTable table;
    private Controller controllerPrincipale;
    private Pagina pagina;
    private JPanel panelPrincipale = new JPanel();

    public StoricoGUI(JFrame frameChiamante, Controller controller)
    {
        this.frameChiamante = frameChiamante;
        controllerPrincipale = controller;
        this.pagina = controller.paginaAperta;

        panelPrincipale.setLayout(new BorderLayout());
        panelPrincipale.setBackground(Color.white);

        // Creazione del modello di tabella vuoto
        DefaultTableModel storico = new DefaultTableModel();
        storico.addColumn("Tipo");
        storico.addColumn("Proposta");
        storico.addColumn("Riga");
        storico.addColumn("Ordine");
        storico.addColumn("Frase Coinvolta");
        storico.addColumn("Frase modificata");
        storico.addColumn("Utente");
        storico.addColumn("Data");
        // Creazione della tabella con il modello vuoto
        JTable tabellaStorico = new JTable(storico);
        tabellaStorico.setEnabled(false); // Rende la tabella non modificabile

        ArrayList<Operazione> listaOp = pagina.getStorico().getListaOperazioni();

        for (Operazione o : listaOp)
        {
            String tipo = "";
            String fraseModificata = "";

            if (o instanceof Inserimento) {
                tipo = "Inserimento";
            } else if (o instanceof Modifica) {
                tipo = "Modifica";
                fraseModificata = ((Modifica) o).getFraseModificata().getContenuto();
            } else if (o instanceof Cancellazione) {
                tipo = "Cancellazione";
            }

            storico.addRow(new Object[]{tipo, o.getProposta(), o.getFraseCoinvolta().getRiga(), o.getFraseCoinvolta().getOrdine(),
            o.getFraseCoinvolta().getContenuto(), fraseModificata, o.getUtente().getUsername(), o.getData()});

        }




        // Imposta il rendering delle linee verticali su nessuna
        tabellaStorico.setShowVerticalLines(false);

        // Imposta l'altezza delle righe
        tabellaStorico.setRowHeight(30); // Imposta l'altezza desiderata delle righe


        // Renderer personalizzato per l'intestazione
        JTableHeader header = tabellaStorico.getTableHeader();
        header.setFont(new Font("Arial", Font.BOLD, 14)); // Imposta il font desiderato per l'intestazione
        header.setForeground(new Color(47,69,92)); // Imposta il colore del testo dell'intestazione
        header.setBackground(new Color(126,179,255)); // Imposta il colore di sfondo dell'intestazione
        header.setPreferredSize(new Dimension(header.getWidth(), 30)); // Imposta l'altezza desiderata dell'intestazione
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(47,69,92))); // Aggiunge una sottolineatura all'intestazione

        // Disabilita il riordinamento delle colonne
        header.setReorderingAllowed(false);


        panelPrincipale.add(new JScrollPane(tabellaStorico), BorderLayout.CENTER); // Utilizza uno JScrollPane per la visualizzazione della tabella

        frame.add(panelPrincipale);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("Page: " + pagina.getTitolo());
        frame.setSize(500, 500);
        frame.setLayout(new FlowLayout());
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }



}
