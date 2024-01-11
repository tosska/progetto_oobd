package GUI;

import Controller.Controller;

import javax.swing.*;

public class PaginaGUI {
    private JPanel panel;
    private JLabel titleField;
    private JTextArea textArea1;
    private JButton modificaButton;
    private JButton indietroButton;
    private JLabel infoLabel;
    private JPanel panelTesto;
    private JPanel panelOperazioni;
    private JPanel panelInfo;
    public JFrame frame;

    public Controller controller;

    public PaginaGUI(JFrame frameChiamante, Controller controller) {
        this.controller = controller;
        frame = new JFrame("PaginaGUI");
        frame.setContentPane(panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
