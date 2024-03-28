package GUI;

import Controller.Controller;
import Model.Tema;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class CreateNewPage{
    private JFrame frame = new JFrame();
    private Controller controllerPrincipale;
    private JTextArea textArea;
    private JScrollPane scrollPane;
    private JLabel titleLabel;
    private JTextField titleField;
    private JButton backButton;
    private JButton submitButton;
    CreateNewPage(Controller controller, JFrame frameChiamante) {
        controllerPrincipale = controller;


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

        // Creazione di un menu a tendina
        JComboBox<String> dropdownMenu = new JComboBox<>();
        ArrayList<Tema> listaTemi;
        listaTemi = controllerPrincipale.generaListaTemi();

        // Ciclo for-each per scorrere l'ArrayList
        for (Tema tema : listaTemi) {
            dropdownMenu.addItem(tema.getNome());
        }


        dropdownMenu.setBounds(270, 10, 100, 25);



        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                frameChiamante.setVisible(true);
                frameChiamante.requestFocusInWindow();
            }
        });

        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String title = titleField.getText();
                String text = textArea.getText();

                // Ottenimento dell'indice della voce selezionata
                int selectedIndex = dropdownMenu.getSelectedIndex();

                Tema tema = listaTemi.get(selectedIndex);

                controllerPrincipale.creazionePagina(title, text, tema);

                frame.dispose();
                frameChiamante.setVisible(true);
            }
        });

        frame.add(scrollPane);
        frame.add(titleLabel);
        frame.add(titleField);
        frame.add(backButton);
        frame.add(submitButton);
        frame.add(dropdownMenu);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("Create new page");
        frame.setSize(500, 500);
        frame.setLayout(null);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

}
