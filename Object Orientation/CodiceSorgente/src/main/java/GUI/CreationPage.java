package GUI;

import Controller.Controller;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CreationPage {
    private JButton creaPaginaButton;
    private JButton indietroButton;
    private JTextField titleField;
    private JTextArea textArea;

    private JPanel panel;
    public JFrame frame;

    public JFrame frameChiamante;

    public Controller controller;

    public CreationPage(JFrame frameChiamante, Controller controller) {
        this.controller = controller;
        frame= new JFrame("CreationPage");
        frame.setContentPane(panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

        creaPaginaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.creazionePagina(titleField.getText(), textArea.getText());
                System.out.println("pagina creata");
            }
        });
    }
}
