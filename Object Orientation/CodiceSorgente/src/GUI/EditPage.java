package GUI;

import Controller.Controller;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import java.awt.*;
import java.awt.event.*;

public class EditPage {

    private JFrame frame = new JFrame();
    private JFrame frameChiamante;
    private Controller controllerPrincipale;
    private JTextPane textPane;
    private JScrollPane scrollPane;
    private JTextField titleField;
    private JButton backButton;
    private JButton submitButton;
    private JPanel titlePanel = new JPanel();
    private JPanel centralPanel = new JPanel();
    private JLabel chooseTheme = new JLabel("Choose Theme:");
    private JPanel bottomPanel = new JPanel();

    EditPage(Controller controller, JFrame frameChiamante) {
        controllerPrincipale = controller;
        this.frameChiamante = frameChiamante;
        controllerPrincipale.caricamentoAnteprimaModifica();

        creationGUI();
        functionButton();
        stampaTesto(controllerPrincipale.paginaAperta.getTestoRiferito().getFrasiString());

    }

    private void stampaTesto(ArrayList<String> testo)
    {
        textPane.setText("");
        Document doc = textPane.getDocument();

        for(String f : testo)
        {
            try {

                Style colore = attribuzioneColore(f);
                f = f.replace("##i", "");
                f= f.replace("##m", "");
                f= f.replace("##c", "");
                f= f.replace("##l", "");

                doc.insertString(doc.getLength(), f, colore);
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


    }

    private void creationGUI() {
        textPane = new JTextPane();

        textPane.setFont(new Font("Arial", Font.PLAIN, 20));
        //textPane.setEditable(false);
        textPane.setBounds(10, 50, 460, 350);

        scrollPane = new JScrollPane(textPane);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBounds(10, 10, 460, 350);



        centralPanel.setLayout(null);
        centralPanel.setBackground(new Color(194, 232, 255));
        centralPanel.setBounds(0, 50, 650, 370);
        MatteBorder borderCentral = BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(116,150,196));
        centralPanel.setBorder(borderCentral);

        titleField = new JTextField(controllerPrincipale.paginaAperta.getTitolo());
        titleField.setBounds(10, 5, 200, 35);
        titleField.setFont(new Font("Arial", Font.PLAIN, 20));

        titlePanel.setLayout(null);
        titlePanel.setBackground(new Color(139, 183, 240));
        titlePanel.setBounds(0,0, 650, 50);
        MatteBorder borderTitle = BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(72,122,181));
        titlePanel.setBorder(borderTitle);
        titlePanel.add(titleField);

        bottomPanel.setLayout(null);
        bottomPanel.setBackground(new Color(139, 183, 240));
        bottomPanel.setBounds(0,420, 650, 90);

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

        for (int i = 0; i < controllerPrincipale.ListaTemi.size(); i++)
        {
            dropdownMenu.addItem(controllerPrincipale.ListaTemi.get(i).getNome());
        }


        dropdownMenu.setBounds(480, 30, 150, 25);
        chooseTheme.setBounds(480, 10, 100, 25);
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

                if(controllerPrincipale.isActivedProposal()) {
                    JOptionPane.showMessageDialog(null, "Le vecchie proposte ancora attive verranno rimosse.", "Avviso", JOptionPane.INFORMATION_MESSAGE);
                    controllerPrincipale.removeOldActiveProposal();
                }

                String title = titleField.getText();
                String text = textPane.getText();

                // Ottenimento dell'indice della voce selezionata
                int selectedIndex = dropdownMenu.getSelectedIndex();

                Tema tema = listaTemi.get(selectedIndex);

                controllerPrincipale.caricaModifichePagina(textPane.getText(), false);


                frame.dispose();
                frameChiamante.setVisible(true);
            }
        });


        centralPanel.add(scrollPane);
        centralPanel.add(dropdownMenu);
        centralPanel.add(chooseTheme);

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
        frame.setSize(650, 510);
        frame.setLayout(null);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.requestFocusInWindow();
    }


    public Style attribuzioneColore(String s)
    {
        Style stile = textPane.addStyle("ColorStyle", null);
        Color c;

        if(s.contains("##l")) {
            StyleConstants.setUnderline(stile, true);
            c = Color.orange;
        }
        else
            c = Color.black;

        if(s.contains("##i"))
            c = Color.blue;
        else if (s.contains("##m"))
            c = Color.green;
        else if (s.contains("##c"))
            c = Color.red;

        StyleConstants.setForeground(stile, c);
        return stile;
    }
}
