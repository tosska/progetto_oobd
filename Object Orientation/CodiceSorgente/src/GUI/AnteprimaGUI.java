package GUI;

import Controller.Controller;

import javax.swing.*;
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
    private JLabel autoreLabel;
    private JButton backButton;
    private JButton paginaButton;

    AnteprimaGUI(Controller controller, JFrame frameChiamante) {
        controllerPrincipale = controller;
        this.frameChiamante = frameChiamante;

        creationGUI();
        functionButton();
        stampaTesto();

    }

    private void creationGUI() {

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("Anteprima: " + controllerPrincipale.paginaAperta.getTitolo());
        frame.setSize(500, 500);
        frame.setLayout(null);
        frame.setLocationRelativeTo(null);


        textArea = new JTextPane();
        textArea.setEditable(false);
        textArea.setFont(new Font("Arial", Font.PLAIN, 20));


        scrollPane = new JScrollPane(textArea);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBounds(10, 50, 460, 350);

        titleLabel = new JLabel(controllerPrincipale.paginaAperta.getTitolo());
        titleLabel.setBounds(10, 10, 200, 25);

        autoreLabel = new JLabel("Proposta di " + controllerPrincipale.paginaAperta.getAutore().getUsername());
        autoreLabel.setBounds(10, 25, 200, 25);

        backButton = new JButton("Back");
        backButton.setBounds(390, 10, 70, 25);

        paginaButton = new JButton("Versione Attuale");
        paginaButton.setBounds(330, 410, 135, 25);



        //frame.add(textArea);
        frame.add(titleLabel);
        frame.add(autoreLabel);
        frame.add(scrollPane);
        frame.add(backButton);
        frame.add(paginaButton);


        frame.setVisible(true);

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
