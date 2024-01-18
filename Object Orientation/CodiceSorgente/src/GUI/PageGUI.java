package GUI;

import Controller.Controller;
import Model.Frase;
import Model.Pagina;
import Model.Storico;

import javax.swing.*;
import javax.swing.text.Utilities;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

public class PageGUI {

    private JFrame frame = new JFrame();

    private JFrame frameChiamante;
    private Controller controllerPrincipale;
    private JTextPane textPane;
    private JLabel titleLabel;
    private JButton backButton;

    private JButton editButton;
    private Pagina pagina; //la pagina aperta


    PageGUI(Controller controller, JFrame frameChiamante, Pagina p) { //da decidere se mandare la pagina tramite controller o tramite oggetto a se
        controllerPrincipale = controller;
        this.frameChiamante = frameChiamante;
        pagina = p;

        if(controlloAutore()) {
            controllerPrincipale.caricaStoricoDaPagina(pagina);
            pagina.getStorico().stampaOperazioni();
        }


        creationGUI();

        functionButton();

    }

    private void creationGUI()
    {
        String editTesto;
        int larghezza;
        if(controlloAutore()) {
            editTesto = "Edit";
            larghezza = 90;
        }
        else {
            editTesto = "Propose Edit";
            larghezza = 120;
        }

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("Page: " + pagina.getTitolo());
        frame.setSize(500, 500);
        frame.setLayout(null);
        frame.setLocationRelativeTo(null);


        textPane = new JTextPane();
        textPane.setBounds(10, 50, 460, 350);
        textPane.setText(pagina.getTestoString());
        textPane.setEditable(false);

        titleLabel = new JLabel(pagina.getTitolo());
        titleLabel.setBounds(10, 10, 200, 25);

        backButton = new JButton("Back");
        backButton.setBounds(390, 10, 70, 25);

        editButton= new JButton(editTesto);
        editButton.setBounds(350, 410, larghezza, 25);

        frame.add(titleLabel);
        frame.add(backButton);
        frame.add(textPane);
        frame.add(editButton);
        frame.setVisible(true);
    }

    private void functionButton()
    {
        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.setVisible(false);
                EditPage editPage = new EditPage(controllerPrincipale, frame, pagina);
            }
        });
    }

    private boolean controlloAutore()
    {
        if(controllerPrincipale.utilizzatore.getUsername().equals(pagina.getAutore()))
            return true;
        else
            return false;
    }


}
