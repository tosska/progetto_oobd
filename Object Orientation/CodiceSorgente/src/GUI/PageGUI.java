package GUI;

import Controller.Controller;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;

public class PageGUI {
    private JFrame frame = new JFrame();
    private JFrame frameChiamante;
    private Controller controllerPrincipale;
    private JTextPane textPane;
    private  JScrollPane scrollPane;
    private JLabel titleLabel;
    private JButton backButton;
    private JButton editButton;
    private JButton linkButton;
    private JPanel titlePanel = new JPanel();
    private JPanel centralPanel = new JPanel();
    private JPanel bottomPanel = new JPanel();
    private JLabel authorLabel = new JLabel();


    PageGUI(Controller controller, JFrame frameChiamante) { //da decidere se mandare la pagina tramite controller o tramite oggetto a se
        controllerPrincipale = controller;
        this.frameChiamante = frameChiamante;

        if(controllerPrincipale.checkAutore()) {
            controllerPrincipale.caricaStoricoDaPagina(controllerPrincipale.paginaAperta);
            controllerPrincipale.paginaAperta.getStorico().stampaOperazioni();
        }

        creationGUI();
        functionButton();
        stampaTesto();

    }

    private void stampaTesto() {
        Document doc = textPane.getDocument();
        Style collegamento = textPane.addStyle("ColorStyle", null);
        StyleConstants.setUnderline(collegamento, true);
        StyleConstants.setForeground(collegamento, Color.BLUE);
        Style attuale;

        for(String f : controllerPrincipale.getFrasiPaginaAperta())
        {
            String frase = f;
            try {

                if (frase.contains("##l")) {
                    attuale = collegamento;
                    frase = frase.replace("##l", "");
                }
                else
                    attuale = null;

                doc.insertString(doc.getLength(), frase, attuale);
            }
            catch (BadLocationException e)
            {
                System.out.println(e.getMessage());
            }

        }
    }

    private void creationGUI()
    {
        String editTesto;
        if(controllerPrincipale.checkAutore()) {
            editTesto = "Edit";
        }
        else {
            editTesto = "Propose Edit";
        }

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

        textPane = new JTextPane();
        textPane.setEditable(false);
        textPane.setBounds(10, 50, 460, 350);
        textPane.setFont(new Font("Arial", Font.PLAIN, 20));



        scrollPane = new JScrollPane(textPane);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBounds(10, 10, 460, 350);

        authorLabel.setBounds(10, 360, 300, 55);
        authorLabel.setText("<html> Author: " + controllerPrincipale.paginaAperta.getAutore().getUsername() +
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

        editButton = new JButton(editTesto);
        editButton.setBounds(90, 10, 120, 35);
        editButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        editButton.setBackground(new Color(47,69,92));
        editButton.setForeground(Color.white);
        editButton.setFocusable(false);

        linkButton = new JButton("Manage Links");
        linkButton.setBounds(220, 10, 120, 35);
        linkButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        linkButton.setBackground(new Color(47,69,92));
        linkButton.setForeground(Color.white);
        linkButton.setFocusable(false);


        titlePanel.add(titleLabel);

        centralPanel.add(scrollPane);
        centralPanel.add(authorLabel);

        bottomPanel.add(backButton);
        bottomPanel.add(editButton);
        bottomPanel.add(linkButton);


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
                controllerPrincipale.paginaAperta = controllerPrincipale.paginaPrecedente;
                controllerPrincipale.paginaPrecedente=null;
                frameChiamante.setVisible(true);
            }
        });

        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.setVisible(false);
                EditPage editPage = new EditPage(controllerPrincipale, frame);
            }
        });

        linkButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.setVisible(false);
                LinkGUI linkGUI = new LinkGUI(controllerPrincipale, frame);
            }
        });

        textPane.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                controllerPrincipale.selezionaFrase(textPane.viewToModel2D(e.getPoint()), false);

                if (controllerPrincipale.PhraseIsLink()) {
                    controllerPrincipale.attivazioneCollegamento();
                    frame.setVisible(false);
                    PageGUI pageGUI = new PageGUI(controllerPrincipale, frame);
                }
            }
        });
    }





}
