package GUI;

import Controller.Controller;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class LinkGUI {
    private JFrame frame = new JFrame();
    private JFrame frameChiamante;
    private Controller controllerPrincipale;
    private JTextPane textPane;
    private JLabel titleLabel;
    private JButton backButton;
    private JButton insertLink;
    private JButton removeLink;
    private JLabel selectedPhrase;

    private JPanel titlePanel = new JPanel();
    private JPanel centralPanel = new JPanel();
    private JPanel bottomPanel = new JPanel();
    private JLabel authorLabel = new JLabel();
    private JScrollPane scrollPane;


    LinkGUI(Controller controller, JFrame frameChiamante) { //da decidere se mandare la pagina tramite controller o tramite oggetto a se
        controllerPrincipale = controller;
        this.frameChiamante = frameChiamante;

        creationGUI();
        functionButton();

        stampaTesto(controller.paginaAperta.getTestoRiferito().getFrasiString());

    }

    private void stampaTesto(ArrayList<String> testo)
    {
        textPane.setText("");
        Document doc = textPane.getDocument();

        for(String f : testo)
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

    public Style attribuzioneColore(String s)
    {
        Style stile = textPane.addStyle("ColorStyle", null);
        Color c;

        if(s.contains("##l")) {
            StyleConstants.setUnderline(stile, true);
            c = Color.orange;
        }
        else
            c = Color.black;

        if(s.contains("##i"))
            c = Color.blue;
        else if (s.contains("##m"))
            c = Color.green;
        else if (s.contains("##c"))
            c = Color.red;

        StyleConstants.setForeground(stile, c);
        return stile;
    }

    private void creationGUI()
    {

        titlePanel.setLayout(null);
        titlePanel.setBackground(new Color(139, 183, 240));
        titlePanel.setBounds(0,0, 500, 50);
        MatteBorder borderTitle = BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(72,122,181));
        titlePanel.setBorder(borderTitle);

        titleLabel = new JLabel(controllerPrincipale.paginaAperta.getTitolo() + " (" + controllerPrincipale.paginaAperta.getTema().getNome() + ")");
        titleLabel.setBounds(10, 10, 350, 25);
        titleLabel.setFont(new Font("Arial", Font.PLAIN, 20));

        centralPanel.setLayout(null);
        centralPanel.setBackground(new Color(194, 232, 255));
        centralPanel.setBounds(0, 50, 500, 420);
        MatteBorder borderCentral = BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(116,150,196));
        centralPanel.setBorder(borderCentral);

        textPane = new JTextPane();
        textPane.setEditable(false);
        textPane.setBounds(10, 50, 460, 350);
        textPane.setFont(new Font("Arial", Font.PLAIN, 20));

        scrollPane = new JScrollPane(textPane);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBounds(10, 10, 460, 350);

        authorLabel.setBounds(10, 360, 300, 55);
        authorLabel.setText("<html> Author by: " + controllerPrincipale.paginaAperta.getAutore().getUsername() +
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

        removeLink = new JButton("Remove");
        removeLink.setBounds(90, 10, 120, 35);
        removeLink.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        removeLink.setBackground(new Color(47,69,92));
        removeLink.setForeground(Color.white);
        removeLink.setFocusable(false);
        removeLink.setEnabled(false);

        insertLink = new JButton("Insert");
        insertLink.setBounds(220, 10, 120, 35);
        insertLink.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        insertLink.setBackground(new Color(47,69,92));
        insertLink.setForeground(Color.white);
        insertLink.setFocusable(false);
        insertLink.setEnabled(false);

        selectedPhrase = new JLabel();
        selectedPhrase.setBounds(15, 15, 70, 25);


        titlePanel.add(titleLabel);

        centralPanel.add(scrollPane);
        centralPanel.add(authorLabel);

        bottomPanel.add(backButton);
        bottomPanel.add(insertLink);
        bottomPanel.add(removeLink);
        bottomPanel.add(selectedPhrase);


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



    private void functionButton()
    {
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                frameChiamante.setVisible(true);
            }
        });

        insertLink.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String titolo = JOptionPane.showInputDialog(null, "Inserisci titolo della pagina per il collegamento:", "Input", JOptionPane.QUESTION_MESSAGE);
                int riga = Integer.parseInt(selectedPhrase.getText().split(";")[0]);
                int ordine = Integer.parseInt(selectedPhrase.getText().split(";")[1]);

                try {
                    if(!titolo.equals(controllerPrincipale.paginaAperta.getTitolo())) {
                        controllerPrincipale.insertLink(riga, ordine, titolo);
                        JOptionPane.showMessageDialog(null, "Collegamento inserito", "Avviso", JOptionPane.INFORMATION_MESSAGE);
                    }
                    else
                        JOptionPane.showMessageDialog(null, "Impossibile collegare una frase alla\nstessa pagina su cui è presente", "Errore", JOptionPane.INFORMATION_MESSAGE);

                }
                catch (RuntimeException er) {
                    JOptionPane.showMessageDialog(null, er.getMessage(), "Errore", JOptionPane.INFORMATION_MESSAGE);
                }


                frame.dispose();
                frameChiamante.setVisible(true);
            }
        });

        removeLink.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int riga = Integer.parseInt(selectedPhrase.getText().split(";")[0]);
                int ordine = Integer.parseInt(selectedPhrase.getText().split(";")[1]);

                controllerPrincipale.removeLink(riga, ordine);

                JOptionPane.showMessageDialog(null, "Collegamento rimosso", "Avviso", JOptionPane.INFORMATION_MESSAGE);
                frame.dispose();
                frameChiamante.setVisible(true);

            }
        });

        textPane.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                controllerPrincipale.selezionaFrase(textPane.viewToModel2D(e.getPoint()), false);

                if(controllerPrincipale.PhraseIsSelected())
                {
                    insertLink.setEnabled(true);

                    if(controllerPrincipale.PhraseIsLink())
                        removeLink.setEnabled(true);
                    else
                        removeLink.setEnabled(false);

                    selectedPhrase.setText(controllerPrincipale.getRowSelectedPhrase() + ";" + controllerPrincipale.getOrderSelectedPhrase());
                }
                else {
                    insertLink.setEnabled(false);
                    removeLink.setEnabled(false);
                }

            }
        });

    }




}
