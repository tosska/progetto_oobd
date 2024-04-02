package GUI;

import Controller.Controller;
import Model.Pagina;

import javax.swing.*;
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

    private JLabel autoreLabel;
    private JButton backButton;

    private JButton editButton;
    private JButton linkButton;
    private Pagina pagina; //la pagina aperta


    PageGUI(Controller controller, JFrame frameChiamante) { //da decidere se mandare la pagina tramite controller o tramite oggetto a se
        controllerPrincipale = controller;
        this.frameChiamante = frameChiamante;
        pagina = controller.paginaAperta;

        if(controllerPrincipale.checkAutore()) {
            controllerPrincipale.caricaStoricoDaPagina(pagina);
            pagina.getStorico().stampaOperazioni();
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
            try {

                if (f.contains("##l")) {
                    attuale = collegamento;
                    f = f.replace("##l", "");
                }
                else
                    attuale = null;

                doc.insertString(doc.getLength(), f, attuale);
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
        int larghezza;
        if(controllerPrincipale.checkAutore()) {
            editTesto = "Edit";
            larghezza = 90;
        }
        else {
            editTesto = "Propose Edit";
            larghezza = 120;
        }

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle(pagina.getTitolo());
        frame.setSize(500, 500);
        frame.setLayout(null);
        frame.setLocationRelativeTo(null);


        textPane = new JTextPane();
        textPane.setEditable(false);
        textPane.setBounds(10, 50, 460, 350);
        textPane.setFont(new Font("Arial", Font.PLAIN, 20));



/*
        scrollPane = new JScrollPane(textPane);
        // scrollPane.setPreferredSize(new Dimension(450, 450));
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBounds(10, 50, 460, 350);

 */

        titleLabel = new JLabel(pagina.getTitolo());
        titleLabel.setBounds(10, 10, 200, 25);

        autoreLabel = new JLabel("Di " + pagina.getAutore().getUsername());
        autoreLabel.setBounds(10, 25,  200, 25);

        backButton = new JButton("Back");
        backButton.setBounds(390, 10, 70, 25);

        editButton= new JButton(editTesto);
        editButton.setBounds(350, 410, larghezza, 25);

        linkButton= new JButton("Manage links");
        linkButton.setBounds(25, 410, 115, 25);

        //frame.add(textArea);
        frame.add(titleLabel);
        frame.add(autoreLabel);
        frame.add(textPane);
        frame.add(backButton);
        frame.add(linkButton);
        frame.add(editButton);
        frame.setVisible(true);
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
                super.mouseClicked(e);
                controllerPrincipale.selezionaFrase(textPane.viewToModel2D(e.getPoint()));

                if (controllerPrincipale.PhraseIsLink()) {
                    controllerPrincipale.attivazioneCollegamento();
                    frame.setVisible(false);
                    PageGUI pageGUI = new PageGUI(controllerPrincipale, frame);
                }
            }
        });
    }




}
