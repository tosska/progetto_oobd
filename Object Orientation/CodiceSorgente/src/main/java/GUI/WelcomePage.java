package GUI;

import Controller.Controller;

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

    WelcomePage(String userID, Controller controller) {
        controllerPrincipale = controller;

        welcomeLabel.setBounds(0, 0, 200, 35);
        welcomeLabel.setFont(new Font(null, Font.PLAIN, 25));
        welcomeLabel.setText("Hello "+userID);

        createNewPageButton.setBounds(140, 170, 150, 25);
        createNewPageButton.setFocusable(false);

        frame.add(welcomeLabel);
        frame.add(createNewPageButton);

        createNewPageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.setVisible(false);
                CreateNewPage createNewPage = new CreateNewPage(controllerPrincipale, frame, userID);
            }
        });


        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(420, 420);
        frame.setLayout(null);
        frame.setVisible(true);
    }
}
