package GUI;

import Controller.Controller;
import Model.Pagina;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

// CLASSE PER DEBUG
public class WelcomePage {
    private JFrame frame = new JFrame();
    private Controller controllerPrincipale;
    private JLabel welcomeLabel = new JLabel("Hello!");
    private JButton createNewPageButton = new JButton("Crea nuova pagina");

    private JLabel searchLabel = new JLabel("Search:");
    private JTextField search = new JTextField();

    private  JButton searchButton = new JButton("Search");

    private JButton viewPageButton = new JButton("Visualizza ultima pagina creata"); //da cancellare

    WelcomePage(Controller controller) {
        controllerPrincipale = controller;
        String userID = controllerPrincipale.utilizzatore.getUsername();

        welcomeLabel.setBounds(0, 0, 200, 35);
        welcomeLabel.setFont(new Font(null, Font.PLAIN, 25));
        welcomeLabel.setText("Hello "+userID);

        createNewPageButton.setBounds(140, 170, 150, 25);
        createNewPageButton.setFocusable(false);

        viewPageButton.setBounds(140, 200, 150, 25);
        viewPageButton.setFocusable(false);

        searchLabel.setBounds(140, 80, 50, 25);
        search.setBounds(80, 100, 180, 25);
        searchButton.setBounds(270, 100, 80, 20);

        frame.add(welcomeLabel);
        frame.add(searchLabel);
        frame.add(search);
        frame.add(searchButton);
        frame.add(createNewPageButton);
        frame.add(viewPageButton);

        createNewPageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.setVisible(false);
                CreateNewPage createNewPage = new CreateNewPage(controllerPrincipale, frame);
            }
        });

        viewPageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.setVisible(false);
                Pagina p = controllerPrincipale.pagineCreate.get(3); //da cancellare
                PageGUI pageGUI = new PageGUI(controllerPrincipale, frame, p);
            }
        });

        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Pagina p = controllerPrincipale.cercaPagina(search.getText());
                PageGUI pageGUI = new PageGUI(controllerPrincipale, frame, p);
                frame.setVisible(false);
            }
        });


        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(420, 420);
        frame.setLayout(null);
        frame.setVisible(true);
    }
}
