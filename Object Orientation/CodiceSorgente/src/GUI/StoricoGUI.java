package GUI;

import Controller.Controller;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class StoricoGUI {

    private JFrame frame = new JFrame();
    private JFrame frameChiamante;
    private Controller controllerPrincipale;
    private JPanel panelPrincipale = new JPanel();
    private JPanel bottomPanel = new JPanel();
    private JButton backButton = new JButton();

    public StoricoGUI(JFrame frameChiamante, Controller controller)
    {
        this.frameChiamante = frameChiamante;
        controllerPrincipale = controller;

        creationGUI();
    }

    private void creationGUI()
    {
        panelPrincipale.setLayout(new BorderLayout());
        panelPrincipale.setBackground(Color.white);
        panelPrincipale.setBounds(0, 0,900, 300);

        bottomPanel.setLayout(null);
        bottomPanel.setBackground(new Color(181, 212, 253));
        bottomPanel.setBounds(0, 300, 900, 100);

        backButton = new JButton("Back");
        backButton.setBounds(10, 10, 100, 40);
        backButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        backButton.setBackground(new Color(47,69,92));
        backButton.setForeground(Color.white);
        backButton.setFocusable(false);
        bottomPanel.add(backButton);
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                frameChiamante.setVisible(true);
                frameChiamante.requestFocusInWindow();
            }
        });


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


        for (int i = 0; i < controllerPrincipale.paginaAperta.getStorico().size(); i++)
        {
            String tipo = controllerPrincipale.paginaAperta.getStorico().get(i).getTipo();
            String fraseModificata="";
            if(tipo.equals("Modifica")) {
                fraseModificata = controllerPrincipale.paginaAperta.getStorico().get(i).getFraseModificata().getContenuto();
            }

            storico.addRow(new Object[]{tipo, controllerPrincipale.paginaAperta.getStorico().get(i).getProposta(),
                    controllerPrincipale.paginaAperta.getStorico().get(i).getFraseCoinvolta().getRiga(),
                    controllerPrincipale.paginaAperta.getStorico().get(i).getFraseCoinvolta().getOrdine(),
                    controllerPrincipale.paginaAperta.getStorico().get(i).getFraseCoinvolta().getContenuto(),
                    fraseModificata,
                    controllerPrincipale.paginaAperta.getStorico().get(i).getUtente().getUsername(),
                    controllerPrincipale.paginaAperta.getStorico().get(i).getData()});
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
        frame.add(bottomPanel);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("Page: " + controllerPrincipale.paginaAperta.getTitolo());
        frame.setSize(900, 400);
        frame.setLayout(null);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }



}
