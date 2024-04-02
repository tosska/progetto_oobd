package GUI;

import Controller.Controller;
import Model.*;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class EditPage {

    private JFrame frame = new JFrame();

    private JFrame frameChiamante;
    private Controller controllerPrincipale;
    private JTextPane textPane;
    private JScrollPane scrollPane;
    private JLabel titleLabel;
    private JTextField titleField;
    private JButton backButton;

    private JButton submitButton;
    private JButton insertButton;
    private JButton editButton;
    private JButton removeButton;

    EditPage(Controller controller, JFrame frameChiamante) {
        controllerPrincipale = controller;
        this.frameChiamante = frameChiamante;
        String userID = controllerPrincipale.utilizzatore.getUsername();

        creationGUI();
        functionButton();
        stampaTesto();

        if(!controller.checkAutore())
        {
            if(controller.getTestoConProposte()!=null)
            {
                JOptionPane.showMessageDialog(null, "Sono state caricate le precedenti proposte fatte su questa pagina", "Avviso", JOptionPane.INFORMATION_MESSAGE);
                textPane.setText(controller.getTestoConProposte().getTestoString());
            }
        }
    }

    private void stampaTesto()
    {
        Document doc = textPane.getDocument();
        Style collegamento = textPane.addStyle("ColorStyle", null);
        StyleConstants.setUnderline(collegamento, true);
        StyleConstants.setForeground(collegamento, Color.BLUE);
        Style attuale;

        for(String f : controllerPrincipale.getFrasiPaginaAperta())
        {
            try {

                if (f.contains("##l")) {
                    attuale = collegamento;
                    f = f.replace("##l", "");
                }
                else
                    attuale = null;

                doc.insertString(doc.getLength(), f, attuale);
            }
            catch (BadLocationException e)
            {
                System.out.println(e.getMessage());
            }

        }
    }

    private void functionButton()
    {
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



            }
        });

        textPane.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {

                super.mouseClicked(e);
                controllerPrincipale.selezionaFrase(textPane.viewToModel2D(e.getPoint()));
            }
        });

        insertButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });

        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });

        removeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });




    }

    private void creationGUI()
    {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("Edit page: " + controllerPrincipale.paginaAperta.getTitolo());
        frame.setSize(500, 500);
        frame.setLayout(null);
        frame.setLocationRelativeTo(null);

        textPane = new JTextPane();
        textPane.setFont(new Font("Arial", Font.PLAIN, 20));
        textPane.setEditable(false);


        scrollPane = new JScrollPane(textPane);
        // scrollPane.setPreferredSize(new Dimension(450, 450));
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBounds(10, 50, 460, 350);

        titleLabel = new JLabel("Titolo");
        titleLabel.setBounds(10, 10, 75, 25);

        titleField = new JTextField(controllerPrincipale.paginaAperta.getTitolo());
        titleField.setBounds(50, 10, 200, 25);

        backButton = new JButton("Back");
        backButton.setBounds(390, 10, 70, 25);

        submitButton = new JButton("Submit");
        submitButton.setBounds(380, 415, 80, 25);

        insertButton = new JButton("Insert");
        insertButton.setBounds(380, 415, 80, 25);

        editButton = new JButton("Edit");
        editButton.setBounds(380, 415, 80, 25);

        removeButton = new JButton("Remove");
        removeButton.setBounds(380, 415, 80, 25);


        frame.add(scrollPane);
        frame.add(titleLabel);
        frame.add(titleField);
        frame.add(backButton);
        frame.add(insertButton);
        frame.add(removeButton);
        frame.add(editButton);
        frame.setVisible(true);
    }

}
