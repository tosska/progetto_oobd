package GUI;

import Controller.Controller;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;

public class AnteprimaGUI {
    private JFrame frame = new JFrame();
    private JFrame frameChiamante;
    private Controller controllerPrincipale;
    private JTextPane textArea;

    private JScrollPane scrollPane;
    private JLabel titleLabel;
    private JButton backButton;
    private JButton paginaButton;


    private JPanel titlePanel = new JPanel();
    private JPanel centralPanel = new JPanel();
    private JPanel bottomPanel = new JPanel();
    private JLabel authorLabel = new JLabel();

    AnteprimaGUI(Controller controller, JFrame frameChiamante) {
        controllerPrincipale = controller;
        this.frameChiamante = frameChiamante;

        creationGUI();
        functionButton();
        stampaTesto();

    }

    private void creationGUI() {
        titlePanel.setLayout(null);
        titlePanel.setBackground(new Color(139, 183, 240));
        titlePanel.setBounds(0,0, 500, 50);
        MatteBorder borderTitle = BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(72,122,181));
        titlePanel.setBorder(borderTitle);

        titleLabel = new JLabel(controllerPrincipale.paginaAperta.getTitolo() + " (" + controllerPrincipale.paginaAperta.getTema().getNome() + ")");
        titleLabel.setBounds(10, 10, 200, 25);
        titleLabel.setFont(new Font("Arial", Font.PLAIN, 20));

        centralPanel.setLayout(null);
        centralPanel.setBackground(new Color(194, 232, 255));
        centralPanel.setBounds(0, 50, 500, 420);
        MatteBorder borderCentral = BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(116,150,196));
        centralPanel.setBorder(borderCentral);

        textArea = new JTextPane();
        textArea.setEditable(false);
        textArea.setBounds(10, 50, 460, 350);
        textArea.setFont(new Font("Arial", Font.PLAIN, 20));


        scrollPane = new JScrollPane(textArea);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBounds(10, 10, 460, 350);

        authorLabel.setBounds(10, 360, 300, 55);
        authorLabel.setText("<html> Proposed by: " + controllerPrincipale.paginaAperta.getAutore().getUsername() +
                "<br>Date: " + controllerPrincipale.paginaAperta.getDataCreazione().toString().split("\\.")[0] +  "</html>");
        authorLabel.setFont(new Font("Monospaced", Font.PLAIN, 15));

        bottomPanel.setLayout(null);
        bottomPanel.setBackground(new Color(139, 183, 240));
        bottomPanel.setBounds(0,470, 500, 90);

        backButton = new JButton("Back");
        backButton.setBounds(10, 10, 70, 35);
        backButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        backButton.setBackground(new Color(47,69,92));
        backButton.setForeground(Color.white);
        backButton.setFocusable(false);

        paginaButton = new JButton("Versione Attuale");
        paginaButton.setBounds(90, 10, 135, 35);
        paginaButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        paginaButton.setBackground(new Color(47,69,92));
        paginaButton.setForeground(Color.white);
        paginaButton.setFocusable(false);



        titlePanel.add(titleLabel);

        centralPanel.add(scrollPane);
        centralPanel.add(authorLabel);

        bottomPanel.add(backButton);
        bottomPanel.add(paginaButton);


        frame.add(titlePanel);
        frame.add(centralPanel);
        frame.add(bottomPanel);

        ImageIcon logo = new ImageIcon(this.getClass().getResource("/icon/wiki.png"));
        frame.setTitle(controllerPrincipale.paginaAperta.getTitolo());
        frame.setResizable(false);
        frame.setIconImage(logo.getImage());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 560);
        frame.setLayout(null);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.requestFocusInWindow();
    }



    private void functionButton() {
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                frameChiamante.setVisible(true);
            }
        });

        paginaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controllerPrincipale.paginaPrecedente = controllerPrincipale.paginaAperta;
                controllerPrincipale.paginaAperta = controllerPrincipale.getPaginaUtilizzatore(controllerPrincipale.paginaAperta.getId());
                PageGUI pageGUI = new PageGUI(controllerPrincipale, frame);
                frame.setVisible(false);
            }
        });

    }

    private void stampaTesto()
    {
        textArea.setText("");
        Document doc = textArea.getDocument();

        for(String f : controllerPrincipale.paginaAperta.getTestoRiferito().getFrasiString())
        {
            try {

                Style colore = attribuzioneColore(f);
                f = f.replace("##i", "");
                f= f.replace("##m", "");
                f= f.replace("##c", "");
                f= f.replace("##l", "");

                doc.insertString(doc.getLength(), f, colore);
            }
            catch (BadLocationException e)
            {
                System.out.println(e.getMessage());
            }

        }
    }
/*
    private void caricamentoTestoColori() {
        StyledDocument doc = textArea.getStyledDocument();
        String testo = controllerPrincipale.paginaAperta.getTestoString();
        Style style = textArea.addStyle("ColorStyle", null);
        String[] testoDiviso = testo.split("\\.");
        Color c;

        for (int i = 0; i < testoDiviso.length - 1; i++) {
            String s = testoDiviso[i];

            ArrayList<String> frasiNewLine = new ArrayList<>(Arrays.stream(s.split("\n")).toList());

            frasiNewLine.remove("");
            frasiNewLine.remove(" ");

            for (int j = 0; j < frasiNewLine.size(); j++) {
                String fraseNewLine = frasiNewLine.get(j);
                String punto = "";

                c = attribuzioneColore(fraseNewLine);

                StyleConstants.setForeground(style, c);

                if (j == frasiNewLine.size() - 1)
                    punto = ".";

                try {

                    if (c.equals(Color.black))
                        doc.insertString(doc.getLength(), s + punto, style);
                    else
                        doc.insertString(doc.getLength(), s.split("##")[0] + punto, style);
                } catch (BadLocationException e) {
                    e.printStackTrace();
                }

            }
        }
    }
*/
    public Style attribuzioneColore(String s) {
        Style stile = textArea.addStyle("ColorStyle", null);
        Color c;

        if (s.contains("##l")) {
            StyleConstants.setUnderline(stile, true);
            c = Color.orange;
        } else
            c = Color.black;

        if (s.contains("##i"))
            c = Color.blue;
        else if (s.contains("##m"))
            c = Color.green;
        else if (s.contains("##c"))
            c = Color.red;

        StyleConstants.setForeground(stile, c);
        return stile;
    }

}
