package GUI;

import Controller.Controller;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.Timestamp;

public class RegisterPage {
    private JFrame frame = new JFrame();
    private Controller controllerPrincipale;
    private JButton registerButton = new JButton("Sign Up");
    private JTextField usernameField = new JTextField();
    private JTextField mailField = new JTextField();
    private JPasswordField passwordField = new JPasswordField();
    private JLabel usernameLabel = new JLabel("Username:");
    private JLabel emailLabel = new JLabel("Email:");
    private JLabel passwordLabel = new JLabel("Password:");
    private JLabel iconUserLabel = new JLabel();
    private JLabel iconNotVisibleLabel = new JLabel();
    private JLabel iconVisibleLabel = new JLabel();
    private JLabel iconNotVisible2Label = new JLabel();
    private JLabel iconVisible2Label = new JLabel();
    private JLabel iconEmailLabel = new JLabel();
    private JLabel registerLabel = new JLabel("SIGN UP");
    private JPanel panel = new JPanel();
    private JLabel passwordConfLabel = new JLabel("Conferma password:");
    private JPasswordField passwordConfField = new JPasswordField();
    private JLabel alreadyHaveAnAccountLabel = new JLabel("Already have an account?");
    private JLabel loginLabel = new JLabel("Log in");


    public RegisterPage(Controller controller, JFrame frameChiamante)
    {
        controllerPrincipale = controller;

        panel.setBackground(Color.white);
        panel.setBounds(0, 0, 750, 400);
        panel.setLayout(null);

        registerLabel.setBounds(50, 30, 160, 50);
        registerLabel.setForeground(new Color(47,69,92));
        registerLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));

        // username
        usernameLabel.setBounds(60, 125, 70, 25);
        usernameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        usernameField.setBounds(60, 150, 230, 25);

        ImageIcon userImagine = new ImageIcon(this.getClass().getResource("/icon/user.png"));
        iconUserLabel.setIcon(userImagine);
        iconUserLabel.setBounds(305, 150, 25, 25);



        // email
        emailLabel.setBounds(60, 205, 70, 25);
        emailLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        mailField.setBounds(60, 230, 230, 25);

        ImageIcon emailImagine = new ImageIcon(this.getClass().getResource("/icon/mail.png"));
        iconEmailLabel.setIcon(emailImagine);
        iconEmailLabel.setBounds(305, 230, 25, 25);



        // password
        passwordLabel.setBounds(400, 125, 70, 25);
        passwordLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        passwordField.setBounds(400, 150, 230, 25);

        ImageIcon visibleImagine = new ImageIcon(this.getClass().getResource("/icon/visible.png"));
        iconVisibleLabel.setIcon(visibleImagine);
        iconVisibleLabel.setBounds(645, 150, 25, 25);
        iconVisibleLabel.setVisible(false);
        iconVisibleLabel.setEnabled(false);

        ImageIcon notVisibleImagine = new ImageIcon(this.getClass().getResource("/icon/notvisible.png"));
        iconNotVisibleLabel.setIcon(notVisibleImagine);
        iconNotVisibleLabel.setBounds(645, 150, 25, 25);
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



        // conferma password
        passwordConfLabel.setBounds(400, 205, 130, 25);
        passwordConfLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        passwordConfField.setBounds(400, 230, 230, 25);

        iconVisible2Label.setIcon(visibleImagine);
        iconVisible2Label.setBounds(645, 230, 25, 25);
        iconVisible2Label.setVisible(false);
        iconVisible2Label.setEnabled(false);

        iconNotVisible2Label.setIcon(notVisibleImagine);
        iconNotVisible2Label.setBounds(645, 230, 25, 25);
        iconNotVisible2Label.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {

            }

            @Override
            public void mousePressed(MouseEvent e) {
                passwordConfField.setEchoChar((char)0);
                iconNotVisible2Label.setVisible(false);
                iconNotVisible2Label.setEnabled(false);
                iconVisible2Label.setVisible(true);
                iconVisible2Label.setEnabled(true);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                passwordConfField.setEchoChar((char)8226);
                iconVisible2Label.setVisible(false);
                iconVisible2Label.setEnabled(false);
                iconNotVisible2Label.setVisible(true);
                iconNotVisible2Label.setEnabled(true);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                iconNotVisible2Label.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });


        registerButton.setBounds(50, 300, 300, 35);
        registerButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        registerButton.setBackground(new Color(47,69,92));
        registerButton.setForeground(Color.white);
        registerButton.setFocusable(false);

        alreadyHaveAnAccountLabel.setBounds(430, 310, 150, 20);

        loginLabel.setBounds(580, 310, 50, 20);
        loginLabel.setForeground(new Color(47,69,92));
        loginLabel.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                frame.setVisible(false);
                frameChiamante.setVisible(true);
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {
                loginLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });


        frame.add(panel);

        panel.add(usernameLabel);
        panel.add(passwordLabel);
        panel.add(usernameField);
        panel.add(passwordField);
        panel.add(mailField);
        panel.add(emailLabel);
        panel.add(iconUserLabel);
        panel.add(iconNotVisibleLabel);
        panel.add(iconVisibleLabel);
        panel.add(iconNotVisible2Label);
        panel.add(iconVisible2Label);
        panel.add(registerLabel);
        panel.add(registerButton);
        panel.add(iconEmailLabel);
        panel.add(passwordConfLabel);
        panel.add(passwordConfField);
        panel.add(alreadyHaveAnAccountLabel);
        panel.add(loginLabel);


        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String userID = usernameField.getText();
                String email = mailField.getText();
                String password = String.valueOf(passwordField.getPassword());
                String confPassword = String.valueOf(passwordConfField.getPassword());

                if (password.equals(confPassword))
                {
                    Timestamp dataIscrizione = new Timestamp(System.currentTimeMillis());
                    controllerPrincipale.aggiungiUtente(userID, email, password, dataIscrizione);
                    controllerPrincipale.impostaUtilizzatore(userID);

                    frame.dispose();
                    WelcomePage welcomePage = new WelcomePage(controllerPrincipale, frameChiamante);
                }
                else
                {
                    JOptionPane.showMessageDialog(null, "Wrong password", "Alert", JOptionPane.ERROR_MESSAGE);
                }

            }
        });


        ImageIcon logo = new ImageIcon(this.getClass().getResource("/icon/wiki.png"));
        frame.setIconImage(logo.getImage());
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(750, 400);
        frame.setLayout(null);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }


}
