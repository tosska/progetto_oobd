package GUI;

import Controller.Controller;
import Model.Pagina;
import Model.Testo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class EditPage {

    private JFrame frame = new JFrame();

    private  JFrame frameChiamante;
    private Controller controllerPrincipale;
    private JTextArea textArea;
    private JScrollPane scrollPane;
    private JLabel titleLabel;
    private JTextField titleField;
    private JButton backButton;
    private JButton submitButton;

    private Pagina editPg; //la pagina che verrà poi modificata

    EditPage(Controller controller, JFrame frameChiamante, Pagina pagina) {
        controllerPrincipale = controller;
        this.frameChiamante = frameChiamante;
        String userID = controllerPrincipale.utilizzatore.getUsername();
        editPg = pagina;

        creationGUI();

        functionButton();
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

        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                Testo testoModificato = new Testo(editPg);
                testoModificato.setTestoString(textArea.getText());

                controllerPrincipale.caricaModifichePagina(editPg, testoModificato, !checkAutore()); //da modificare la gestione del parametro proposta

            }
        });
    }

    private void creationGUI()
    {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("Edit page: " + editPg.getTitolo());
        frame.setSize(500, 500);
        frame.setLayout(null);
        frame.setLocationRelativeTo(null);

        textArea = new JTextArea();
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        //textArea.setFont(new Font("Arial", Font.PLAIN, 20));
        String testo = editPg.getTestoString().replace("-\n", "");
        textArea.setText(testo);
        textArea.setFont(new Font("Arial", Font.PLAIN, 20));

        scrollPane = new JScrollPane(textArea);
        // scrollPane.setPreferredSize(new Dimension(450, 450));
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBounds(10, 50, 460, 350);

        titleLabel = new JLabel("Titolo");
        titleLabel.setBounds(10, 10, 75, 25);

        titleField = new JTextField(editPg.getTitolo());
        titleField.setBounds(50, 10, 200, 25);

        backButton = new JButton("Back");
        backButton.setBounds(390, 10, 70, 25);

        submitButton = new JButton("Submit");
        submitButton.setBounds(380, 415, 80, 25);


        frame.add(scrollPane);
        frame.add(titleLabel);
        frame.add(titleField);
        frame.add(backButton);
        frame.add(submitButton);
        frame.setVisible(true);
    }

    private boolean checkAutore()
    {
        if(controllerPrincipale.utilizzatore.getUsername().equals(editPg.getAutore()))
            return true;
        else
            return false;
    }
}
