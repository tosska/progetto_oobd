package GUI;

import Controller.Controller;
import Model.Pagina;
import Model.Tema;
import Model.Testo;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import java.awt.*;
import java.awt.event.*;
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
    private JButton removeButton;
    private JButton insertButton;
    private JButton editButton;
    private JPanel titlePanel = new JPanel();
    private JPanel centralPanel = new JPanel();
    private JLabel chooseTheme = new JLabel("Choose Theme:");
    private JPanel bottomPanel = new JPanel();

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



    private void creationGUI() {
        textPane = new JTextPane();

        //textArea.setBackground(new Color(196, 220, 235));
        textPane.setFont(new Font("Arial", Font.PLAIN, 20));
        textPane.setForeground(Color.GRAY);
        textPane.setText("Write something...");
        textPane.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (textPane.getText().equals("Write something...")) {
                    textPane.setText("");
                    textPane.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (textPane.getText().isEmpty()) {
                    textPane.setForeground(Color.GRAY);
                    textPane.setText("Write something...");
                }
            }
        });

        scrollPane = new JScrollPane(textPane);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBounds(10, 10, 350, 350);

        removeButton = new JButton("Remove");
        removeButton.setBounds(385, 325, 120, 35);
        removeButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        removeButton.setBackground(new Color(47,69,92));
        removeButton.setForeground(Color.white);
        removeButton.setFocusable(false);

        insertButton = new JButton("Insert");
        insertButton.setBounds(385, 285, 120, 35);
        insertButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        insertButton.setBackground(new Color(47,69,92));
        insertButton.setForeground(Color.white);
        insertButton.setFocusable(false);

        editButton = new JButton("Edit");
        editButton.setBounds(385, 245, 120, 35);
        editButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        editButton.setBackground(new Color(47,69,92));
        editButton.setForeground(Color.white);
        editButton.setFocusable(false);

        centralPanel.setLayout(null);
        centralPanel.setBackground(new Color(194, 232, 255));
        centralPanel.setBounds(0, 50, 550, 370);
        MatteBorder borderCentral = BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(116,150,196));
        centralPanel.setBorder(borderCentral);

        //titleLabel = new JLabel("Titolo");
        //titleLabel.setBounds(10, 10, 75, 25);

        titleField = new JTextField("Insert title...");
        titleField.setBounds(10, 5, 200, 35);
        //titleField.setBackground(new Color(196, 220, 235));
        titleField.setFont(new Font("Arial", Font.PLAIN, 20));
        titleField.setForeground(Color.GRAY);
        titleField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (titleField.getText().equals("Insert title...")) {
                    titleField.setText("");
                    titleField.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (titleField.getText().isEmpty()) {
                    titleField.setForeground(Color.GRAY);
                    titleField.setText("Insert title...");
                }
            }
        });

        titlePanel.setLayout(null);
        titlePanel.setBackground(new Color(139, 183, 240));
        titlePanel.setBounds(0,0, 550, 50);
        MatteBorder borderTitle = BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(72,122,181));
        titlePanel.setBorder(borderTitle);
        //titlePanel.add(titleLabel);
        titlePanel.add(titleField);

        bottomPanel.setLayout(null);
        bottomPanel.setBackground(new Color(139, 183, 240));
        bottomPanel.setBounds(0,420, 550, 90);

        backButton = new JButton("Back");
        backButton.setBounds(10, 10, 70, 35);
        backButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        backButton.setBackground(new Color(47,69,92));
        backButton.setForeground(Color.white);
        backButton.setFocusable(false);

        submitButton = new JButton("Submit");
        submitButton.setBounds(90, 10, 120, 35);
        submitButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        submitButton.setBackground(new Color(47,69,92));
        submitButton.setForeground(Color.white);
        submitButton.setFocusable(false);

        // Creazione di un menu a tendina
        JComboBox<String> dropdownMenu = new JComboBox<>();
        ArrayList<Tema> listaTemi;
        listaTemi = controllerPrincipale.generaListaTemi();

        // Ciclo for-each per scorrere l'ArrayList
        for (Tema tema : listaTemi) {
            dropdownMenu.addItem(tema.getNome());
        }

        insertButton = new JButton("Insert");
        insertButton.setBounds(380, 415, 80, 25);

        editButton = new JButton("Edit");
        editButton.setBounds(380, 415, 80, 25);

        removeButton = new JButton("Remove");
        removeButton.setBounds(380, 415, 80, 25);


        dropdownMenu.setBounds(370, 30, 150, 25);
        chooseTheme.setBounds(370, 10, 100, 25);
        chooseTheme.setFont(new Font("Roboto", Font.BOLD, 13));
        chooseTheme.setForeground(new Color(47,69,92));




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
                String text = textPane.getText();

                // Ottenimento dell'indice della voce selezionata
                int selectedIndex = dropdownMenu.getSelectedIndex();

                Tema tema = listaTemi.get(selectedIndex);

                controllerPrincipale.creazionePagina(title, text, tema);

                frame.dispose();
                frameChiamante.setVisible(true);
            }
        });


        centralPanel.add(scrollPane);
        centralPanel.add(dropdownMenu);
        centralPanel.add(chooseTheme);
        centralPanel.add(removeButton);
        centralPanel.add(insertButton);
        centralPanel.add(editButton);

        bottomPanel.add(backButton);
        bottomPanel.add(submitButton);


        frame.add(titlePanel);
        frame.add(centralPanel);
        frame.add(bottomPanel);



        ImageIcon logo = new ImageIcon(this.getClass().getResource("/icon/wiki.png"));
        frame.setTitle("Edit page: " + controllerPrincipale.paginaAperta.getTitolo());
        frame.setResizable(false);
        frame.setIconImage(logo.getImage());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(550, 510);
        frame.setLayout(null);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.requestFocusInWindow();
    }

}
