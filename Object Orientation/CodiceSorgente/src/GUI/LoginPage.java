package GUI;

import Controller.Controller;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class LoginPage {

    private JFrame frame = new JFrame();
    private JButton loginButton = new JButton("Login");
    private JTextField usernameField = new JTextField();
    private JPasswordField passwordField = new JPasswordField();
    private JLabel usernameLabel = new JLabel("Username:");
    private JLabel passwordLabel = new JLabel("Password:");
    private JLabel dontHaveAnAccountLabel = new JLabel("Don't have an account?");
    private JLabel signUpLabel = new JLabel("Sign Up");
    private JPanel leftPanel = new JPanel();
    private JPanel rightPanel = new JPanel();
    private JLabel iconUserLabel = new JLabel();
    private JLabel iconVisibleLabel = new JLabel();
    private JLabel iconNotVisibleLabel = new JLabel();
    private JLabel loginLabel = new JLabel("LOGIN");
    private JLabel copyrightLabel = new JLabel("copyright © danilo wiki All rights reserved");
    private Controller controllerPrincipale;
    private JLabel logoLabel = new JLabel();


    public LoginPage(Controller controller) {

        controllerPrincipale = controller;

        leftPanel.setBackground(new Color(47,69,92));
        leftPanel.setBounds(0, 0, 400, 500);
        leftPanel.setLayout(null);

        rightPanel.setBackground(Color.white);
        rightPanel.setBounds(400, 0, 400, 500);
        rightPanel.setLayout(null);

        loginLabel.setBounds(120, 30, 160, 50);
        loginLabel.setForeground(new Color(47,69,92));
        loginLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));


        // username
        usernameLabel.setBounds(60, 140, 70, 25);
        usernameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        usernameLabel.setForeground(Color.black);

        usernameField.setBounds(60, 165, 230, 25);

        ImageIcon userImagine = new ImageIcon(this.getClass().getResource("/icon/user.png"));
        iconUserLabel.setIcon(userImagine);
        iconUserLabel.setBounds(305, 165, 25, 25);


        // password
        passwordLabel.setBounds(60, 205, 70, 25);
        passwordLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        passwordLabel.setForeground(Color.black);

        passwordField.setBounds(60, 230, 230, 25);

        ImageIcon visibleImagine = new ImageIcon(this.getClass().getResource("/icon/visible.png"));
        iconVisibleLabel.setIcon(visibleImagine);
        iconVisibleLabel.setBounds(305, 230, 25, 25);
        iconVisibleLabel.setVisible(false);
        iconVisibleLabel.setEnabled(false);

        ImageIcon notVisibleImagine = new ImageIcon(this.getClass().getResource("/icon/notvisible.png"));
        iconNotVisibleLabel.setIcon(notVisibleImagine);
        iconNotVisibleLabel.setBounds(305, 230, 25, 25);
        iconNotVisibleLabel.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {

            }

            @Override
            public void mousePressed(MouseEvent e) {
                passwordField.setEchoChar((char)0);
                iconNotVisibleLabel.setVisible(false);
                iconNotVisibleLabel.setEnabled(false);
                iconVisibleLabel.setVisible(true);
                iconVisibleLabel.setEnabled(true);

            }

            @Override
            public void mouseReleased(MouseEvent e) {
                passwordField.setEchoChar((char)8226);
                iconVisibleLabel.setVisible(false);
                iconVisibleLabel.setEnabled(false);
                iconNotVisibleLabel.setVisible(true);
                iconNotVisibleLabel.setEnabled(true);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                iconNotVisibleLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });



        loginButton.setBounds(60, 300, 100, 35);
        loginButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        loginButton.setBackground(new Color(47,69,92));
        loginButton.setForeground(Color.white);
        loginButton.setFocusable(false);

        //usernameField.setText("lorenzo");
        //passwordField.setText("Password123@");

        usernameField.setText("gianna_bianchi");
        passwordField.setText("GiannaBianchi!456");

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String userID = usernameField.getText();
                String password = String.valueOf(passwordField.getPassword());


                if (controllerPrincipale.verificaUsername(userID)) {
                    if (controllerPrincipale.verificaPassword(userID, password)) {
                        frame.dispose();    // la schermata di login non ci serve più (va bene?)

                        controllerPrincipale.impostaUtilizzatore(userID);
                        controllerPrincipale.caricaPagineCreate();

                        // elimino il contenuto dei text field così se l'utente decide di fare un logout questi saranno già vuoti
                        usernameField.setText("");
                        passwordField.setText("");

                        WelcomePage welcomePage = new WelcomePage(controllerPrincipale, frame);
                    }
                    else {
                        JOptionPane.showMessageDialog(null, "Wrong password", "Alert", JOptionPane.ERROR_MESSAGE);
                    }
                }
                else {
                    JOptionPane.showMessageDialog(null, "Username not found", "Alert", JOptionPane.ERROR_MESSAGE);
                }

            }
        });

        dontHaveAnAccountLabel.setBounds(60, 350, 150, 20);

        signUpLabel.setBounds(200, 350, 50, 20);
        signUpLabel.setForeground(new Color(47,69,92));
        signUpLabel.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                frame.setVisible(false);

                // elimino il contenuto dei text field così se l'utente decide di ritornare indietro questi saranno già vuoti
                usernameField.setText("");
                passwordField.setText("");

                RegisterPage registerPage = new RegisterPage(controllerPrincipale, frame);
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {
                signUpLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });

        copyrightLabel.setBounds(80, 420, 300, 20);
        copyrightLabel.setForeground(new Color(210, 210, 210));

        ImageIcon logoImagine = new ImageIcon(this.getClass().getResource("/icon/logo.png"));
        logoLabel.setIcon(logoImagine);
        logoLabel.setBounds(0, 60, 350, 300);

        frame.add(leftPanel);
        frame.add(rightPanel);
        rightPanel.add(loginButton);
        rightPanel.add(dontHaveAnAccountLabel);
        rightPanel.add(signUpLabel);
        rightPanel.add(usernameField);
        rightPanel.add(usernameLabel);
        rightPanel.add(passwordField);
        rightPanel.add(passwordLabel);
        rightPanel.add(iconUserLabel);
        rightPanel.add(iconNotVisibleLabel);
        rightPanel.add(iconVisibleLabel);
        rightPanel.add(loginLabel);

        leftPanel.add(copyrightLabel);
        leftPanel.add(logoLabel);


        ImageIcon logo = new ImageIcon(this.getClass().getResource("/icon/wiki.png"));
        frame.setResizable(false);
        frame.setIconImage(logo.getImage());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 500);
        frame.setLayout(null);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }




}
