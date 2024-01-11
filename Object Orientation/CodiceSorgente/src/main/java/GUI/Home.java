package GUI;

import Controller.Controller;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Home {
    private JPanel panel;
    private JTextField textField1;
    private JButton searchButton;
    private JButton bottoneDaCancellarePerButton;
    public JFrame frame;

    public Controller controller;

    public Home(JFrame frameChiamante, Controller controller) {
        this.controller = controller;
        frame= new JFrame("HomeGUI");
        frame.setContentPane(panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PaginaGUI paginaGUI = new PaginaGUI(frame, controller);
                paginaGUI.frame.setVisible(true);
                frame.setVisible(false);
            }
        });
        bottoneDaCancellarePerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                CreationPage creationPage = new CreationPage(frame, controller);
                creationPage.frame.setVisible(true);
                frame.setVisible(false);
            }
        });
    }
}
