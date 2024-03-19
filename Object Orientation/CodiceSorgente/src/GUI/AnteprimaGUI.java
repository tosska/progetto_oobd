package GUI;

import Controller.Controller;
import Model.Frase;
import Model.Pagina;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AnteprimaGUI {

    private JFrame frame = new JFrame();

    private JFrame frameChiamante;
    private Controller controllerPrincipale;
    private JTextPane textArea;

    private  JScrollPane scrollPane;
    private JLabel titleLabel;

    private JLabel autoreLabel;
    private JButton backButton;

    private JButton paginaButton;
    private Pagina pagina; //la pagina aperta


    AnteprimaGUI(Controller controller, JFrame frameChiamante) {
        controllerPrincipale = controller;
        this.frameChiamante = frameChiamante;
        pagina = controller.paginaAperta;

        creationGUI();
        functionButton();

    }

    private void creationGUI()
    {

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("Anteprima: " + pagina.getTitolo());
        frame.setSize(500, 500);
        frame.setLayout(null);
        frame.setLocationRelativeTo(null);


        textArea = new JTextPane();
        textArea.setEditable(false);
        textArea.setFont(new Font("Arial", Font.PLAIN, 20));


        scrollPane = new JScrollPane(textArea);
        // scrollPane.setPreferredSize(new Dimension(450, 450));
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBounds(10, 50, 460, 350);

        titleLabel = new JLabel(pagina.getTitolo());
        titleLabel.setBounds(10, 10, 200, 25);

        autoreLabel = new JLabel("Proposta di " + pagina.getAutore().getUsername());
        autoreLabel.setBounds(10, 25,  200, 25);

        backButton = new JButton("Back");
        backButton.setBounds(390, 10, 70, 25);

        paginaButton = new JButton("Versione Attuale");
        paginaButton.setBounds(330, 410, 135, 25);


        caricamentoTestoColori();
        //frame.add(textArea);
        frame.add(titleLabel);
        frame.add(autoreLabel);
        frame.add(scrollPane);
        frame.add(backButton);
        frame.add(paginaButton);





        frame.setVisible(true);

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

        paginaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controllerPrincipale.paginaAperta = controllerPrincipale.getPaginaUtilizzatore(pagina.getId());
                PageGUI pageGUI = new PageGUI(controllerPrincipale, frame);
                frame.setVisible(false);
            }
        });

    }

    private void caricamentoTestoColori()
    {
        StyledDocument doc = textArea.getStyledDocument();
        String testo = pagina.getTestoString();
        Style style = textArea.addStyle("ColorStyle", null);
        String[] testoDiviso = testo.split("\\.");
        Color c;

        for(int i=0; i<testoDiviso.length-1; i++)
        {
            String s = testoDiviso[i];

            String[] frasiNewLine = s.split("\n");

            if(frasiNewLine.length==1)
            {
                frasiNewLine[0] = s;
            }

            for(int j=0; j<frasiNewLine.length; j++)
            {
                String fraseNewLine = frasiNewLine[j];
                String punto="";

                c = attribuzioneColore(fraseNewLine);

                StyleConstants.setForeground(style, c);

                if(j==frasiNewLine.length-1)
                    punto = ".";

                try {

                    if(c.equals(Color.black))
                        doc.insertString(doc.getLength(), s + punto, style);
                    else
                        doc.insertString(doc.getLength(), s.split("##")[0] + punto , style);
                } catch (BadLocationException e) {
                    e.printStackTrace();
                }




            }
        }
    }

    public Color attribuzioneColore(String s)
    {
        Color c;

        if(s.contains("##i"))
        {
            c = Color.blue;

        } else if (s.contains("##m")) {
            c = Color.green;

        } else if (s.contains("##c")) {
            c = Color.red;

        } else {
            c = Color.black;
        }

        return c;
    }
}
