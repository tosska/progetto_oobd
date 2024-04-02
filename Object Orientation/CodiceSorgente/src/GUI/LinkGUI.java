package GUI;

import Controller.Controller;
import Model.Collegamento;
import Model.Frase;
import Model.Pagina;
import Model.Tema;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class LinkGUI {

    private JFrame frame = new JFrame();
    private JFrame frameChiamante;
    private Controller controllerPrincipale;
    private JTextPane textPane;

    private  JScrollPane scrollPane;
    private JLabel titleLabel;

    private JLabel autoreLabel;
    private JButton backButton;
    private JButton insertLink;
    private JButton removeLink;
    private Pagina pagina; //la pagina aperta

    private JLabel selectedPhrase;


    LinkGUI(Controller controller, JFrame frameChiamante) { //da decidere se mandare la pagina tramite controller o tramite oggetto a se
        controllerPrincipale = controller;
        this.frameChiamante = frameChiamante;
        pagina = controller.paginaAperta;

        creationGUI();
        functionButton();

        textPane.setText(pagina.getTestoString());
    }

    private void creationGUI()
    {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("Manage links: " + pagina.getTitolo());
        frame.setSize(500, 500);
        frame.setLayout(null);
        frame.setLocationRelativeTo(null);

        textPane = new JTextPane();
        textPane.setEditable(false);
        textPane.setFont(new Font("Arial", Font.PLAIN, 20));
        textPane.setBounds(10, 50, 460, 350);



        titleLabel = new JLabel(pagina.getTitolo());
        titleLabel.setBounds(10, 10, 200, 25);

        autoreLabel = new JLabel("Di " + pagina.getAutore().getUsername());
        autoreLabel.setBounds(10, 25,  200, 25);

        backButton = new JButton("Back");
        backButton.setBounds(390, 10, 70, 25);

        selectedPhrase = new JLabel();
        selectedPhrase.setBounds(25, 410, 70, 25);

        insertLink = new JButton("Insert Link");
        insertLink.setBounds(350, 410, 100, 25);
        insertLink.setEnabled(false);

        removeLink = new JButton("Remove Link");
        removeLink.setBounds(280, 410, 100, 25);
        removeLink.setEnabled(false);

        //frame.add(textArea);
        frame.add(titleLabel);
        frame.add(autoreLabel);
        frame.add(textPane);
        frame.add(backButton);
        frame.add(insertLink);
        frame.add(removeLink);
        frame.add(selectedPhrase);
        frame.setVisible(true);
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
                Pagina paginaCollegamento = controllerPrincipale.cercaPagina(titolo);
                int riga = Integer.parseInt(selectedPhrase.getText().split(";")[0]);
                int ordine = Integer.parseInt(selectedPhrase.getText().split(";")[1]);

                controllerPrincipale.insertLink(pagina, riga, ordine, paginaCollegamento, controllerPrincipale.utilizzatore);
            }
        });

        removeLink.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null, "Collegamento rimosso", "Avviso", JOptionPane.INFORMATION_MESSAGE);
                int riga = Integer.parseInt(selectedPhrase.getText().split(";")[0]);
                int ordine = Integer.parseInt(selectedPhrase.getText().split(";")[1]);

                controllerPrincipale.removeLink(pagina, riga, ordine, controllerPrincipale.utilizzatore);

            }
        });

        textPane.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                controllerPrincipale.selezionaFrase(textPane.viewToModel2D(e.getPoint()));

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
