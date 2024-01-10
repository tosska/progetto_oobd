package GUI;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Register {
    public JFrame frame;
    private JPanel panel;
    private JTextField usernameField;
    private JTextField emailField;
    private JButton signUpButton;
    private JPasswordField passwordField1;
    private JPasswordField passwordField2;

    public Register(JFrame frameChiamante) {
        frame= new JFrame("RegisterGUI");
        frame.setContentPane(panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

        signUpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Login loginGUI = new Login(frame);
                loginGUI.frame.setVisible(true);
                frame.setVisible(false);
            }
        });
    }
}
