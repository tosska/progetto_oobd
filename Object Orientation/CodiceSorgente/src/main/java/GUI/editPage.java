package GUI;

import Controller.Controller;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class editPage {

    private JFrame frame = new JFrame();
    private Controller controllerPrincipale;
    private JTextArea textArea;
    private JScrollPane scrollPane;
    private JLabel titleLabel;
    private JTextField titleField;
    private JButton backButton;
    private JButton submitButton;
    editPage(Controller controller, JFrame frameChiamante) {
        controllerPrincipale = controller;
        String userID = controllerPrincipale.utilizzatore.getUsername();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("Edit page");
        frame.setSize(500, 500);
        frame.setLayout(null);
        frame.setLocationRelativeTo(null);

        textArea = new JTextArea();
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setFont(new Font("Arial", Font.PLAIN, 20));

        scrollPane = new JScrollPane(textArea);
        // scrollPane.setPreferredSize(new Dimension(450, 450));
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBounds(10, 50, 460, 350);

        titleLabel = new JLabel("Titolo");
        titleLabel.setBounds(10, 10, 75, 25);

        titleField = new JTextField();
        titleField.setBounds(50, 10, 200, 25);

        backButton = new JButton("Back");
        backButton.setBounds(390, 10, 70, 25);

        submitButton = new JButton("Submit");
        submitButton.setBounds(380, 415, 80, 25);

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                frameChiamante.setVisible(true);
            }
        });

        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String title = titleField.getText();
                String text = textArea.getText();
                controllerPrincipale.creazionePagina(title, text);

                frame.dispose();
                frameChiamante.setVisible(true);
            }
        });

        frame.add(scrollPane);
        frame.add(titleLabel);
        frame.add(titleField);
        frame.add(backButton);
        frame.add(submitButton);
        frame.setVisible(true);
    }
}
