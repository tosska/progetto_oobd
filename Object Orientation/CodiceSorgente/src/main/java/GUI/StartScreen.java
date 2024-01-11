package GUI;

import Controller.Controller;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class StartScreen {
    private JPanel panel1;
    private JButton login;
    private JButton register;
    private JPanel panel2;
    private static JFrame frame;

    public Controller controller = new Controller();

    public StartScreen() {
        register.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Register registerGUI = new Register(frame, controller);
                registerGUI.frame.setVisible(true);
                frame.setVisible(false);
            }
        });

        login.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Login loginGUI = new Login(frame, controller);
                loginGUI.frame.setVisible(true);
                frame.setVisible(false);
            }
        });
    }


    public static void main(String[] args){
            frame = new JFrame("Start Screen");
            frame.setContentPane(new StartScreen().panel1);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.pack();
            frame.setVisible(true);
    }

}
