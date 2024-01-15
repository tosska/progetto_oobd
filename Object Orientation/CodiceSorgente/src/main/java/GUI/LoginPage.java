package GUI;

import Controller.Controller;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginPage implements ActionListener {

    private JFrame frame = new JFrame();
    private Controller controllerPrincipale;
    private JButton loginButton = new JButton("Login");
    private JButton registerButton = new JButton("Register");
    private JTextField userIDField = new JTextField();
    private JPasswordField userPasswordField = new JPasswordField();
    private JLabel userIDLabel = new JLabel("userID:");
    private JLabel userPasswordLabel = new JLabel("password:");
    private JLabel messageLabel = new JLabel();

    LoginPage(Controller controller) {

        controllerPrincipale = controller;

        userIDLabel.setBounds(50, 100, 75, 25);
        userPasswordLabel.setBounds(50, 150, 75, 25);

        messageLabel.setBounds(125, 250, 250, 35);
        messageLabel.setFont(new Font(null, Font.ITALIC, 25));

        userIDField.setBounds(125, 100, 200, 25);
        userPasswordField.setBounds(125, 150, 200, 25);

        loginButton.setBounds(125, 200, 100, 25);
        loginButton.setFocusable(false);
        loginButton.addActionListener(this);

        registerButton.setBounds(225, 200, 100, 25);
        registerButton.setFocusable(false);
        registerButton.addActionListener(this);

        frame.add(userIDLabel);
        frame.add(userPasswordLabel);
        frame.add(messageLabel);
        frame.add(userIDField);
        frame.add(userPasswordField);
        frame.add(loginButton);
        frame.add(registerButton);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(420, 420);
        frame.setLayout(null);
        frame.setVisible(true);
    }


    @Override
    public void actionPerformed(ActionEvent e) {

        if(e.getSource() == registerButton) {
            // rivedere se la comunicazione tra frame è corretta o si può fare meglio (funziona anche così)
            frame.setVisible(false);
            RegisterPage registerPage = new RegisterPage(controllerPrincipale, frame);
        }

        if(e.getSource() == loginButton) {

            String userID = userIDField.getText();
            String password = String.valueOf(userPasswordField.getPassword());


            if (controllerPrincipale.verificaUsername(userID)) {
                if (controllerPrincipale.verificaPassword(userID, password)) {
                    messageLabel.setForeground(Color.green);
                    messageLabel.setText("Login succesful");
                    frame.dispose();    // la schermata di login non ci serve più (va bene?)

                    controllerPrincipale.impostaUtilizzatore(userID);
                    controllerPrincipale.caricaPagineCreate();

                    WelcomePage welcomePage = new WelcomePage(controllerPrincipale); //userid non va passato, bisogna creare un istanza di utente con gli attributi prelevati dal database
                }
                else {
                    messageLabel.setForeground(Color.red);
                    messageLabel.setText("Wrong password");
                }
            }
            else {
                messageLabel.setForeground(Color.red);
                messageLabel.setText("Username not found");
            }

        }
    };


}
