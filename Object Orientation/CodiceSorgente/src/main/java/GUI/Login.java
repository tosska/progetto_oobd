package GUI;

import Controller.Controller;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Login {
    private JPanel panel;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JLabel usernamLabel;
    private JLabel passwordLabel;
    public JFrame frame;
    public Controller controller;

    public Login(JFrame frameChiamante, Controller controller) {
        this.controller = controller;
        frame= new JFrame("LoginGUI");
        frame.setContentPane(panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Home homeGUI = new Home(frame, controller);
                homeGUI.frame.setVisible(true);
                frame.setVisible(false);
            }
        });
    }
}