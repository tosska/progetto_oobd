package GUI;

import Controller.Controller;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RegisterPage {
    private JFrame frame = new JFrame();
    private Controller controllerPrincipale;
    private JButton registerButton = new JButton("Register");
    private JTextField userIDField = new JTextField();
    private JTextField userEmailField = new JTextField();
    private JPasswordField userPasswordField = new JPasswordField();
    private JLabel userIDLabel = new JLabel("userID:");
    private JLabel userEmailLabel = new JLabel("email:");
    private JLabel userPasswordLabel = new JLabel("password:");
    private JLabel messageLabel = new JLabel();


    public RegisterPage(Controller controller, JFrame frameChiamante)
    {
        controllerPrincipale = controller;

        userIDLabel.setBounds(50, 100, 75, 25);
        userEmailLabel.setBounds(50, 150, 75, 25);
        userPasswordLabel.setBounds(50, 200, 75, 25);

        messageLabel.setBounds(125, 250, 250, 35);
        messageLabel.setFont(new Font(null, Font.ITALIC, 25));

        userIDField.setBounds(125, 100, 200, 25);
        userEmailField.setBounds(125, 150, 200, 25);
        userPasswordField.setBounds(125, 200, 200, 25);

        registerButton.setBounds(125, 250, 100, 25);
        registerButton.setFocusable(false);


        frame.add(userIDLabel);
        frame.add(userPasswordLabel);
        frame.add(userEmailLabel);
        frame.add(messageLabel);
        frame.add(userIDField);
        frame.add(userEmailField);
        frame.add(userPasswordField);
        frame.add(registerButton);


        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String userID = userIDField.getText();
                String email = userEmailField.getText();
                String password = String.valueOf(userPasswordField.getPassword());

                controllerPrincipale.aggiungiUtente(userID, email, password);

                frame.dispose();
                frameChiamante.setVisible(true);
            }
        });



        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(420, 420);
        frame.setLayout(null);
        frame.setVisible(true);
    }


}
